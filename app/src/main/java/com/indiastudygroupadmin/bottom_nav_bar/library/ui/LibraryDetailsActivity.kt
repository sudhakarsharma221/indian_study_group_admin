package com.indiastudygroupadmin.bottom_nav_bar.library.ui

import android.app.DownloadManager
import android.app.DownloadManager.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
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
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.app_utils.HideStatusBarUtil
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.bottom_nav_bar.library.ui.adapter.DaysAdapter
import com.indiastudygroupadmin.bottom_nav_bar.library.viewModel.LibraryViewModel
import com.indiastudygroupadmin.databinding.ActivityLibraryDetailsBinding
import com.indiastudygroupadmin.databinding.ErrorBottomDialogLayoutBinding
import com.indiastudygroupadmin.editLibraryRequest.EditLibraryRequestActivity
import java.io.File

class LibraryDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLibraryDetailsBinding
    private lateinit var viewModel: LibraryViewModel
    private lateinit var libraryId: String
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var isExpanded = false
    private lateinit var libImageList: ArrayList<ImageSlidesModel>
    private var listener: ItemsListener? = null
    private var listOfDays: ArrayList<String>? = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HideStatusBarUtil.hideStatusBar(this)
        binding = ActivityLibraryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
//        window.statusBarColor = Color.parseColor("#2f3133")
        libImageList = ArrayList()

        viewModel = ViewModelProvider(this@LibraryDetailsActivity)[LibraryViewModel::class.java]
        libraryId = intent.getStringExtra("LibraryId").toString()
        callIdLibraryDetailsApi(libraryId)

        initListener()
        observeProgress()
        observerIdLibraryApiResponse()
        observerErrorMessageApiResponse()
    }

    private fun initListener() {

        binding.rvDays.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
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


        binding.backButton.setOnClickListener {
            finish()
        }



        binding.openMap.setOnClickListener {
            openGoogleMaps(
                latitude, longitude
            )
        }

    }

    private fun showConfirmBookingDialog() {
        val bottomDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val dialogBinding = ErrorBottomDialogLayoutBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(true)
        bottomDialog.show()
        dialogBinding.headingTv.visibility = View.VISIBLE
        dialogBinding.messageTv.text =
            "Your booking will be confirmed once library owner approves it. You can check it on your sessions "
        dialogBinding.continueButton.setOnClickListener {
            bottomDialog.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == RESULT_OK) {
            showConfirmBookingDialog()
        }
    }

    private fun showReviewDialog() {
//        val bottomDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
//        val dialogBinding = ReviewBottomDialogBinding.inflate(layoutInflater)
//        bottomDialog.setContentView(dialogBinding.root)
//        bottomDialog.setCancelable(true)
//        bottomDialog.show()
////        dialogBinding.messageTv.text = message
////        dialogBinding.continueButton.setOnClickListener {
////            HideKeyboard.hideKeyboard(requireContext(), binding.phoneEt.windowToken)
////            bottomDialog.dismiss()
////        }
    }

    private fun callIdLibraryDetailsApi(
        id: String?
    ) {
        viewModel.callIdLibrary(id)
    }

    private fun observerIdLibraryApiResponse() {
        viewModel.idLibraryResponse.observe(this, Observer { libraryData ->
//            viewModel.setLibraryDetailsResponse(it)


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

            when (seats.size) {
                3 -> {
                    binding.tvSeats33.text = "${seats[2]} / ${libraryData.libData?.seats}"
                    binding.tvSeats22.text = "${seats[1]} / ${libraryData.libData?.seats}"
                    binding.tvSeats11.text = "${seats[0]} / ${libraryData.libData?.seats}"


                    val timeStartFormatted2 =
                        formatTime(libraryData.libData?.timing?.get(2)?.from?.toInt(), 0)
                    val timeEndFormatted2 =
                        formatTime(libraryData.libData?.timing?.get(2)?.to?.toInt(), 0)

                    binding.tvTime2.text = "$timeStartFormatted2 to $timeEndFormatted2"


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
                    Log.d(
                        "TIMECHECK", "${libraryData.libData?.timing?.get(0)?.from?.toInt()} to ${
                            libraryData.libData?.timing?.get(0)?.to?.toInt()
                        }"
                    )

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
            val drawable = ContextCompat.getDrawable(this, R.drawable.baseline_air_24)
            if (drawable != null) {
                setAmenitiesWithDrawable(binding.tvAmmenities, amenities, drawable)
            }

//            binding.tvAmmenities.text = libraryData.libData?.ammenities?.joinToString("\n")
//            binding.tvPrice.text = HtmlCompat.fromHtml(
//                "<b>Daily Charge : </b> ₹${it.pricing?.daily}<br/>",
//                HtmlCompat.FROM_HTML_MODE_LEGACY
//            )

            binding.tvAddress.text =
                "${libraryData.libData?.address?.street}, ${libraryData.libData?.address?.district}, ${libraryData.libData?.address?.state}, ${libraryData.libData?.address?.pincode}"
            listOfDays = libraryData.libData?.timing?.get(0)?.days
            binding.rvDays.adapter = DaysAdapter(this, listOfDays!!)

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

    private fun setAmenitiesWithDrawable(
        textView: TextView, amenities: List<String>?, drawable: Drawable
    ) {
        if (amenities == null) {
            binding.tvAmmenities.text = ""
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

        binding.tvAmmenities.text = spannableStringBuilder
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
    }

    private fun observerErrorMessageApiResponse() {
        viewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
    }

    private fun formatTime(hours: Int?, minutes: Int?): String {
        val hourFormatted = if (hours == 0 || hours == 21) 12 else hours?.rem(12)
        val amPm = if (hours!! < 12) "am" else "pm"
        return String.format("%02d:%02d %s", hourFormatted, minutes, amPm)
    }
}