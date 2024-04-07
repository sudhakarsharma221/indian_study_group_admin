package com.indiastudygroupadmin.addLibrary.ui

import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.addLibrary.model.AddLibraryRequestModel
import com.indiastudygroupadmin.addLibrary.model.AddTimingsDataClass
import com.indiastudygroupadmin.addLibrary.model.Address
import com.indiastudygroupadmin.addLibrary.ui.adapter.TimingAdapter
import com.indiastudygroupadmin.addLibrary.viewModel.AddLibraryViewModel
import com.indiastudygroupadmin.app_utils.TimePickerCustomDialog
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.databinding.ActivityAddLibraryBinding
import com.indiastudygroupadmin.databinding.AddTimingBottomDialogBinding
import com.indiastudygroupadmin.pincode.PincodeViewModel
import okhttp3.internal.notify
import java.io.File

class AddLibraryActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var photoUrl: String? = ""
    private var storageRef = Firebase.storage
    private val timingsList = ArrayList<AddTimingsDataClass>()
    private var uri: Uri? = null
    lateinit var imageUri: Uri
    private val contract =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                binding.libImage.visibility = View.VISIBLE
                binding.libImage.setImageURI(imageUri)
                uri = imageUri
                uploadImage()

            } else {
                binding.libImage.setImageURI(uri)
                uri = null
            }
        }

    private val requestForPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                contract.launch(imageUri)
            } else {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                    showRationaleDialog()
                } else {
                    val message =
                        "You've denied camera permission twice. To enable it, open app settings."
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
        }
    lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>


    private lateinit var binding: ActivityAddLibraryBinding
    private var selectedDays: ArrayList<String>? = arrayListOf()
    private lateinit var adapter: TimingAdapter
    lateinit var pincodeViewModel: PincodeViewModel
    lateinit var addLibViewModel: AddLibraryViewModel
    private var selectedFacilities: ArrayList<String>? = arrayListOf()

    private val daysList =
        arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    private val facilitiesList = arrayOf(
        "AC",
        "Books",
        "Water",
        "Study Spaces",
        "Computers",
        "Wi-Fi",
        "Restrooms",
        "Printing Services",
        "Reference Materials",
        "Charging Stations",
        "Group Study Rooms"
    )
    private val checkedFacilities =
        BooleanArray(facilitiesList.size) // Initialize with the same length as topicsList

    val checkedDays = BooleanArray(daysList.size)
    lateinit var district: String
    lateinit var state: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pincodeViewModel = ViewModelProvider(this)[PincodeViewModel::class.java]
        addLibViewModel = ViewModelProvider(this)[AddLibraryViewModel::class.java]
        window.statusBarColor = Color.parseColor("#2f3133")
        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance()
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.libImage.visibility = View.VISIBLE
                binding.libImage.setImageURI(uri)
                this.uri = uri
                uploadImage()

            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
        imageUri = createImageUri()!!


        initListener()
        focusChangeListeners()
        observerPincodeApiResponse()
        observerAddLibApiResponse()
        observeProgress()
        observerErrorMessageApiResponse()

    }


    private fun initListener() {
        binding.tvTimings.setOnClickListener {
            binding.tvchooseTimings.visibility = View.GONE
            showAddTimingsBottomDialog()
        }

        binding.tvFacilities.setOnClickListener {
            chooseFacilitiesDialog()
        }
        binding.tvAddPhoto.setOnClickListener {
            chooseImage()
        }

        binding.pincodeEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used, but needs to be implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used, but needs to be implemented
            }

            override fun afterTextChanged(s: Editable?) {
                val pincode = s.toString().trim()
                if (pincode.length == 6) {
                    callPincodeApi(pincode)
                }
            }
        })


        binding.addButton.setOnClickListener {

            val libName = binding.libNameEt.text.toString()
            val phoneNo = binding.phoneEt.text.toString()
            val address = binding.addressEt.text.toString()
            val pincode = binding.pincodeEt.text.toString()
            val state = binding.tvState.text.toString()
            val district = binding.tvCity.text.toString()
            val bio = binding.bioET.text.toString()
            val seats = binding.seatsET.text.toString()
            val dailyCharge = binding.dailyChargeET.text.toString()
            val monthlyCharge = binding.monthlyChargeET.text.toString()


            if (libName.trim().isEmpty()) {
                binding.libNameEt.error = "Empty Field"
            } else if (libName.length < 2) {
                binding.libNameEt.error = "Enter minimum 2 characters"
            } else if (libName.length > 100) {
                binding.libNameEt.error = "Enter less than 100 characters"
            } else if (phoneNo.trim().isEmpty()) {
                binding.phoneEt.error = "Empty Field"
            } else if (phoneNo.length < 10) {
                binding.phoneEt.error = "Enter Valid Mobile Number"
            } else if (address.trim().isEmpty()) {
                binding.addressEt.error = "Empty Field"
            } else if (address.length < 3) {
                binding.addressEt.error = "Enter minimum 3 characters"
            } else if (address.length > 100) {
                binding.addressEt.error = "Enter less than 100 characters"
            } else if (pincode.trim().isEmpty()) {
                binding.pincodeEt.error = "Empty Field"
            } else if (pincode.length < 6) {
                binding.pincodeEt.error = "Enter Valid Pincode"
            } else if (seats.trim().isEmpty()) {
                binding.seatsET.error = "Empty Field"
            } else if (monthlyCharge.trim().isEmpty()) {
                binding.monthlyChargeET.error = "Empty Field"
            } else if (dailyCharge.trim().isEmpty()) {
                binding.dailyChargeET.error = "Empty Field"
            } else if (timingsList.isEmpty()) {
                binding.tvchooseTimings.visibility = View.VISIBLE
            } else {
                Log.d("PHOTOURL", photoUrl.toString())
                callAddLib(
                    auth.currentUser!!.uid, AddLibraryRequestModel(
                        libName,
                        auth.currentUser!!.uid,
                        phoneNo,
                        seats.toInt(),
                        bio,
                        photoUrl,
                        selectedFacilities,
                        Address(address, pincode, district, state)
                    )
                )
            }
        }

    }


    private fun showAddTimingsBottomDialog() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = TimingAdapter(this, timingsList)
        binding.recyclerView.adapter = adapter


        val bottomDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val dialogBinding = AddTimingBottomDialogBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(true)
        bottomDialog.show()

        dialogBinding.addDaysButton.setOnClickListener {
            dialogBinding.addDaysButton.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#4E4F50"))
            selectDaysDialog(dialogBinding.tvDays)
        }
        dialogBinding.fromText.setOnClickListener {
            dialogBinding.fromText.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#4E4F50"))
            val timePicker = TimePickerCustomDialog(dialogBinding.fromText)
            timePicker.show(supportFragmentManager, "timePicker")

        }
        dialogBinding.toText.setOnClickListener {
            dialogBinding.toText.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#4E4F50"))
            val timePicker = TimePickerCustomDialog(dialogBinding.toText)
            timePicker.show(supportFragmentManager, "timePicker")

        }
        dialogBinding.confirmButton.setOnClickListener {
            val from = dialogBinding.fromText.text.toString()
            val to = dialogBinding.toText.text.toString()

            if (from == "Select") {
                dialogBinding.fromText.backgroundTintList = ColorStateList.valueOf(Color.RED)
            } else if (to == "Select") {
                dialogBinding.toText.backgroundTintList = ColorStateList.valueOf(Color.RED)
            } else if (dialogBinding.tvDays.text.toString().trim().isEmpty()) {
                dialogBinding.addDaysButton.backgroundTintList = ColorStateList.valueOf(Color.RED)
            } else {
                val tempSelectedDays = ArrayList<String>(selectedDays ?: emptyList())
                // Clear the list of selected days
                selectedDays?.clear()
                val model = AddTimingsDataClass(tempSelectedDays, from, to)
                timingsList.add(model) // Add the timings to the ArrayList
                binding.recyclerView.visibility = View.VISIBLE
                adapter.notifyDataSetChanged() // Notify the RecyclerView adapter
                bottomDialog.dismiss()
            }
        }
    }

    private fun uploadImage() {
        if (uri == null) {
            Toast.makeText(this, "Please choose an image first", Toast.LENGTH_SHORT).show()
            return
        }

        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Wait while uploading photo")
        progressDialog.show()

        val imageRef = storageRef.reference.child("library")
            .child("${System.currentTimeMillis()} ${auth.currentUser!!.uid}")
        imageRef.putFile(uri!!).addOnSuccessListener { task ->
            task.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                photoUrl = uri.toString()
                binding.libImage.visibility = View.VISIBLE
                progressDialog.dismiss()
                Toast.makeText(
                    this, "Image Uploaded", Toast.LENGTH_SHORT
                ).show()

                // Call function to add library here or update UI accordingly
            }?.addOnFailureListener {
                Toast.makeText(
                    this, "Error: ${it.message}", Toast.LENGTH_SHORT
                ).show()
                progressDialog.dismiss()
            }
        }.addOnFailureListener {
            Toast.makeText(
                this, "Error: ${it.message}", Toast.LENGTH_SHORT
            ).show()
            progressDialog.dismiss()
        }
    }


    private fun chooseImage() {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.media_choose_bottom_dialog, null)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        val gallery = view.findViewById<TextView>(R.id.chooseGallery)
        gallery.setOnClickListener {
            dialog.dismiss()
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }
        val camera = view.findViewById<TextView>(R.id.chooseCamera)
        camera.setOnClickListener {
            dialog.dismiss()
            if (checkPermission()) {
                contract.launch(imageUri)
            } else {
                requestForPermission.launch(android.Manifest.permission.CAMERA)
            }

        }
    }

    private fun showRationaleDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Camera Permission")
            .setMessage("This app requires camera permission to take profile photos. If you deny this time you have to manually go to app setting to allow permission.")
            .setPositiveButton("Ok") { _, _ ->
                requestForPermission.launch(android.Manifest.permission.CAMERA)
            }
        builder.create().show()
    }

    private fun checkPermission(): Boolean {
        val permission = android.Manifest.permission.CAMERA
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createImageUri(): Uri? {
        val image = File(this.filesDir, "library_photos.png")
        return FileProvider.getUriForFile(
            this, "com.indiastudygroupadmin.libraryFileProvider", image
        )
    }


    private fun chooseFacilitiesDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Items").setMultiChoiceItems(
            facilitiesList, checkedFacilities
        ) { dialog, which, isChecked ->
            checkedFacilities[which] = isChecked
        }.setPositiveButton("OK") { dialog, which ->
            // Check if none of the options is selected
            if (checkedFacilities.all { !it }) {
                Toast.makeText(
                    applicationContext, "Please select at least one item", Toast.LENGTH_SHORT
                ).show()
            } else {
                // User clicked OK and at least one item is selected
                for (i in checkedFacilities.indices) {
                    if (checkedFacilities[i]) {
                        selectedFacilities?.add(facilitiesList[i])
                    }
                }
            }
        }.setNegativeButton("Cancel") { dialog, which ->
            // User cancelled the dialog
            dialog.dismiss()
        }.setNeutralButton("Clear All") { dialog, which ->
            // Clear all selections
            checkedFacilities.fill(false)
        }
        // Create and show the AlertDialog
        val dialog = builder.create()
        dialog.show()
    }


    private fun selectDaysDialog(textView: TextView) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Days").setMultiChoiceItems(
            daysList, checkedDays
        ) { dialog, which, isChecked ->
            checkedDays[which] = isChecked
        }.setPositiveButton("OK") { dialog, which ->
            // Check if none of the options is selected
            if (checkedDays.all { !it }) {
                Toast.makeText(
                    applicationContext, "Please select at least one item", Toast.LENGTH_SHORT
                ).show()
            } else {
                val selectedDaysText = StringBuilder()
                // User clicked OK and at least one item is selected
                for (i in checkedDays.indices) {
                    if (checkedDays[i]) {
                        selectedDays?.add(daysList[i])
                        selectedDaysText.append(daysList[i]).append("\n")
                    }
                }
                textView.text = selectedDaysText.toString()
            }
        }.setNegativeButton("Cancel") { dialog, which ->
            // User cancelled the dialog
            dialog.dismiss()
        }.setNeutralButton("Clear All") { dialog, which ->
            // Clear all selections
            checkedDays.fill(false)
        }
        // Create and show the AlertDialog
        val dialog = builder.create()
        dialog.show()
    }


    private fun focusChangeListeners() {

        binding.addressEt.setOnFocusChangeListener { view, b ->
            if (!b) {
                if (binding.addressEt.text.toString().trim()
                        .isNotEmpty() && binding.addressEt.text.toString().length < 3
                ) {
                    binding.addressEt.error = "Enter Minimum 3 Characters"
                } else if (binding.addressEt.text.toString().trim()
                        .isNotEmpty() && binding.addressEt.text.toString().length > 100
                ) {
                    binding.addressEt.error = "Enter Less Than 100 Characters"

                }
            }
        }



        binding.libNameEt.setOnFocusChangeListener { view, b ->
            if (!b) {
                if (binding.libNameEt.text.toString().trim()
                        .isNotEmpty() && binding.libNameEt.text.toString().length < 2
                ) {
                    binding.libNameEt.error = "Enter Minimum 2 Characters"
                } else if (binding.libNameEt.text.toString().trim()
                        .isNotEmpty() && binding.libNameEt.text.toString().length > 100
                ) {
                    binding.libNameEt.error = "Enter Less Than 100 Characters"

                }
            }
        }
        binding.seatsET.setOnFocusChangeListener { view, b ->
            if (!b) {
                if (binding.seatsET.text.toString().trim()
                        .isNotEmpty() && binding.seatsET.text.toString().length < 2
                ) {
                    binding.seatsET.error = "Enter Minimum 2 Characters"
                }
            }
        }


        binding.pincodeEt.setOnFocusChangeListener { view, b ->
            if (!b) {
                if (binding.pincodeEt.text.toString().trim()
                        .isNotEmpty() && binding.pincodeEt.text.toString().length < 6
                ) {
                    binding.pincodeEt.error = "Enter valid pincode"
                }
            }
        }

    }

    private fun callPincodeApi(pincode: String?) {
        pincodeViewModel.callPinCodeDetails(pincode)
    }

    private fun callAddLib(userId: String?, addLibraryRequestModel: AddLibraryRequestModel) {
        addLibViewModel.callAddLibrary(userId, addLibraryRequestModel)
    }

    private fun observeProgress() {
        addLibViewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.mainView.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.mainView.visibility = View.VISIBLE
            }
        })


        pincodeViewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.mainView.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.mainView.visibility = View.VISIBLE
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        addLibViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
        pincodeViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
    }


    private fun observerAddLibApiResponse() {
        addLibViewModel.addLibraryResponse.observe(this, Observer {
            Log.d("addLibraryResponseFromObserve", it.toString())
            ToastUtil.makeToast(this, "Library Added")
            finish()
        })
    }

    private fun observerPincodeApiResponse() {
        pincodeViewModel.pincodeResponse.observe(this, Observer {
            Log.d("testPINCODEAPI", it.toString())
            if (it[0].postOffice == null) {
                ToastUtil.makeToast(this, "Please enter valid pincode")

            } else {
                state = it[0].postOffice?.get(0)?.state!!
                district = it[0].postOffice?.get(0)?.district!!
                binding.tvCity.text = district
                binding.tvState.text = state
            }

        })
    }

}