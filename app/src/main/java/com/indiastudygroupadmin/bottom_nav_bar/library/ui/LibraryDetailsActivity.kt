package com.indiastudygroupadmin.bottom_nav_bar.library.ui

import android.app.DownloadManager
import android.app.DownloadManager.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codebyashish.autoimageslider.Enums.ImageAnimationTypes
import com.codebyashish.autoimageslider.Enums.ImageScaleType
import com.codebyashish.autoimageslider.Interfaces.ItemsListener
import com.codebyashish.autoimageslider.Models.ImageSlidesModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.app_utils.HideStatusBarUtil
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.bottom_nav_bar.library.model.AmenityItem
import com.indiastudygroupadmin.bottom_nav_bar.library.model.LibraryIdDetailsResponseModel
import com.indiastudygroupadmin.bottom_nav_bar.library.ui.adapter.AmenitiesAdapter
import com.indiastudygroupadmin.bottom_nav_bar.library.ui.adapter.DaysAdapter
import com.indiastudygroupadmin.bottom_nav_bar.library.ui.adapter.ReviewAdapter
import com.indiastudygroupadmin.bottom_nav_bar.library.viewModel.LibraryViewModel
import com.indiastudygroupadmin.databinding.ActivityLibraryDetailsBinding
import com.indiastudygroupadmin.databinding.DeleteLibraryBottomDialogBinding
import com.indiastudygroupadmin.databinding.ErrorBottomDialogLayoutBinding
import com.indiastudygroupadmin.databinding.ReviewBottomDialogBinding
import com.indiastudygroupadmin.editLibraryRequest.EditLibraryRequestActivity
import com.indiastudygroupadmin.email.EmailRequestModel
import com.indiastudygroupadmin.email.EmailViewModel
import com.indiastudygroupadmin.email.From
import com.indiastudygroupadmin.email.To
import com.indiastudygroupadmin.rating.ui.ReviewActivity
import java.io.File

class LibraryDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLibraryDetailsBinding
    private lateinit var viewModel: LibraryViewModel
    private lateinit var libraryId: String
    private var latitude: Double? = null
    private lateinit var auth: FirebaseAuth
    private var longitude: Double? = null
    private lateinit var emailViewModel: EmailViewModel
    private var isExpanded = false
    private lateinit var libImageList: ArrayList<ImageSlidesModel>
    private var listener: ItemsListener? = null
    private lateinit var libraryDetails: LibraryIdDetailsResponseModel
    private var listOfDays: ArrayList<String>? = arrayListOf()
    val amenityMappings = mapOf(
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HideStatusBarUtil.hideStatusBar(this)
        binding = ActivityLibraryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        emailViewModel = ViewModelProvider(this)[EmailViewModel::class.java]
//        window.statusBarColor = Color.parseColor("#2f3133")
        libImageList = ArrayList()
        auth = FirebaseAuth.getInstance()

        viewModel = ViewModelProvider(this@LibraryDetailsActivity)[LibraryViewModel::class.java]
        libraryId = intent.getStringExtra("LibraryId").toString()
        callIdLibraryDetailsApi(libraryId)

        initListener()
        observerEmailApiResponse()
        observeProgress()
        observerIdLibraryApiResponse()
        observerErrorMessageApiResponse()
    }

    private fun initListener() {
        binding.reviewRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.rvDays.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.tvAmmenities.layoutManager = LinearLayoutManager(this)

//        binding.readMore.setOnClickListener {
//            if (isExpanded) {
//                // Collapse the bio
//                binding.tvBio.maxLines = 3
//                binding.readMore.text = "Read more"
//            } else {
//                // Expand the bio
//                binding.tvBio.maxLines = Int.MAX_VALUE
//                binding.readMore.text = "Read less"
//            }
//            isExpanded = !isExpanded
//        }
//        binding.tvBio.isSelected = true
//        binding.tvBio.post {
//            val numLines = binding.tvBio.lineCount
//            if (numLines > 3) {
//                // If the number of lines is more than 3, show the "Read more" button
//                binding.readMore.visibility = View.VISIBLE
//            } else {
//                // If the number of lines is 3 or less, hide the "Read more" button
//                binding.readMore.visibility = View.GONE
//            }
//        }


        binding.writeReview.setOnClickListener {
            showReviewDialog()
        }

        binding.delete.setOnClickListener {
            showDeleteBottomDialog()
        }
        binding.backButton.setOnClickListener {
            finish()
        }



        binding.openMap.setOnClickListener {
            openGoogleMaps(
                latitude, longitude
            )
        }

    }

    private fun showDeleteBottomDialog() {
        val bottomDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val dialogBinding = DeleteLibraryBottomDialogBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(true)
        bottomDialog.show()

        dialogBinding.tvLibraryOwnerName.text = libraryDetails.libData?.ownerName
//
        Glide.with(this).load(libraryDetails.libData?.ownerPhoto).placeholder(R.drawable.profile)
            .error(R.drawable.profile).into(dialogBinding.libraryOwnerPhoto)

        dialogBinding.submitButton.setOnClickListener {
            val deleteText = dialogBinding.reviewEt.text.toString()
            if (deleteText.trim().isEmpty()) {
                dialogBinding.reviewEt.error = "Empty Field"
            } else {


                val body = """
        User Id       : ${auth.currentUser!!.uid}    
        Library Name  : ${libraryDetails.libData?.name}
        Address       : ${libraryDetails.libData?.address?.street}
        Pin Code      : ${libraryDetails.libData?.address?.pincode}
        State         : ${libraryDetails.libData?.address?.state}
        District      : ${libraryDetails.libData?.address?.district}
        Seats         : ${libraryDetails.libData?.seats}
        Owner         : ${libraryDetails.libData?.ownerName}
        Amenities     : ${libraryDetails.libData?.ammenities}
        Bio           : ${libraryDetails.libData?.bio}
        Delete Reason :  $deleteText
    """.trimIndent()


                bottomDialog.dismiss()
                callSendEmail(
                    EmailRequestModel(
                        "Request To Delete Library",
                        From("indian.study.group@demomailtrap.com", "Library Delete Request"),
                        "Library Delete Request",
                        body,
                        listOf(To("indianstudygroup1@gmail.com"))
                    )
                )
            }
        }


    }

//    private fun showConfirmBookingDialog() {
//        val bottomDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
//        val dialogBinding = ErrorBottomDialogLayoutBinding.inflate(layoutInflater)
//        bottomDialog.setContentView(dialogBinding.root)
//        bottomDialog.setCancelable(true)
//        bottomDialog.show()
//        dialogBinding.headingTv.visibility = View.VISIBLE
//        dialogBinding.messageTv.text =
//            "Your booking will be confirmed once library owner approves it. You can check it on your sessions "
//        dialogBinding.continueButton.setOnClickListener {
//            bottomDialog.dismiss()
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 2 && resultCode == RESULT_OK) {
//            showConfirmBookingDialog()
//        }
//    }

    private fun callIdLibraryDetailsApi(
        id: String?
    ) {
        viewModel.callIdLibrary(id)
    }

    private fun showReviewDialog() {
        val bottomDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val dialogBinding = ReviewBottomDialogBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(true)
        bottomDialog.show()
        var ratingValue = 0f
        dialogBinding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            dialogBinding.error.visibility = View.GONE
            if (fromUser) {
                ratingValue = rating
            }
        }


        dialogBinding.tvLibraryOwnerName.text = libraryDetails.libData?.ownerName
//
        Glide.with(this).load(libraryDetails.libData?.ownerPhoto).placeholder(R.drawable.profile)
            .error(R.drawable.profile).into(dialogBinding.libraryOwnerPhoto)

        dialogBinding.submitButton.setOnClickListener {
            val reviewText = dialogBinding.reviewEt.text.toString()
            if (reviewText.trim().isEmpty()) {
                dialogBinding.reviewEt.error = "Empty Field"
            } else if (ratingValue == 0f) {
                dialogBinding.error.visibility = View.VISIBLE
            } else {
                ToastUtil.makeToast(this, "Review Posted\nRated: ${ratingValue.toInt()} stars")
            }
        }
//        dialogBinding.messageTv.text = message
//        dialogBinding.continueButton.setOnClickListener {
//            HideKeyboard.hideKeyboard(requireContext(), binding.phoneEt.windowToken)
//            bottomDialog.dismiss()
//        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observerIdLibraryApiResponse() {
        viewModel.idLibraryResponse.observe(this, Observer { libraryData ->
//            viewModel.setLibraryDetailsResponse(it)
            libraryDetails = libraryData

            latitude = libraryData.libData?.address?.latitude?.toDouble()
            longitude = libraryData.libData?.address?.longitude?.toDouble()

            if (libraryData.libData?.photo?.isEmpty() == true) {
                binding.libNoImage.visibility = View.VISIBLE
            } else {
                libraryData.libData?.photo?.forEach {
                    libImageList.add(ImageSlidesModel(it, ""))
                }
                addImageOnAutoImageSlider()
            }


            binding.sendRequestButton.setOnClickListener {
                val intent = Intent(this, EditLibraryRequestActivity::class.java)
                intent.putExtra("libraryData", libraryData.libData)
                startActivity(intent)
            }
//            Glide.with(this).load(libraryData.libData?.photo).placeholder(R.drawable.noimage)
//                .error(R.drawable.noimage).into(binding.libImage)
            binding.tvName.text = libraryData.libData?.name
            binding.tvBio.text = libraryData.libData?.bio
            val seats = libraryData.libData?.vacantSeats!!

            binding.tvReviews.text = "${libraryData.libData?.reviews?.size} Reviews"
            binding.tvReviews.setOnClickListener {

                if (libraryData.libData?.reviews?.isEmpty() == true) {
                    ToastUtil.makeToast(this, "No Reviews")
                } else {
                    val intent = Intent(this, ReviewActivity::class.java)
                    intent.putExtra("Reviews", libraryData.libData?.reviews)
                    startActivity(intent)
                }

            }
            val adapter = libraryData.libData?.reviews?.let { ReviewAdapter(this, it) }
            binding.reviewRecyclerView.adapter = adapter
            var rating = 1f
            if (libraryData.libData?.rating?.count == 0) {
                binding.tvRating.text = "1.0"
            } else {
                if (libraryData.libData?.rating?.count == null) {
                    binding.tvRating.text = "1.0"
                } else {
                    Log.d("RATING", rating.toInt().toString())


                    rating = (libraryData.libData?.rating?.count?.toFloat()?.let {
                        libraryData.libData?.rating?.totalRatings?.toFloat()?.div(
                            it
                        )
                    })?.toFloat()!!

                    Log.d("RATINGG", rating.toString())
                }

            }
            binding.tvRating.text = String.format("%.1f", rating)
            if (libraryData.libData?.rating?.count == null) {
                binding.basedOnReview.text = "Based On 0 Reviews"
            } else {
                binding.basedOnReview.text =
                    "Based On ${libraryData.libData?.rating?.count} Reviews"
            }

            binding.ratingBar.rating = rating

            when (seats.size) {
                3 -> {
                    binding.tvSeats33.text = "${seats[2]} / ${libraryData.libData?.seats}"
                    binding.tvSeats22.text = "${seats[1]} / ${libraryData.libData?.seats}"
                    binding.tvSeats11.text = "${seats[0]} / ${libraryData.libData?.seats}"


                    val timeStartFormatted2 =
                        formatTime(libraryData.libData?.timing?.get(2)?.from?.toInt(), 0)
                    val timeEndFormatted2 =
                        formatTime(libraryData.libData?.timing?.get(2)?.to?.toInt(), 0)

                    binding.tvTime3.text = "$timeStartFormatted2 to $timeEndFormatted2"


                    val timeStartFormatted1 =
                        formatTime(libraryData.libData?.timing?.get(1)?.from?.toInt(), 0)
                    val timeEndFormatted1 =
                        formatTime(libraryData.libData?.timing?.get(1)?.to?.toInt(), 0)

                    binding.tvTime2.text = "$timeStartFormatted1 to $timeEndFormatted1"


                    val timeStartFormatted =
                        formatTime(libraryData.libData?.timing?.get(0)?.from?.toInt(), 0)
                    val timeEndFormatted =
                        formatTime(libraryData.libData?.timing?.get(0)?.to?.toInt(), 0)

                    binding.tvTime1.text = "$timeStartFormatted to $timeEndFormatted"

                }

                2 -> {
                    binding.tvSeats22.text = "${seats[1]} / ${libraryData.libData?.seats}"
                    binding.tvSeats11.text = "${seats[0]} / ${libraryData.libData?.seats}"
                    val timeStartFormatted1 =
                        formatTime(libraryData.libData?.timing?.get(1)?.from?.toInt(), 0)
                    val timeEndFormatted1 =
                        formatTime(libraryData.libData?.timing?.get(1)?.to?.toInt(), 0)

                    binding.tvTime2.text = "$timeStartFormatted1 to $timeEndFormatted1"


                    val timeStartFormatted =
                        formatTime(libraryData.libData?.timing?.get(0)?.from?.toInt(), 0)
                    val timeEndFormatted =
                        formatTime(libraryData.libData?.timing?.get(0)?.to?.toInt(), 0)

                    binding.tvTime1.text = "$timeStartFormatted to $timeEndFormatted"

                    binding.tvSeats33.visibility = View.GONE
                    binding.tvSeats3.visibility = View.GONE
                    binding.tvTime3.visibility = View.GONE
                }

                1 -> {
                    binding.tvSeats11.text = "${seats[0]} / ${libraryData.libData?.seats}"

                    val timeStartFormatted =
                        formatTime(libraryData.libData?.timing?.get(0)?.from?.toInt(), 0)
                    val timeEndFormatted =
                        formatTime(libraryData.libData?.timing?.get(0)?.to?.toInt(), 0)


                    binding.tvTime1.text = "$timeStartFormatted to $timeEndFormatted"

                    binding.tvSeats22.visibility = View.GONE
                    binding.tvSeats2.visibility = View.GONE
                    binding.tvTime2.visibility = View.GONE
                    binding.tvSeats3.visibility = View.GONE
                    binding.tvSeats33.visibility = View.GONE
                    binding.tvTime3.visibility = View.GONE

                }
            }

            val amenities = libraryData.libData?.ammenities
            if (amenities != null) {
                val allAmenities = getAmenitiesWithDrawable(amenities, amenityMappings)
                val adapter = AmenitiesAdapter(this, allAmenities)
                binding.tvAmmenities.adapter = adapter
                //                setAmenitiesWithDrawable(binding.tvAmmenities, amenities)
            }


            binding.tvAddress.text =
                "${libraryData.libData?.address?.street}, ${libraryData.libData?.address?.district}, ${libraryData.libData?.address?.state}, ${libraryData.libData?.address?.pincode}"
            val listOfWeekDays = arrayListOf("mon", "tue", "wed", "thu", "fri", "sat", "sun")

            listOfDays = libraryData.libData?.timing?.get(0)?.days
            binding.rvDays.adapter = DaysAdapter(this, listOfDays!!, listOfWeekDays)


//            val timingStringBuilder = StringBuilder()
//            timingStringBuilder.append("Time Slots : ")
//            libraryData.libData?.timing?.forEachIndexed { index, timing ->
//                timingStringBuilder.append(
//                    "<b>${timing.from} to ${timing.to}<br/>${
//                        timing.days.joinToString(
//                            ", "
//                        )
//                    } </b>"
//                )
//                if (index != libraryData.libData?.timing!!.size - 1) {
//                    timingStringBuilder.append("<br/>")
//                }
//            }
//            binding.tvTiming.text = HtmlCompat.fromHtml(
//                timingStringBuilder.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY
//            )
        })
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

    private fun addImageOnAutoImageSlider() {
        // add some images or titles (text) inside the imagesArrayList


        // set the added images inside the AutoImageSlider
        binding.autoImageSlider.setImageList(libImageList, ImageScaleType.FIT)

        // set any default animation or custom animation (setSlideAnimation(ImageAnimationTypes.ZOOM_IN))
        binding.autoImageSlider.setSlideAnimation(ImageAnimationTypes.DEPTH_SLIDE)

        // handle click event on item click
        binding.autoImageSlider.onItemClickListener(listener)
    }

    private fun openGoogleMaps(latitude: Double?, longitude: Double?) {
        val gmmIntentUri = Uri.parse("https://maps.google.com/maps?daddr=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
//        if (mapIntent.resolveActivity(packageManager) != null) {
        startActivity(mapIntent)
//        }
    }

    private fun callSendEmail(emailRequestModel: EmailRequestModel) {
        emailViewModel.postEmail(emailRequestModel)
    }


    private fun observeProgress() {
        viewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.mainView.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.mainView.visibility = View.VISIBLE
            }
        })
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

    private fun observerEmailApiResponse() {
        emailViewModel.emailResponse.observe(this, Observer {
            showErrorBottomDialog(
                "We have received your email and will be in touch with you shortly."
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


    private fun observerErrorMessageApiResponse() {
        viewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
        emailViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
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
}