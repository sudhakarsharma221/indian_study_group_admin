package com.indiastudygroupadminapp.bottom_nav_bar.gym.ui

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
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
import com.indiastudygroupadminapp.R
import com.indiastudygroupadminapp.app_utils.HideStatusBarUtil
import com.indiastudygroupadminapp.app_utils.ToastUtil
import com.indiastudygroupadminapp.bottom_nav_bar.gym.model.GymIdDetailsResponseModel
import com.indiastudygroupadminapp.bottom_nav_bar.gym.viewModel.GymViewModel
import com.indiastudygroupadminapp.bottom_nav_bar.library.model.AmenityItem
import com.indiastudygroupadminapp.bottom_nav_bar.library.ui.adapter.AmenitiesAdapter
import com.indiastudygroupadminapp.bottom_nav_bar.library.ui.adapter.DaysAdapter
import com.indiastudygroupadminapp.bottom_nav_bar.library.ui.adapter.ReviewAdapter
import com.indiastudygroupadminapp.databinding.ActivityGymDetailsBinding
import com.indiastudygroupadminapp.databinding.DeleteLibraryBottomDialogBinding
import com.indiastudygroupadminapp.databinding.ErrorBottomDialogLayoutBinding
import com.indiastudygroupadminapp.databinding.ReviewBottomDialogBinding
import com.indiastudygroupadminapp.editGymRequest.EditGymRequestActivity
import com.indiastudygroupadminapp.email.EmailRequestModel
import com.indiastudygroupadminapp.email.EmailViewModel
import com.indiastudygroupadminapp.email.From
import com.indiastudygroupadminapp.email.To
import com.indiastudygroupadminapp.rating.ui.ReviewActivity

class GymDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGymDetailsBinding
    private lateinit var viewModel: GymViewModel
    private lateinit var gymId: String
    private var latitude: Double? = null
    private lateinit var auth: FirebaseAuth
    private var longitude: Double? = null
    private lateinit var emailViewModel: EmailViewModel
    private var isExpanded = false
    private lateinit var libImageList: ArrayList<ImageSlidesModel>
    private var listener: ItemsListener? = null
    private lateinit var gymDetails: GymIdDetailsResponseModel
    private var listOfDays: ArrayList<String>? = arrayListOf()

    //    private var mapFragment: SupportMapFragment? = null
//    private var googleMap: GoogleMap? = null
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


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGymDetailsBinding.inflate(layoutInflater)
        HideStatusBarUtil.hideStatusBar(this)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        emailViewModel = ViewModelProvider(this)[EmailViewModel::class.java]
//        window.statusBarColor = Color.parseColor("#2f3133")
        libImageList = ArrayList()
        auth = FirebaseAuth.getInstance()

        viewModel = ViewModelProvider(this@GymDetailsActivity)[GymViewModel::class.java]
        gymId = intent.getStringExtra("gymId").toString()
        callIdGymDetailsApi(gymId)

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
        binding.tvEquipments.layoutManager = LinearLayoutManager(this)

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

        dialogBinding.tvLibraryOwnerName.text = gymDetails.gymData?.ownerName
        dialogBinding.libraryOwner.text = "Gym Owner"
//
        Glide.with(this).load(gymDetails.gymData?.ownerPhoto).placeholder(R.drawable.profile)
            .error(R.drawable.profile).into(dialogBinding.libraryOwnerPhoto)

        dialogBinding.submitButton.setOnClickListener {
            val deleteText = dialogBinding.reviewEt.text.toString()
            if (deleteText.trim().isEmpty()) {
                dialogBinding.reviewEt.error = "Empty Field"
            } else {


                val body = """
        User Id       : ${auth.currentUser!!.uid}    
        Library Name  : ${gymDetails.gymData?.name}
        Address       : ${gymDetails.gymData?.address?.street}
        Pin Code      : ${gymDetails.gymData?.address?.pincode}
        State         : ${gymDetails.gymData?.address?.state}
        District      : ${gymDetails.gymData?.address?.district}
        Seats         : ${gymDetails.gymData?.seats}
        Owner         : ${gymDetails.gymData?.ownerName}
        Amenities     : ${gymDetails.gymData?.ammenities}
        Bio           : ${gymDetails.gymData?.bio}
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

    private fun callIdGymDetailsApi(
        id: String?
    ) {
        viewModel.callIdGym(id)
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


        dialogBinding.tvLibraryOwnerName.text = gymDetails.gymData?.ownerName
//
        Glide.with(this).load(gymDetails.gymData?.ownerPhoto).placeholder(R.drawable.profile)
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
        viewModel.idGymResponse.observe(this, Observer { gymData ->
//            viewModel.setLibraryDetailsResponse(it)
            gymDetails = gymData


            binding.tvCoachesNumber.text = gymData.gymData?.trainers?.size.toString()
            binding.gymCoachesDetails.setOnClickListener {
                if (gymData.gymData?.trainers?.isEmpty() == true) {
                    ToastUtil.makeToast(this, "No Trainers")
                } else {
                    val intent = Intent(this, CoachDetailsActivity::class.java)
                    intent.putExtra("Trainers", gymData.gymData?.trainers)
                    startActivity(intent)
                }
            }

            latitude = gymData.gymData?.address?.latitude?.toDouble()
            longitude = gymData.gymData?.address?.longitude?.toDouble()

            if (gymData.gymData?.photo?.isEmpty() == true) {
                binding.libNoImage.visibility = View.VISIBLE
            } else {
                gymData.gymData?.photo?.forEach {
                    libImageList.add(ImageSlidesModel(it, ""))
                }
                addImageOnAutoImageSlider()
            }


            binding.sendRequestButton.setOnClickListener {
                val intent = Intent(this, EditGymRequestActivity::class.java)
                intent.putExtra("gymData", gymData.gymData)
                startActivity(intent)
            }
//            Glide.with(this).load(libraryData.libData?.photo).placeholder(R.drawable.noimage)
//                .error(R.drawable.noimage).into(binding.libImage)
            binding.tvName.text = gymData.gymData?.name
            binding.tvBio.text = gymData.gymData?.bio
            val seats = gymData.gymData?.vacantSeats!!

            binding.tvReviews.text = "${gymData.gymData?.reviews?.size} Reviews"
            binding.tvReviews.setOnClickListener {

                if (gymData.gymData?.reviews?.isEmpty() == true) {
                    ToastUtil.makeToast(this, "No Reviews")
                } else {
                    val intent = Intent(this, ReviewActivity::class.java)
                    intent.putExtra("Reviews", gymData.gymData?.reviews)
                    startActivity(intent)
                }

            }
            val adapter = gymData.gymData?.reviews?.let { ReviewAdapter(this, it) }
            binding.reviewRecyclerView.adapter = adapter
            var rating = 1f
            if (gymData.gymData?.rating?.count == 0) {
                binding.tvRating.text = "1.0"
            } else {
                if (gymData.gymData?.rating?.count == null) {
                    binding.tvRating.text = "1.0"
                } else {
                    Log.d("RATING", rating.toInt().toString())


                    rating = (gymData.gymData?.rating?.count?.toFloat()?.let {
                        gymData.gymData?.rating?.totalRatings?.toFloat()?.div(
                            it
                        )
                    })?.toFloat()!!

                    Log.d("RATINGG", rating.toString())
                }

            }
            binding.tvRating.text = String.format("%.1f", rating)
            if (gymData.gymData?.rating?.count == null) {
                binding.basedOnReview.text = "Based On 0 Reviews"
            } else {
                binding.basedOnReview.text = "Based On ${gymData.gymData?.rating?.count} Reviews"
            }

            binding.ratingBar.rating = rating

            when (seats.size) {
                3 -> {
                    binding.tvSeats33.text = "${seats[2]} / ${gymData.gymData?.seats}"
                    binding.tvSeats22.text = "${seats[1]} / ${gymData.gymData?.seats}"
                    binding.tvSeats11.text = "${seats[0]} / ${gymData.gymData?.seats}"


                    val timeStartFormatted2 =
                        formatTime(gymData.gymData?.timing?.get(2)?.from?.toInt(), 0)
                    val timeEndFormatted2 =
                        formatTime(gymData.gymData?.timing?.get(2)?.to?.toInt(), 0)

                    binding.tvTime3.text = "$timeStartFormatted2 to $timeEndFormatted2"


                    val timeStartFormatted1 =
                        formatTime(gymData.gymData?.timing?.get(1)?.from?.toInt(), 0)
                    val timeEndFormatted1 =
                        formatTime(gymData.gymData?.timing?.get(1)?.to?.toInt(), 0)

                    binding.tvTime2.text = "$timeStartFormatted1 to $timeEndFormatted1"


                    val timeStartFormatted =
                        formatTime(gymData.gymData?.timing?.get(0)?.from?.toInt(), 0)
                    val timeEndFormatted =
                        formatTime(gymData.gymData?.timing?.get(0)?.to?.toInt(), 0)

                    binding.tvTime1.text = "$timeStartFormatted to $timeEndFormatted"

                }

                2 -> {
                    binding.tvSeats22.text = "${seats[1]} / ${gymData.gymData?.seats}"
                    binding.tvSeats11.text = "${seats[0]} / ${gymData.gymData?.seats}"
                    val timeStartFormatted1 =
                        formatTime(gymData.gymData?.timing?.get(1)?.from?.toInt(), 0)
                    val timeEndFormatted1 =
                        formatTime(gymData.gymData?.timing?.get(1)?.to?.toInt(), 0)

                    binding.tvTime2.text = "$timeStartFormatted1 to $timeEndFormatted1"


                    val timeStartFormatted =
                        formatTime(gymData.gymData?.timing?.get(0)?.from?.toInt(), 0)
                    val timeEndFormatted =
                        formatTime(gymData.gymData?.timing?.get(0)?.to?.toInt(), 0)

                    binding.tvTime1.text = "$timeStartFormatted to $timeEndFormatted"

                    binding.tvSeats33.visibility = View.GONE
                    binding.tvSeats3.visibility = View.GONE
                    binding.tvTime3.visibility = View.GONE
                }

                1 -> {
                    binding.tvSeats11.text = "${seats[0]} / ${gymData.gymData?.seats}"

                    val timeStartFormatted =
                        formatTime(gymData.gymData?.timing?.get(0)?.from?.toInt(), 0)
                    val timeEndFormatted =
                        formatTime(gymData.gymData?.timing?.get(0)?.to?.toInt(), 0)


                    binding.tvTime1.text = "$timeStartFormatted to $timeEndFormatted"

                    binding.tvSeats22.visibility = View.GONE
                    binding.tvSeats2.visibility = View.GONE
                    binding.tvTime2.visibility = View.GONE
                    binding.tvSeats3.visibility = View.GONE
                    binding.tvSeats33.visibility = View.GONE
                    binding.tvTime3.visibility = View.GONE

                }
            }

            val amenities = gymData.gymData?.ammenities
            if (amenities != null) {
                val allAmenities = getAmenitiesWithDrawable(amenities, amenityMappings)
                val adapter = AmenitiesAdapter(this, allAmenities)
                binding.tvAmmenities.adapter = adapter
            }

            val equipments = gymData.gymData?.equipments
            if (equipments != null) {
                val allEquipments = getEquipmentsWithDrawable(equipments, equipmentMappings)
                val adapter = AmenitiesAdapter(this, allEquipments)
                binding.tvEquipments.adapter = adapter
            }



            binding.tvAddress.text =
                "${gymData.gymData?.address?.street}, ${gymData.gymData?.address?.district}, ${gymData.gymData?.address?.state}, ${gymData.gymData?.address?.pincode}"
            val listOfWeekDays = arrayListOf("mon", "tue", "wed", "thu", "fri", "sat", "sun")

            listOfDays = gymData.gymData?.timing?.get(0)?.days
            binding.rvDays.adapter = DaysAdapter(this, listOfDays!!, listOfWeekDays)

        })
    }

    private fun getEquipmentsWithDrawable(
        equipments: List<String>?, equipmentMappings: Map<String, Pair<String, Int>>
    ): List<AmenityItem> {
        val equipmentItems = mutableListOf<AmenityItem>()

        if (equipments == null) {
            return equipmentItems
        }

        equipments.forEach { equipmentId ->
            val equipmentData = equipmentMappings[equipmentId]
            if (equipmentData != null) {
                val (label, drawableResId) = equipmentData
                val drawable = ContextCompat.getDrawable(this, drawableResId)
                if (drawable != null) {
                    equipmentItems.add(AmenityItem(label, drawable))
                }
            }
        }

        return equipmentItems
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