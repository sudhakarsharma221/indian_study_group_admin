package com.indiastudygroupadmin.addLibrary.ui

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.addLibrary.ui.adapter.ImageAdapter
import com.indiastudygroupadmin.addLibrary.viewModel.AddLibraryViewModel
import com.indiastudygroupadmin.app_utils.TimePickerCustomDialog
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.databinding.ActivityAddLibraryBinding
import com.indiastudygroupadmin.databinding.AddTimingBottomDialogBinding
import com.indiastudygroupadmin.pincode.PinCodeViewModel

class AddLibraryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddLibraryBinding

    private lateinit var auth: FirebaseAuth
    private var morning: String? = ""
    private var morningButton = false
    private var afternoon: String? = ""
    private var afternoonButton = false
    private var evening: String? = ""
    private var eveningButton = false
    private var storageRef = Firebase.storage

    private var imagesUriList = ArrayList<Uri>()
    private var selectedImageUris = ArrayList<Uri>()

    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    private lateinit var adapter: ImageAdapter
    private lateinit var pinCodeViewModel: PinCodeViewModel
    lateinit var addLibViewModel: AddLibraryViewModel
    private var selectedFacilities: ArrayList<String>? = arrayListOf()

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

    lateinit var district: String
    lateinit var state: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pinCodeViewModel = ViewModelProvider(this)[PinCodeViewModel::class.java]
//        addLibViewModel = ViewModelProvider(this)[AddLibraryViewModel::class.java]
        window.statusBarColor = Color.WHITE
        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ImageAdapter(this, imagesUriList)
        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(3)) { uri ->
                if (uri != null) {
                    this.imagesUriList.addAll(uri)
                    adapter = ImageAdapter(this@AddLibraryActivity, imagesUriList)
                    adapter.notifyDataSetChanged()
                    binding.recyclerView.adapter = adapter

                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }


        initListener()
        focusChangeListeners()
        observerPincodeApiResponse()
        observeProgress()
        observerErrorMessageApiResponse()

    }

    //
    private fun initListener() {
        binding.backButton.setOnClickListener {
            finish()
        }



        binding.buttonMorning.setOnClickListener {
            morningButton = true
            binding.requireTime.visibility = View.GONE
            showAddTimingsBottomDialog()
        }
        binding.buttonAfternoon.setOnClickListener {
            afternoonButton = true
            binding.requireTime.visibility = View.GONE
            showAddTimingsBottomDialog()
        }
        binding.buttonEvening.setOnClickListener {
            eveningButton = true
            binding.requireTime.visibility = View.GONE
            showAddTimingsBottomDialog()
        }

        binding.amenitiesButton.setOnClickListener {
            chooseFacilitiesDialog()
        }
        binding.addImage.setOnClickListener {
            uploadImageDialog()
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


        binding.sendRequestButton.setOnClickListener {

            val libName = binding.libNameEt.text.toString()
            val address = binding.addressEt.text.toString()
            val pinCode = binding.pincodeEt.text.toString()
            val state = binding.tvState.text.toString()
            val district = binding.tvCity.text.toString()
            val seats = binding.seatsEt.text.toString()
            val owner = binding.ownerEt.text.toString()
            val bio = binding.bioEt.text.toString()
            val morning = binding.buttonMorning.text.toString()
            val afternoon = binding.buttonAfternoon.text.toString()
            val evening = binding.buttonEvening.text.toString()



            if (libName.trim().isEmpty()) {
                binding.libNameEt.error = "Empty Field"
            } else if (libName.length < 2) {
                binding.libNameEt.error = "Enter minimum 2 characters"
            } else if (libName.length > 100) {
                binding.libNameEt.error = "Enter less than 100 characters"
            } else if (address.trim().isEmpty()) {
                binding.addressEt.error = "Empty Field"
            } else if (address.length < 3) {
                binding.addressEt.error = "Enter minimum 3 characters"
            } else if (address.length > 100) {
                binding.addressEt.error = "Enter less than 100 characters"
            } else if (pinCode.trim().isEmpty()) {
                binding.pincodeEt.error = "Empty Field"
            } else if (pinCode.length < 6) {
                binding.pincodeEt.error = "Enter Valid Pincode"
            } else if (seats.trim().isEmpty()) {
                binding.seatsEt.error = "Empty Field"
            } else if (owner.trim().isEmpty()) {
                binding.ownerEt.error = "Empty Field"
            } else if (owner.length < 2) {
                binding.ownerEt.error = "Enter minimum 2 characters"
            } else if (owner.length > 30) {
                binding.ownerEt.error = "Enter less than 30 characters"
            } else if (morning == "Set Morning Time" || evening == "Set Evening Time" || afternoon == "Set Afternoon Time") {
                binding.requireTime.visibility = View.VISIBLE
            } else {
                ToastUtil.makeToast(this, "Email Sent")

//                callAddLib(
//                    auth.currentUser!!.uid, AddLibraryRequestModel(
//                        libName,
//                        auth.currentUser!!.uid,
//                        phoneNo,
//                        seats.toInt(),
//                        bio,
//                        photoUrl,
//                        selectedFacilities,
//                        Address(
//                            address,
//                            pincode,
//                            district,
//                            state,
//                            "28.680470694323148",
//                            "77.49292190961113"
//                        ),
//                        Pricing(dailyCharge.toInt(), monthlyCharge.toInt(), weeklyCharge.toInt()),
//                        timingsList
//                    )
//                )
            }
        }
    }


    private fun showAddTimingsBottomDialog() {
        val bottomDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val dialogBinding = AddTimingBottomDialogBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(true)
        bottomDialog.show()

        dialogBinding.fromText.setOnClickListener {
            dialogBinding.fromText.setBackgroundResource(R.drawable.edit_text_background_3)
            val timePicker = TimePickerCustomDialog(dialogBinding.fromText)
            timePicker.show(supportFragmentManager, "timePicker")

        }
        dialogBinding.toText.setOnClickListener {
            dialogBinding.toText.setBackgroundResource(R.drawable.edit_text_background_3)
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
            } else {
                if (morningButton) {
                    binding.buttonMorning.text = "$from to $to"
                    morningButton = false
                } else if (afternoonButton) {
                    binding.buttonAfternoon.text = "$from to $to"
                    afternoonButton = false
                } else if (eveningButton) {
                    binding.buttonEvening.text = "$from to $to"
                    eveningButton = false
                }

                bottomDialog.dismiss()
            }
        }
    }


//
//    private fun uploadImage() {
//        if (uri == null) {
//            Toast.makeText(this, "Please choose an image first", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val progressDialog = ProgressDialog(this)
//        progressDialog.setCancelable(false)
//        progressDialog.setMessage("Wait while uploading photo")
//        progressDialog.show()
//
//        val imageRef = storageRef.reference.child("library")
//            .child("${System.currentTimeMillis()} ${auth.currentUser!!.uid}")
//        imageRef.putFile(uri!!).addOnSuccessListener { task ->
//            task.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
//                photoUrl = uri.toString()
//                binding.libImage.visibility = View.VISIBLE
//                progressDialog.dismiss()
//                Toast.makeText(
//                    this, "Image Uploaded", Toast.LENGTH_SHORT
//                ).show()
//
//                // Call function to add library here or update UI accordingly
//            }?.addOnFailureListener {
//                Toast.makeText(
//                    this, "Error: ${it.message}", Toast.LENGTH_SHORT
//                ).show()
//                progressDialog.dismiss()
//            }
//        }.addOnFailureListener {
//            Toast.makeText(
//                this, "Error: ${it.message}", Toast.LENGTH_SHORT
//            ).show()
//            progressDialog.dismiss()
//        }
//    }
//
//


    private fun uploadImageDialog() {
        val builder = Dialog(this)
        val view = layoutInflater.inflate(R.layout.upload_image_dialog, null)
        builder.setContentView(view)
        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)
        builder.show()
        builder.setCancelable(true)
        val upload = view.findViewById<ImageView>(R.id.upload)
        val browse = view.findViewById<TextView>(R.id.browseButton)
        upload.setOnClickListener {
            builder.dismiss()
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        browse.setOnClickListener {
            builder.dismiss()
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
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
        binding.ownerEt.setOnFocusChangeListener { view, b ->
            if (!b) {
                if (binding.ownerEt.text.toString().trim()
                        .isNotEmpty() && binding.ownerEt.text.toString().length < 2
                ) {
                    binding.ownerEt.error = "Enter Minimum 2 Characters"
                } else if (binding.ownerEt.text.toString().trim()
                        .isNotEmpty() && binding.ownerEt.text.toString().length > 30
                ) {
                    binding.ownerEt.error = "Enter Less Than 30 Characters"

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
        pinCodeViewModel.callPinCodeDetails(pincode)
    }

    //
//    private fun callAddLib(userId: String?, addLibraryRequestModel: AddLibraryRequestModel) {
//        addLibViewModel.callAddLibrary(userId, addLibraryRequestModel)
//    }
//
    private fun observeProgress() {
//        addLibViewModel.showProgress.observe(this, Observer {
//            if (it) {
//                binding.progressBar.visibility = View.VISIBLE
//                binding.mainView.visibility = View.GONE
//            } else {
//                binding.progressBar.visibility = View.GONE
//                binding.mainView.visibility = View.VISIBLE
//            }
//        })


        pinCodeViewModel.showProgress.observe(this, Observer {
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
//        addLibViewModel.errorMessage.observe(this, Observer {
//            ToastUtil.makeToast(this, it)
//        })
        pinCodeViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
    }


//    private fun observerAddLibApiResponse() {
//        addLibViewModel.addLibraryResponse.observe(this, Observer {
//            Log.d("addLibraryResponseFromObserve", it.toString())
//            ToastUtil.makeToast(this, "Library Added")
//            finish()
//        })
//    }

    private fun observerPincodeApiResponse() {
        pinCodeViewModel.pinCodeResponse.observe(this, Observer {
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