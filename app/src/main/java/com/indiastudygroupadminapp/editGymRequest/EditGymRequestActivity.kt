package com.indiastudygroupadminapp.editGymRequest

import android.app.Dialog
import android.app.ProgressDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.indiastudygroupadminapp.R
import com.indiastudygroupadminapp.addLibrary.adapter.ImageAdapter
import com.indiastudygroupadminapp.app_utils.TimePickerCustomDialog
import com.indiastudygroupadminapp.app_utils.ToastUtil
import com.indiastudygroupadminapp.bottom_nav_bar.gym.model.GymResponseItem
import com.indiastudygroupadminapp.bottom_nav_bar.library.model.AmenityItem
import com.indiastudygroupadminapp.bottom_nav_bar.library.ui.adapter.AmenitiesAdapter
import com.indiastudygroupadminapp.databinding.ActivityEditGymRequestBinding
import com.indiastudygroupadminapp.databinding.AddTimingBottomDialogBinding
import com.indiastudygroupadminapp.databinding.ErrorBottomDialogLayoutBinding
import com.indiastudygroupadminapp.email.EmailRequestModel
import com.indiastudygroupadminapp.email.EmailViewModel
import com.indiastudygroupadminapp.email.From
import com.indiastudygroupadminapp.email.To

class EditGymRequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditGymRequestBinding
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var auth: FirebaseAuth
    private lateinit var gymData: GymResponseItem
    private var morningButton = false
    private var afternoonButton = false
    private var eveningButton = false
    private var uri: Uri? = null
    private var storageRef = Firebase.storage
    private lateinit var emailViewModel: EmailViewModel
    private var imagesUriList = ArrayList<Uri>()
    private lateinit var adapter: ImageAdapter
    private var itemsCount = 5
    private var selectedEquipments: ArrayList<String>? = arrayListOf()




    private val equipmentMappings = mapOf(
        "Treadmill" to Pair("Treadmill", R.drawable.treadmill),
        "SpinBike" to Pair("Spin Bike", R.drawable.spinbike),
        "Dumbbell" to Pair("Dumbbell", R.drawable.dumbbell),
        "Multipress" to Pair("Multipress", R.drawable.multipress),
        "Benches" to Pair("Benches", R.drawable.benches),
        "Legpress" to Pair("Leg Press", R.drawable.legpress),
        "Extension" to Pair("Extension", R.drawable.extension),
        "Punchingbag" to Pair("Punching Bag", R.drawable.punching_bag),
        "SmithMachine" to Pair("Smith Machine", R.drawable.smithmachine),
        "Elliptical" to Pair("Elliptical", R.drawable.elliptical),
        "StandingAbductor" to Pair("Standing Abductor", R.drawable.standingabductor),
        "Cabel" to Pair("Cable", R.drawable.cable),
        "Hacksquat" to Pair("Hack Squat", R.drawable.hacksquat),
        "Packfly" to Pair("Pec Fly", R.drawable.packfly),
        "Dip" to Pair("Dip", R.drawable.dip),
        "Letpull" to Pair("Lat Pull", R.drawable.letpull),
        "Preacher" to Pair("Preacher Curl", R.drawable.preacher),
        "Excercise" to Pair("Exercise", R.drawable.preacher),
        "Hammer&Tyre" to Pair("Hammer & Tyre", R.drawable.hammer)
    )



    private val equipmentsList = arrayOf(
        "Treadmill",
        "SpinBike",
        "Dumbbell",
        "Multipress",
        "Benches",
        "Legpress",
        "Extension",
        "Punchingbag",
        "SmithMachine",
        "Elliptical",
        "StandingAbductor",
        "Cabel",
        "Hacksquat",
        "Packfly",
        "Dip",
        "Letpull",
        "Preacher",
        "Excercise",
        "Hammer&Tyre"
    )


    private val checkedEquipments = BooleanArray(equipmentsList.size)


    private var selectedFacilities: ArrayList<String>? = arrayListOf()
    private val amenityMappings = mapOf(
        "AC" to Pair("Air Conditioning", R.drawable.ac),
        "Water" to Pair("Water Dispenser", R.drawable.water),
        "Coffee" to Pair("Coffee Machine", R.drawable.coffee),
        "Parking" to Pair("Parking Space", R.drawable.parking),
        "Trainer" to Pair("Personal Trainer", R.drawable.personal_trainer),
        "Cardio" to Pair("Cardio Equipment", R.drawable.cardio),
        "Canteen" to Pair("Canteen", R.drawable.canteen),
        "Music" to Pair("Music System", R.drawable.music),
        "Washroom" to Pair("Washroom", R.drawable.washroom),
        "Locker" to Pair("Locker Room", R.drawable.locker),
        "Changing" to Pair("Changing Room", R.drawable.changing_room),
        "Supplements" to Pair("Supplements", R.drawable.supplement_bottle),
        "Wifi" to Pair("Wi-Fi", R.drawable.wifi)
    )


    private val amenitiesList = arrayOf(
        "AC",
        "Water",
        "Coffee",
        "Parking",
        "Trainer",
        "Cardio",
        "Canteen",
        "Music",
        "Washroom",
        "Locker",
        "Changing",
        "Supplements",
        "Wifi"
    )


    private val checkedFacilities =
        BooleanArray(amenitiesList.size) // Initialize with the same length as topicsList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditGymRequestBinding.inflate(layoutInflater)
        emailViewModel = ViewModelProvider(this)[EmailViewModel::class.java]

        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        auth = FirebaseAuth.getInstance()

        storageRef = FirebaseStorage.getInstance()


        val receivedIntent = intent

        if (receivedIntent.hasExtra("gymData")) {
            val userDetails: GymResponseItem? = receivedIntent.getParcelableExtra("gymData")
            userDetails?.let {
                gymData = it
                initListener()
                1
            }
        } else {
            ToastUtil.makeToast(this, "Gym details not found")
            finish()
        }
        observeProgress()
        observerErrorMessageApiResponse()
        observerEmailApiResponse()
    }

    private fun initListener() {
        binding.amenitiesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.equipmentsRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.amenitiesButton.setOnClickListener {
            chooseFacilitiesDialog()
        }
        binding.equipmentsButton.setOnClickListener {
            chooseEquipmentsDialog()
        }


        binding.imageRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ImageAdapter(this, imagesUriList)

        when (gymData.photo?.size) {
            0 -> itemsCount = 5
            1 -> itemsCount = 4
            2 -> itemsCount = 3
            3 -> itemsCount = 2
            4 -> itemsCount = 1
            else -> binding.addImage.visibility = View.GONE
        }

        pickMedia =

            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(itemsCount)) { uri ->
                if (uri != null) {
                    this.imagesUriList.addAll(uri)
                    adapter = ImageAdapter(this@EditGymRequestActivity, imagesUriList)
                    adapter.notifyDataSetChanged()
                    binding.imageRecyclerView.adapter = adapter

                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }


        binding.libNameEt.setText(gymData.name)
        binding.coachesEt.setText(gymData.trainers.size.toString())
        binding.ownerEt.setText(gymData.ownerName)
        binding.feesEt.setText(gymData.pricing?.daily.toString())
        binding.gymPhoneEt.setText(gymData.contact)
        binding.bioEt.setText(gymData.bio)

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


        val timing = gymData.timing

        when (timing.size) {
            3 -> {
                val timeStartFormatted2 = formatTime(timing[2].from?.toInt(), 0)
                val timeEndFormatted2 = formatTime(timing[2].to?.toInt(), 0)

                binding.buttonSlot3.text = "Slot 3 : $timeStartFormatted2 to $timeEndFormatted2"


                val timeStartFormatted1 = formatTime(timing[1].from?.toInt(), 0)
                val timeEndFormatted1 = formatTime(timing[1].to?.toInt(), 0)

                binding.buttonSlot2.text = "Slot 2 : $timeStartFormatted1 to $timeEndFormatted1"


                val timeStartFormatted = formatTime(timing[0].from?.toInt(), 0)
                val timeEndFormatted = formatTime(timing[0].to?.toInt(), 0)

                binding.buttonSlot1.text = "Slot 1 : $timeStartFormatted to $timeEndFormatted"
            }

            2 -> {


                val timeStartFormatted1 = formatTime(timing[1].from?.toInt(), 0)
                val timeEndFormatted1 = formatTime(timing[1].to?.toInt(), 0)

                binding.buttonSlot2.text = "Slot 2 : $timeStartFormatted1 to $timeEndFormatted1"


                val timeStartFormatted = formatTime(timing[0].from?.toInt(), 0)
                val timeEndFormatted = formatTime(timing[0].to?.toInt(), 0)

                binding.buttonSlot1.text = "Slot 1 : $timeStartFormatted to $timeEndFormatted"

            }

            1 -> {

                val timeStartFormatted = formatTime(timing[0].from?.toInt(), 0)
                val timeEndFormatted = formatTime(timing[0].to?.toInt(), 0)

                binding.buttonSlot1.text = "Slot 1 : $timeStartFormatted to $timeEndFormatted"

            }
        }

        val amenities = gymData.ammenities
        if (amenities != null) {
            val allAmenities = getAmenitiesWithDrawable(amenities, amenityMappings)
            val adapter = AmenitiesAdapter(this, allAmenities)
            binding.amenitiesRecyclerView.adapter = adapter
            //                setAmenitiesWithDrawable(binding.tvAmmenities, amenities)
        }


        val equipments = gymData.equipments
        if (equipments != null) {
            val allEquipments = getEquipmentWithDrawable(equipments, equipmentMappings)
            val adapter = AmenitiesAdapter(this, allEquipments)
            binding.equipmentsRecyclerView.adapter = adapter
            //                setAmenitiesWithDrawable(binding.tvAmmenities, amenities)
        }

        binding.addImage.setOnClickListener {
            uploadImageDialog()
        }
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.sendRequestButton.setOnClickListener {

            val gymName = binding.libNameEt.text.toString()
            val phoneNumber = binding.gymPhoneEt.text.toString()
            val coaches = binding.coachesEt.text.toString()
            val fees = binding.feesEt.text.toString()
            val owner = binding.ownerEt.text.toString()
            val bio = binding.bioEt.text.toString()
            val changes = binding.changeEt.text.toString()
            val buttonSlot1 = binding.buttonSlot1.text.toString()
            val buttonSlot2 = binding.buttonSlot2.text.toString()
            val buttonSlot3 = binding.buttonSlot3.text.toString()

            if (gymName.trim().isEmpty()) {
                binding.libNameEt.error = "Empty Field"
            } else if (gymName.length < 2) {
                binding.libNameEt.error = "Enter minimum 2 characters"
            } else if (gymName.length > 100) {
                binding.libNameEt.error = "Enter less than 100 characters"
            } else if (phoneNumber.trim().isEmpty()) {
                binding.gymPhoneEt.error = "Empty Field"
            } else if (phoneNumber.length < 10) {
                binding.gymPhoneEt.error = "Please Enter Valid Mobile Number"
            } else if (fees.trim().isEmpty()) {
                binding.feesEt.error = "Empty Field"
            } else if (coaches.trim().isEmpty()) {
                binding.coachesEt.error = "Empty Field"
            } else if (owner.trim().isEmpty()) {
                binding.ownerEt.error = "Empty Field"
            } else if (owner.length < 2) {
                binding.ownerEt.error = "Enter minimum 2 characters"
            } else if (owner.length > 30) {
                binding.ownerEt.error = "Enter less than 30 characters"
            } else if (buttonSlot1 == "Set Slot 1 Time" && buttonSlot2 == "Set Slot 2 Time" && buttonSlot3 == "Set Slot 3 Time") {
                binding.requireTime.visibility = View.VISIBLE
            } else if (changes.trim().isEmpty()) {
                binding.changeEt.error = "Empty Field"
            } else if (imagesUriList.size > itemsCount) {
                ToastUtil.makeToast(
                    this,
                    "Select Maximum Of $itemsCount Images. You already have ${3 - itemsCount} uploaded"
                )
            } else {
                if (imagesUriList.isNotEmpty()) {
                    uploadImage(
                        gymName,
                        phoneNumber,
                        gymData.address?.street!!,
                        gymData.address?.pincode!!,
                        gymData.address?.state!!,
                        gymData.address?.district!!,
                        coaches,
                        fees,
                        owner,
                        changes,
                        bio,
                        buttonSlot1,
                        buttonSlot2,
                        buttonSlot3
                    )
                } else {
                    sendGymEmail(
                        gymName,
                        phoneNumber,
                        gymData.address?.street!!,
                        gymData.address?.pincode!!,
                        gymData.address?.state!!,
                        gymData.address?.district!!,
                        coaches,
                        fees,
                        changes,
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
        changes: String,
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
                        sendGymEmail(
                            gymName,
                            phoneNumber,
                            address,
                            pinCode,
                            state,
                            district,
                            coaches,
                            fees,
                            changes,
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

    private fun sendGymEmail(
        gymName: String,
        gymPhone: String,
        address: String,
        pinCode: String,
        state: String,
        district: String,
        coaches: String,
        fees: String,
        changes: String,
        owner: String,
        bio: String,
        buttonSlot1: String,
        buttonSlot2: String,
        buttonSlot3: String,
        photoUrlList: List<String>
    ) {
        val body = """
        User Id       : ${auth.currentUser!!.uid}    
        Library Name  : $gymName
        Gym Phone     : $gymPhone
        Address       : $address
        Pin Code      : $pinCode
        State         : $state
        District      : $district
        Coaches       : $coaches
        Fees          : $fees
        Owner         : $owner
        Amenities     : $selectedFacilities
        Bio           : $bio
        Changes Made  : $changes
        Slot 1 Time   : $buttonSlot1
        Slot 2 Time   : $buttonSlot2
        Slot 3 Time   : $buttonSlot3
        Images List   : $photoUrlList        
    """.trimIndent()

        callSendEmail(
            EmailRequestModel(
                "Request To Edit Gym",
                From("indian.study.group@demomailtrap.com", "Gym Edit Request"),
                "Gym Edit Request",
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

    private fun formatTime(hours: Int?, minutes: Int?): String {
        if (hours == null || minutes == null) {
            throw IllegalArgumentException("Hours and minutes cannot be null")
        }

        val adjustedHours = when {
            hours == 24 -> 0
            hours == 0 -> 12
            hours > 12 -> hours - 12
            hours == 12 -> 12
            else -> hours
        }

        val amPm = if (hours < 12 || hours == 24) "AM" else "PM"

        return String.format("%02d:%02d %s", adjustedHours, minutes, amPm)
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
            checkedFacilities.fill(false)
        }
        // Create and show the AlertDialog
        val dialog = builder.create()
        dialog.show()
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

    private fun observerErrorMessageApiResponse() {
        emailViewModel.errorMessage.observe(this, Observer {
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


}