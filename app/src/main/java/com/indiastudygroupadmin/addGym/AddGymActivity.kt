package com.indiastudygroupadmin.addGym

import android.app.Dialog
import android.app.ProgressDialog
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.addLibrary.adapter.ImageAdapter
import com.indiastudygroupadmin.app_utils.HideKeyboard
import com.indiastudygroupadmin.app_utils.TimePickerCustomDialog
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.bottom_nav_bar.library.model.AmenityItem
import com.indiastudygroupadmin.bottom_nav_bar.library.ui.adapter.AmenitiesAdapter
import com.indiastudygroupadmin.databinding.ActivityAddGymBinding
import com.indiastudygroupadmin.databinding.AddTimingBottomDialogBinding
import com.indiastudygroupadmin.databinding.ErrorBottomDialogLayoutBinding
import com.indiastudygroupadmin.email.EmailRequestModel
import com.indiastudygroupadmin.email.EmailViewModel
import com.indiastudygroupadmin.email.From
import com.indiastudygroupadmin.email.To
import com.indiastudygroupadmin.pincode.PinCodeViewModel

class AddGymActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddGymBinding
    private lateinit var auth: FirebaseAuth
    private var morningButton = false
    private var afternoonButton = false
    private var eveningButton = false
    private var storageRef = Firebase.storage
    private lateinit var emailViewModel: EmailViewModel
    private var imagesUriList = ArrayList<Uri>()
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var adapter: ImageAdapter
    private lateinit var pinCodeViewModel: PinCodeViewModel
    private var selectedEquipments: ArrayList<String>? = arrayListOf()


    private val equipmentMappings = mapOf(
        "Treadmill" to Pair("Treadmill", R.drawable.treadmill),
        "Spin Bike" to Pair("Spin Bike", R.drawable.spinbike),
        "Dumbbell" to Pair("Dumbbell", R.drawable.dumbbell),
        "Multi Press" to Pair("Multi Press", R.drawable.multipress),
        "Benches" to Pair("Benches", R.drawable.benches),
        "Leg Press" to Pair("Leg Press", R.drawable.legpress),
        "Extension" to Pair("Extension", R.drawable.extension),
        "Punching Bag" to Pair("Punching Bag", R.drawable.punching_bag),
        "Smith Machine" to Pair("Smith Machine", R.drawable.smithmachine),
        "Elliptical" to Pair("Elliptical", R.drawable.elliptical),
        "Standing Abductor" to Pair("Standing Abductor", R.drawable.standingabductor),
        "Cable" to Pair("Cable", R.drawable.cable),
        "Hack Squat" to Pair("Hack Squat", R.drawable.hacksquat),
        "Pec Fly" to Pair("Pec Fly", R.drawable.packfly),
        "Dip" to Pair("Dip", R.drawable.dip),
        "Lat Pull" to Pair("Lat Pull", R.drawable.letpull),
        "Preacher Curl" to Pair("Preacher Curl", R.drawable.preacher),
        "Exercise" to Pair("Exercise", R.drawable.preacher),
        "Hammer & Tyre" to Pair("Hammer & Tyre", R.drawable.hammer)
    )


    private val equipmentsList = arrayOf(
        "Treadmill",
        "Spin Bike",
        "Dumbbell",
        "Multi Press",
        "Benches",
        "Leg Press",
        "Extension",
        "Punching Bag",
        "Smith Machine",
        "Elliptical",
        "Standing Abductor",
        "Cable",
        "Hack Squat",
        "Pec Fly",
        "Dip",
        "Lat Pull",
        "Preacher Curl",
        "Exercise",
        "Hammer & Tyre"
    )


    private val checkedEquipments = BooleanArray(equipmentsList.size)


    private var selectedFacilities: ArrayList<String>? = arrayListOf()
    private val amenityMappings = mapOf(
        "AC" to Pair("Air Conditioning", R.drawable.ac),
        "Water Dispenser" to Pair("Water Dispenser", R.drawable.water),
        "Coffee Machine" to Pair("Coffee Machine", R.drawable.coffee),
        "Parking Space" to Pair("Parking Space", R.drawable.parking),
        "Trainer" to Pair("Trainer", R.drawable.personal_trainer),
        "Cardio Equipment" to Pair("Cardio Equipment", R.drawable.cardio),
        "Canteen" to Pair("Canteen", R.drawable.canteen),
        "Music System" to Pair("Music System", R.drawable.music),
        "Washroom" to Pair("Washroom", R.drawable.washroom),
        "Locker Room" to Pair("Locker Room", R.drawable.locker),
        "Changing Room" to Pair("Changing Room", R.drawable.changing_room),
        "Supplements" to Pair("Supplements", R.drawable.supplement_bottle),
        "Wifi" to Pair("Wi-Fi", R.drawable.wifi)
    )


    private val amenitiesList = arrayOf(
        "AC",
        "Water Dispenser",
        "Coffee Machine",
        "Parking Space",
        "Trainer",
        "Cardio Equipment",
        "Canteen",
        "Music System",
        "Washroom",
        "Locker Room",
        "Changing Room",
        "Supplements",
        "Wifi"
    )


    private val checkedFacilities =
        BooleanArray(amenitiesList.size) // Initialize with the same length as topicsList

    lateinit var district: String
    lateinit var state: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGymBinding.inflate(layoutInflater)
        emailViewModel = ViewModelProvider(this)[EmailViewModel::class.java]
        setContentView(binding.root)
        pinCodeViewModel = ViewModelProvider(this)[PinCodeViewModel::class.java]
        window.statusBarColor = Color.WHITE
        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ImageAdapter(this, imagesUriList)
        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uri ->
                if (uri != null) {
                    this.imagesUriList.addAll(uri)
                    adapter = ImageAdapter(this@AddGymActivity, imagesUriList)
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
        observerEmailApiResponse()

    }


    private fun initListener() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.amenitiesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.equipmentsRecyclerView.layoutManager = LinearLayoutManager(this)


        binding.buttonSlot1.setOnClickListener {
            morningButton = true
            binding.requireTime.visibility = View.GONE
            showAddTimingsBottomDialog()
        }
        binding.buttonSlot2.setOnClickListener {
            afternoonButton = true
            binding.requireTime.visibility = View.GONE
            showAddTimingsBottomDialog()
        }
        binding.buttonSlot3.setOnClickListener {
            eveningButton = true
            binding.requireTime.visibility = View.GONE
            showAddTimingsBottomDialog()
        }

        binding.amenitiesButton.setOnClickListener {
            chooseFacilitiesDialog()
        }
        binding.equipmentsButton.setOnClickListener {
            chooseEquipmentsDialog()
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
                val pinCode = s.toString().trim()
                if (pinCode.length == 6) {
                    HideKeyboard.hideKeyboard(
                        this@AddGymActivity, binding.pincodeEt.windowToken
                    )
                    callPincodeApi(pinCode)
                }
            }
        })


        binding.sendRequestButton.setOnClickListener {

            val gymName = binding.gymNameEt.text.toString()
            val phoneNumber = binding.gymPhoneEt.text.toString()
            val coaches = binding.coachesEt.text.toString()
            val fees = binding.feesEt.text.toString()
            val address = binding.addressEt.text.toString()
            val pinCode = binding.pincodeEt.text.toString()
            val state = binding.tvState.text.toString()
            val district = binding.tvCity.text.toString()
            val owner = binding.ownerEt.text.toString()
            val bio = binding.bioEt.text.toString()
            val buttonSlot1 = binding.buttonSlot1.text.toString()
            val buttonSlot2 = binding.buttonSlot2.text.toString()
            val buttonSlot3 = binding.buttonSlot3.text.toString()



            if (gymName.trim().isEmpty()) {
                binding.gymNameEt.error = "Empty Field"
            } else if (gymName.length < 2) {
                binding.gymNameEt.error = "Enter minimum 2 characters"
            } else if (gymName.length > 100) {
                binding.gymNameEt.error = "Enter less than 100 characters"
            } else if (phoneNumber.trim().isEmpty()) {
                binding.gymPhoneEt.error = "Empty Field"
            } else if (phoneNumber.length < 10) {
                binding.gymPhoneEt.error = "Please Enter Valid Mobile Number"
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
            } else if (coaches.trim().isEmpty()) {
                binding.coachesEt.error = "Empty Field"
            } else if (fees.trim().isEmpty()) {
                binding.feesEt.error = "Empty Field"
            } else if (owner.trim().isEmpty()) {
                binding.ownerEt.error = "Empty Field"
            } else if (owner.length < 2) {
                binding.ownerEt.error = "Enter minimum 2 characters"
            } else if (owner.length > 30) {
                binding.ownerEt.error = "Enter less than 30 characters"
            } else if (buttonSlot1 == "Set Slot 1 Time" && buttonSlot2 == "Set Slot 2 Time" && buttonSlot3 == "Set Slot 3 Time") {
                binding.requireTime.visibility = View.VISIBLE
            } else if (imagesUriList.size > 5) {
                ToastUtil.makeToast(this, "Select Maximum Of 5 Images")
            } else {
                if (imagesUriList.isNotEmpty()) {
                    uploadImage(
                        gymName,
                        phoneNumber,
                        address,
                        pinCode,
                        state,
                        district,
                        coaches,
                        fees,
                        owner,
                        bio,
                        buttonSlot1,
                        buttonSlot2,
                        buttonSlot3
                    )
                } else {
                    sendLibraryEmail(
                        gymName,
                        phoneNumber,
                        address,
                        pinCode,
                        state,
                        district,
                        coaches,
                        fees,
                        owner,
                        bio,
                        buttonSlot1,
                        buttonSlot2,
                        buttonSlot3,
                        arrayListOf()
                    )
                }

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
                    binding.buttonSlot1.text = "$from to $to"
                    morningButton = false
                } else if (afternoonButton) {
                    binding.buttonSlot2.text = "$from to $to"
                    afternoonButton = false
                } else if (eveningButton) {
                    binding.buttonSlot3.text = "$from to $to"
                    eveningButton = false
                }

                bottomDialog.dismiss()
            }
        }
    }


    private fun uploadImage(
        gymName: String,
        phoneNumber: String,
        address: String,
        pinCode: String,
        state: String,
        district: String,
        coaches: String,
        fees: String,
        owner: String,
        bio: String,
        buttonSlot1: String,
        buttonSlot2: String,
        buttonSlot3: String
    ) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Wait while uploading photos")
        progressDialog.show()

        val totalImages = imagesUriList.size
        var imagesUploaded = 0
        val uploadedImageUrls = arrayListOf<String>()

        imagesUriList.forEach { uri ->
            val imageRef = storageRef.reference.child("library")
                .child("${System.currentTimeMillis()} ${auth.currentUser!!.uid}")

            imageRef.putFile(uri).addOnSuccessListener { task ->
                task.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    uploadedImageUrls.add(uri.toString())
                    imagesUploaded++
                    if (imagesUploaded == totalImages) {
                        progressDialog.dismiss()
                        Toast.makeText(this, "All Images Uploaded", Toast.LENGTH_SHORT).show()
                        sendLibraryEmail(
                            gymName,
                            phoneNumber,
                            address,
                            pinCode,
                            state,
                            district,
                            coaches,
                            fees,
                            owner,
                            bio,
                            buttonSlot1,
                            buttonSlot2,
                            buttonSlot3,
                            uploadedImageUrls
                        )
                    }
                }?.addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendLibraryEmail(
        gymName: String,
        gymPhone: String,
        address: String,
        pinCode: String,
        state: String,
        district: String,
        coaches: String,
        fees: String,
        owner: String,
        bio: String,
        buttonSlot1: String,
        buttonSlot2: String,
        buttonSlot3: String,
        photoUrlList: List<String>
    ) {
        val body = """
        User Id       : ${auth.currentUser!!.uid}    
        Gym Name      : $gymName
        Gym Phone     : $gymPhone
        Address       : $address
        Pin Code      : $pinCode
        State         : $state
        District      : $district
        Coaches       : $coaches
        Fees          : $fees
        Owner         : $owner
        Amenities     : $selectedFacilities
        Equipments    : $selectedEquipments
        Bio           : $bio
        Slot 1 Time   : $buttonSlot1
        Slot 2 Time   : $buttonSlot2
        Slot 3 Time   : $buttonSlot3
        Images List   : $photoUrlList
    """.trimIndent()

        callSendEmail(
            EmailRequestModel(
                "Request To Add Gym",
                From("indian.study.group@demomailtrap.com", "Library Add Gym"),
                "Gym Add Request",
                body,
                listOf(To("indianstudygroup1@gmail.com"))
            )
        )
    }


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


    private fun chooseEquipmentsDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Items").setMultiChoiceItems(
            equipmentsList, checkedEquipments
        ) { dialog, which, isChecked ->
            checkedEquipments[which] = isChecked
        }.setPositiveButton("OK") { dialog, which ->
            // Check if none of the options is selected
            if (checkedEquipments.all { !it }) {
                Toast.makeText(
                    applicationContext, "Please select at least one item", Toast.LENGTH_SHORT
                ).show()
            } else {
                // User clicked OK and at least one item is selected
                selectedEquipments?.clear()
                for (i in checkedEquipments.indices) {
                    if (checkedEquipments[i]) {
                        selectedEquipments?.add(equipmentsList[i])
                    }
                }
                if (selectedEquipments?.isNotEmpty() == true) {
                    val allAmenities =
                        getEquipmentWithDrawable(selectedEquipments!!.distinct(), equipmentMappings)
                    val adapter = AmenitiesAdapter(this, allAmenities)
                    binding.equipmentsRecyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }
        }.setNegativeButton("Cancel") { dialog, which ->
            // User cancelled the dialog
            dialog.dismiss()
        }.setNeutralButton("Clear All") { dialog, which ->
            // Clear all selections
            checkedEquipments.fill(false)
        }
        // Create and show the AlertDialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun getEquipmentWithDrawable(
        equipments: List<String>?, equipmentMappings: Map<String, Pair<String, Int>>
    ): List<AmenityItem> {
        val equipmentsItems = mutableListOf<AmenityItem>()

        if (equipments == null) {
            return equipmentsItems
        }

        equipments.forEach { equipmentsId ->
            val equipmentData = equipmentMappings[equipmentsId]
            if (equipmentData != null) {
                val (label, drawableResId) = equipmentData
                val drawable = ContextCompat.getDrawable(this, drawableResId)
                if (drawable != null) {
                    equipmentsItems.add(AmenityItem(label, drawable))
                }
            }
        }

        return equipmentsItems
    }


    private fun chooseFacilitiesDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Items").setMultiChoiceItems(
            amenitiesList, checkedFacilities
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
                selectedFacilities?.clear()
                for (i in checkedFacilities.indices) {
                    if (checkedFacilities[i]) {
                        selectedFacilities?.add(amenitiesList[i])
                    }
                }
                if (selectedFacilities?.isNotEmpty() == true) {
                    val allAmenities =
                        getAmenitiesWithDrawable(selectedFacilities!!.distinct(), amenityMappings)
                    val adapter = AmenitiesAdapter(this, allAmenities)
                    binding.amenitiesRecyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
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

    private fun getAmenitiesWithDrawable(
        amenities: List<String>?, amenityMappings: Map<String, Pair<String, Int>>
    ): List<AmenityItem> {
        val amenityItems = mutableListOf<AmenityItem>()

        if (amenities == null) {
            return amenityItems
        }

        amenities.forEach { amenityId ->
            val amenityData = amenityMappings[amenityId]
            if (amenityData != null) {
                val (label, drawableResId) = amenityData
                val drawable = ContextCompat.getDrawable(this, drawableResId)
                if (drawable != null) {
                    amenityItems.add(AmenityItem(label, drawable))
                }
            }
        }

        return amenityItems
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
        binding.gymPhoneEt.setOnFocusChangeListener { view, b ->
            if (!b) {
                if (binding.gymPhoneEt.text.toString().trim()
                        .isNotEmpty() && binding.gymPhoneEt.text.toString().length < 10
                ) {
                    binding.gymPhoneEt.error = "Enter Valid Phone No"
                }
            }
        }

        binding.gymNameEt.setOnFocusChangeListener { view, b ->
            if (!b) {
                if (binding.gymNameEt.text.toString().trim()
                        .isNotEmpty() && binding.gymNameEt.text.toString().length < 2
                ) {
                    binding.gymNameEt.error = "Enter Minimum 2 Characters"
                } else if (binding.gymNameEt.text.toString().trim()
                        .isNotEmpty() && binding.gymNameEt.text.toString().length > 100
                ) {
                    binding.gymNameEt.error = "Enter Less Than 100 Characters"

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


    private fun callSendEmail(emailRequestModel: EmailRequestModel) {
        emailViewModel.postEmail(emailRequestModel)
    }

    private fun observeProgress() {
        emailViewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.mainView.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.mainView.visibility = View.VISIBLE
            }
        })


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
        emailViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
        pinCodeViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
    }


    private fun observerEmailApiResponse() {
        emailViewModel.emailResponse.observe(this, Observer {
            showErrorBottomDialog(
                "Thank you for reaching out to us.\nWe have received your email and will be in touch with you shortly."
            )
        })
    }

    private fun showErrorBottomDialog(message: String) {
        val bottomDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val dialogBinding = ErrorBottomDialogLayoutBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(false)
        bottomDialog.show()
        dialogBinding.messageTv.text = message
        dialogBinding.continueButton.setOnClickListener {
            bottomDialog.dismiss()
            finish()
        }
    }

    private fun observerPincodeApiResponse() {
        pinCodeViewModel.pinCodeResponse.observe(this, Observer {
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