package com.indiastudygroupadmin.editLibraryRequest

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
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
import com.indiastudygroupadmin.app_utils.TimePickerCustomDialog
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.bottom_nav_bar.library.model.AmenityItem
import com.indiastudygroupadmin.bottom_nav_bar.library.model.LibraryResponseItem
import com.indiastudygroupadmin.bottom_nav_bar.library.ui.adapter.AmenitiesAdapter
import com.indiastudygroupadmin.databinding.ActivityEditLibraryRequestBinding
import com.indiastudygroupadmin.databinding.AddTimingBottomDialogBinding
import com.indiastudygroupadmin.databinding.ErrorBottomDialogLayoutBinding
import com.indiastudygroupadmin.email.EmailRequestModel
import com.indiastudygroupadmin.email.EmailViewModel
import com.indiastudygroupadmin.email.From
import com.indiastudygroupadmin.email.To

class EditLibraryRequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditLibraryRequestBinding
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var auth: FirebaseAuth
    private lateinit var libraryData: LibraryResponseItem
    private var morningButton = false
    private var afternoonButton = false
    private var eveningButton = false
    private var uri: Uri? = null
    private var storageRef = Firebase.storage
    private lateinit var emailViewModel: EmailViewModel
    private var imagesUriList = ArrayList<Uri>()
    private lateinit var adapter: ImageAdapter
    private var itemsCount = 5
    private var selectedFacilities: ArrayList<String>? = arrayListOf()

    private val amenityMappings = mapOf(
        "AC" to Pair("Air Conditioning", R.drawable.ac),
        "Studyspace" to Pair("Study Space", R.drawable.study),
        "Wifi" to Pair("Wi-Fi", R.drawable.wifi),
        "Printing" to Pair("Printing", R.drawable.printing),
        "Charging" to Pair("Charging Station", R.drawable.charging),
        "Groupstudyroom" to Pair("Group Study Room", R.drawable.groupstudy),
        "Refreshment" to Pair("Refreshment Area", R.drawable.refreshment),
        "Studyarea" to Pair("Study Area", R.drawable.study),
        "Books" to Pair("Books and Magazines", R.drawable.books),
        "Computer" to Pair("Computer", R.drawable.computer)
    )


    private val facilitiesList = arrayOf(
        "AC",
        "Studyspace",
        "Wifi",
        "Printing",
        "Charging",
        "Groupstudyroom",
        "Refreshment",
        "Books",
        "Computer",
    )

    //    private val facilitiesList2 = arrayOf(
//        "AC",
//        "Study Spaces",
//        "Wi-Fi",
//        "Printing Services",
//        "Charging Stations",
//        "Group Study Rooms",
//        "Refreshment",
//        "Books",
//        "Computers",
//    )
    private val checkedFacilities =
        BooleanArray(facilitiesList.size) // Initialize with the same length as topicsList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditLibraryRequestBinding.inflate(layoutInflater)
        emailViewModel = ViewModelProvider(this)[EmailViewModel::class.java]

        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        auth = FirebaseAuth.getInstance()

        storageRef = FirebaseStorage.getInstance()


        val receivedIntent = intent

        if (receivedIntent.hasExtra("libraryData")) {
            val userDetails: LibraryResponseItem? = receivedIntent.getParcelableExtra("libraryData")
            userDetails?.let {
                libraryData = it
                initListener()
                1
            }
        } else {
            ToastUtil.makeToast(this, "Library details not found")
            finish()
        }
        observeProgress()
        observerErrorMessageApiResponse()
        observerEmailApiResponse()

    }

    private fun initListener() {
        binding.amenitiesRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.amenitiesButton.setOnClickListener {
            chooseFacilitiesDialog()
        }

        binding.imageRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ImageAdapter(this, imagesUriList)

        when (libraryData.photo?.size) {
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
                    adapter = ImageAdapter(this@EditLibraryRequestActivity, imagesUriList)
                    adapter.notifyDataSetChanged()
                    binding.imageRecyclerView.adapter = adapter

                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }


        binding.libNameEt.setText(libraryData.name)
        binding.seats.setText(libraryData.seats.toString())
        binding.ownerEt.setText(libraryData.ownerName)
        binding.bioEt.setText(libraryData.bio)

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


        val timing = libraryData.timing

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

        val amenities = libraryData.ammenities
        if (amenities != null) {
            val allAmenities = getAmenitiesWithDrawable(amenities, amenityMappings)
            val adapter = AmenitiesAdapter(this, allAmenities)
            binding.amenitiesRecyclerView.adapter = adapter
            //                setAmenitiesWithDrawable(binding.tvAmmenities, amenities)
        }

        binding.addImage.setOnClickListener {
            uploadImageDialog()
        }
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.sendRequestButton.setOnClickListener {

            val libName = binding.libNameEt.text.toString()
            val seats = binding.seats.text.toString()
            val owner = binding.ownerEt.text.toString()
            val bio = binding.bioEt.text.toString()
            val changes = binding.changeEt.text.toString()
            val buttonSlot1 = binding.buttonSlot1.text.toString()
            val buttonSlot2 = binding.buttonSlot2.text.toString()
            val buttonSlot3 = binding.buttonSlot3.text.toString()

            if (libName.trim().isEmpty()) {
                binding.libNameEt.error = "Empty Field"
            } else if (libName.length < 2) {
                binding.libNameEt.error = "Enter minimum 2 characters"
            } else if (libName.length > 100) {
                binding.libNameEt.error = "Enter less than 100 characters"
            } else if (seats.trim().isEmpty()) {
                binding.seats.error = "Empty Field"
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
                ToastUtil.makeToast(this, "Select Maximum Of $itemsCount Images. You already have ${3- itemsCount} uploaded")
            } else {
                if (imagesUriList.isNotEmpty()) {
                    uploadImage(
                        libName,
                        libraryData.address?.street!!,
                        libraryData.address?.pincode!!,
                        libraryData.address?.state!!,
                        libraryData.address?.district!!,
                        seats,
                        owner,
                        bio,
                        changes,
                        buttonSlot1,
                        buttonSlot2,
                        buttonSlot3
                    )
                } else {
                    sendLibraryEmail(
                        libName,
                        libraryData.address?.street!!,
                        libraryData.address?.pincode!!,
                        libraryData.address?.state!!,
                        libraryData.address?.district!!,
                        seats,
                        owner,
                        bio,
                        changes,
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
        libName: String,
        address: String,
        pinCode: String,
        state: String,
        district: String,
        seats: String,
        owner: String,
        bio: String,
        changes: String,
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
                            libName,
                            address,
                            pinCode,
                            state,
                            district,
                            seats,
                            owner,
                            bio,
                            changes,
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

    private fun sendLibraryEmail(
        libName: String,
        address: String,
        pinCode: String,
        state: String,
        district: String,
        seats: String,
        owner: String,
        bio: String,
        changes: String,
        buttonSlot1: String,
        buttonSlot2: String,
        buttonSlot3: String,
        photoUrlList: List<String>
    ) {
        val body = """
        User Id       : ${auth.currentUser!!.uid}    
        Library Name  : $libName
        Address       : $address
        Pin Code      : $pinCode
        State         : $state
        District      : $district
        Seats         : $seats
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
                "Request To Edit Library",
                From("indian.study.group@demomailtrap.com", "Library Edit Request"),
                "Library Edit Request",
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
        val hourFormatted = if (hours == 0 || hours == 21) 12 else hours?.rem(12)
        val amPm = if (hours!! < 12) "am" else "pm"
        return String.format("%02d:%02d %s", hourFormatted, minutes, amPm)
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
                selectedFacilities?.clear()
                for (i in checkedFacilities.indices) {
                    if (checkedFacilities[i]) {
                        selectedFacilities?.add(facilitiesList[i])
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