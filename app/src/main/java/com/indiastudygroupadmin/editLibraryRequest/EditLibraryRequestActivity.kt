package com.indiastudygroupadmin.editLibraryRequest

import android.app.Dialog
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import com.indiastudygroupadmin.addLibrary.ui.adapter.ImageAdapter
import com.indiastudygroupadmin.app_utils.TimePickerCustomDialog
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.bottom_nav_bar.library.model.LibraryResponseItem
import com.indiastudygroupadmin.databinding.ActivityEditLibraryRequestBinding
import com.indiastudygroupadmin.databinding.AddTimingBottomDialogBinding
import com.indiastudygroupadmin.databinding.ErrorBottomDialogLayoutBinding
import com.indiastudygroupadmin.email.EmailRequestModel
import com.indiastudygroupadmin.email.EmailViewModel
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsResponseModel

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
    private var itemsCount = 0
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
        binding.imageRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ImageAdapter(this, imagesUriList)

        when (libraryData.photo?.size) {
            0 -> itemsCount = 3
            1 -> itemsCount = 2
            2 -> itemsCount = 1
            3 -> itemsCount = 0
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
        val drawable = ContextCompat.getDrawable(this, R.drawable.baseline_air_24)
        if (drawable != null) {
            setAmenitiesWithDrawable(binding.amenitiesRecyclerView, amenities, drawable)
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
            } else {
                val recipient = "indianstudygroup1@gmail.com"
                val subject = "Request To Add The Library"
                val body = """
                Library Name: $libName
                Seats: $seats
                Owner: $owner
                Bio: $bio
                Slot 1 Time: $buttonSlot1
                Slot 2 Time: $buttonSlot2
                Slot 3 Time: $buttonSlot3
            """.trimIndent()

                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                    data = Uri.parse("mailto:") // Only email apps should handle this
                    type = "text/plain"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, body)
                }
                try {
                    startActivity(Intent.createChooser(emailIntent, "Send Email"))
                } catch (e: Exception) {
                    e.localizedMessage?.let { it1 -> ToastUtil.makeToast(this, it1) }
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

    private fun setAmenitiesWithDrawable(
        textView: TextView, amenities: List<String>?, drawable: Drawable
    ) {
        if (amenities == null) {
            binding.amenitiesRecyclerView.text = ""
            return
        }

        val spannableStringBuilder = SpannableStringBuilder()

        amenities.forEach { amenity ->
            val spannableString = SpannableString(" $amenity\n")

            // Adjust drawable size if needed
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

            // Create an ImageSpan and set it to the SpannableString
            val imageSpan = ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM)
            spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Append this spannableString to the builder
            spannableStringBuilder.append(spannableString)
        }

        binding.amenitiesRecyclerView.text = spannableStringBuilder
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