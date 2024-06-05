package com.indiastudygroupadmin.bottom_nav_bar.attendance.ui

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.bottom_nav_bar.attendance.ui.adapter.SeatAdapter
import com.indiastudygroupadmin.bottom_nav_bar.library.model.LibraryResponseItem
import com.indiastudygroupadmin.databinding.ActivityCurrentAttendanceBinding
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsResponseModel
import com.indiastudygroupadmin.userDetailsApi.viewModel.UserDetailsViewModel
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class CurrentAttendanceActivity : AppCompatActivity() {
    private var timingSize = 0
    private lateinit var libraryData: LibraryResponseItem
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var userData: UserDetailsResponseModel
    private var selectedTimingFromList = ""
    private lateinit var adapter: SeatAdapter
    private lateinit var selectedTimingButton: TextView
    var slot1Time = ""
    var slot2Time = ""
    var slot3Time = ""
    private var selectedSeat: Int = 0
    private lateinit var binding: ActivityCurrentAttendanceBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrentAttendanceBinding.inflate(layoutInflater)
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        window.statusBarColor = Color.WHITE
        setContentView(binding.root)
        val receivedIntent = intent

        if (receivedIntent.hasExtra("LibraryData")) {
            val userDetails: LibraryResponseItem? = receivedIntent.getParcelableExtra("LibraryData")
            userDetails?.let {
                libraryData = it
                initListener()
                1
            }
        } else {
            ToastUtil.makeToast(this, "Library Data not found")
            finish()
        }
        observeProgress()
        observerErrorMessageApiResponse()
        observerUserDetailsApiResponse()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initListener() {
        ToastUtil.makeToast(this, "Select a Slot")
        binding.recyclerView.layoutManager = GridLayoutManager(this, 6)

        setButtonState(binding.buttonSlot1, false)
        setButtonState(binding.buttonSlot2, false)
        setButtonState(binding.buttonSlot3, false)

        binding.buttonSlot1.setOnClickListener {
            toggleButtonState(binding.buttonSlot1)
        }
        binding.buttonSlot2.setOnClickListener {
            toggleButtonState(binding.buttonSlot2)
        }
        binding.buttonSlot3.setOnClickListener {
            toggleButtonState(binding.buttonSlot3)
        }

        val timing = libraryData.timing
        timingSize = timing.size
        when (timing.size) {
            3 -> {
                val timeStartFormatted2 = formatTime(timing[2].from?.toInt(), 0)
                val timeEndFormatted2 = formatTime(timing[2].to?.toInt(), 0)
                slot3Time = "$timeStartFormatted2 to $timeEndFormatted2"
                binding.buttonSlot3.text = "$timeStartFormatted2 to $timeEndFormatted2"


                val timeStartFormatted1 = formatTime(timing[1].from?.toInt(), 0)
                val timeEndFormatted1 = formatTime(timing[1].to?.toInt(), 0)
                slot2Time = "$timeStartFormatted1 to $timeEndFormatted1"
                binding.buttonSlot2.text = "$timeStartFormatted1 to $timeEndFormatted1"


                val timeStartFormatted = formatTime(timing[0].from?.toInt(), 0)
                val timeEndFormatted = formatTime(timing[0].to?.toInt(), 0)
                slot1Time = "$timeStartFormatted to $timeEndFormatted"
                binding.buttonSlot1.text = "$timeStartFormatted to $timeEndFormatted"
            }

            2 -> {


                val timeStartFormatted1 = formatTime(timing[1].from?.toInt(), 0)
                val timeEndFormatted1 = formatTime(timing[1].to?.toInt(), 0)
                slot2Time = "$timeStartFormatted1 to $timeEndFormatted1"

                binding.buttonSlot2.text = "$timeStartFormatted1 to $timeEndFormatted1"


                val timeStartFormatted = formatTime(timing[0].from?.toInt(), 0)
                val timeEndFormatted = formatTime(timing[0].to?.toInt(), 0)
                slot1Time = "$timeStartFormatted to $timeEndFormatted"

                binding.buttonSlot1.text = "$timeStartFormatted to $timeEndFormatted"

                binding.buttonSlot3.visibility = View.GONE
                binding.tvslot3.visibility = View.GONE

            }

            1 -> {

                val timeStartFormatted = formatTime(timing[0].from?.toInt(), 0)
                val timeEndFormatted = formatTime(timing[0].to?.toInt(), 0)
                slot1Time = "$timeStartFormatted to $timeEndFormatted"

                binding.buttonSlot1.text = "$timeStartFormatted to $timeEndFormatted"

                binding.buttonSlot2.visibility = View.GONE
                binding.buttonSlot3.visibility = View.GONE
                binding.tvslot2.visibility = View.GONE
                binding.tvslot3.visibility = View.GONE

            }
        }


        val currentInstant = Instant.now()
        val formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC)
        val formattedDate = formatter.format(currentInstant)




        binding.backButton.setOnClickListener {
            finish()
        }
        binding.historyButton.setOnClickListener {
            val intent = Intent(this, AttendanceHistoryActivity::class.java)
            intent.putExtra("LibraryData", libraryData)
            startActivity(intent)
        }

    }

    private fun toggleButtonState(textView: TextView) {
        // Deselect previously selected button
        if (::selectedTimingButton.isInitialized) {
            selectedTimingButton.isSelected = false
            setButtonState(selectedTimingButton, false)
        }
        // Select the clicked button
        textView.isSelected = true
        setButtonState(textView, true)
        selectedTimingButton = textView
        val text = textView.text.toString()
        val seatDetailsListSize = libraryData.seatDetails.size
        selectedTimingFromList = text
        when (timingSize) {
            3 -> {
                when (selectedTimingFromList) {
                    slot1Time -> {
                        binding.tvSeats.text =
                            "${libraryData.vacantSeats?.get(0)}/${libraryData.seats} seats vacant"

                        adapter = SeatAdapter(
                            this,
                            libraryData.seats!!,
                            libraryData.vacantSeats?.get(0)!!,
                            libraryData.seatDetails
                        ) {
                            selectedSeat = it
                            libraryData.seatDetails.subList(0, libraryData.seats!!)
                                .forEach { seat ->
                                    if (seat.seatNumber == selectedSeat && !seat.bookedBy.isNullOrEmpty()) {
                                        callUserDetailsApi(seat.bookedBy)
                                    }
                                }
                        }
                        binding.recyclerView.adapter = adapter

                    }

                    slot2Time -> {
                        binding.tvSeats.text =
                            "${libraryData.vacantSeats?.get(1)}/${libraryData.seats} seats vacant"
                        adapter = SeatAdapter(
                            this,
                            libraryData.seats!!,
                            libraryData.vacantSeats?.get(1)!!,
                            libraryData.seatDetails
                        ) {
                            selectedSeat = it
                            libraryData.seatDetails.subList(
                                libraryData.seats!!, 2 * libraryData.seats!!
                            ).forEach { seat ->
                                if (seat.seatNumber == selectedSeat && !seat.bookedBy.isNullOrEmpty()) {
                                    callUserDetailsApi(seat.bookedBy)
                                }
                            }
                        }
                        binding.recyclerView.adapter = adapter

                    }


                    slot3Time -> {
                        binding.tvSeats.text =
                            "${libraryData.vacantSeats?.get(2)}/${libraryData.seats} seats vacant"
                        adapter = SeatAdapter(
                            this,
                            libraryData.seats!!,
                            libraryData.vacantSeats?.get(2)!!,
                            libraryData.seatDetails
                        ) {
                            selectedSeat = it
                            libraryData.seatDetails.subList(
                                2 * libraryData.seats!!, seatDetailsListSize - 1
                            ).forEach { seat ->
                                if (seat.seatNumber == selectedSeat && !seat.bookedBy.isNullOrEmpty()) {
                                    callUserDetailsApi(seat.bookedBy)
                                }
                            }
                        }
                        binding.recyclerView.adapter = adapter
                    }

                    else -> {
                        ToastUtil.makeToast(this, "Select a slot")
                    }
                }
            }

            2 -> {
                when (selectedTimingFromList) {
                    slot1Time -> {
                        binding.tvSeats.text =
                            "${libraryData.vacantSeats?.get(0)}/${libraryData.seats} seats vacant"

                        adapter = SeatAdapter(
                            this,
                            libraryData.seats!!,
                            libraryData.vacantSeats?.get(0)!!,
                            libraryData.seatDetails
                        ) {
                            selectedSeat = it
                            libraryData.seatDetails.subList(0, libraryData.seats!!)
                                .forEach { seat ->
                                    if (seat.seatNumber == selectedSeat && !seat.bookedBy.isNullOrEmpty()) {
                                        callUserDetailsApi(seat.bookedBy)
                                    }
                                }
                        }
                        binding.recyclerView.adapter = adapter

                    }

                    slot2Time -> {

                        binding.tvSeats.text =
                            "${libraryData.vacantSeats?.get(1)}/${libraryData.seats} seats vacant"

                        adapter = SeatAdapter(
                            this,
                            libraryData.seats!!,
                            libraryData.vacantSeats?.get(1)!!,
                            libraryData.seatDetails
                        ) {
                            selectedSeat = it
                            libraryData.seatDetails.subList(
                                libraryData.seats!!, seatDetailsListSize - 1
                            ).forEach { seat ->
                                if (seat.seatNumber == selectedSeat && !seat.bookedBy.isNullOrEmpty()) {
                                    callUserDetailsApi(seat.bookedBy)
                                }
                            }
                        }
                        binding.recyclerView.adapter = adapter

                    }

                    else -> {
                        ToastUtil.makeToast(this, "Select a slot")
                    }
                }
            }

            1 -> {
                when (selectedTimingFromList) {
                    slot1Time -> {
                        binding.tvSeats.text =
                            "${libraryData.vacantSeats?.get(0)}/${libraryData.seats} seats vacant"

                        adapter = SeatAdapter(
                            this,
                            libraryData.seats!!,
                            libraryData.vacantSeats?.get(0)!!,
                            libraryData.seatDetails
                        ) {
                            Log.d("SEATNUMBER", it.toString())
                            selectedSeat = it
                            libraryData.seatDetails.forEach { seat ->
                                Log.d("SEATNUMBERRR", seat.seatNumber.toString())
                                if (seat.seatNumber == selectedSeat && !seat.bookedBy.isNullOrEmpty()) {
                                    callUserDetailsApi(seat.bookedBy)
                                }
                            }
                        }
                        binding.recyclerView.adapter = adapter

                    }

                    else -> {
                        ToastUtil.makeToast(this, "Select a slot")
                    }
                }
            }
        }
    }

    private fun callUserDetailsApi(id: String?) {
        if (!id.isNullOrEmpty()) {
            userDetailsViewModel.callGetUserIdDetails(id)
        } else {
            ToastUtil.makeToast(this, "Invalid user ID")
        }
    }

    private fun setButtonState(textView: TextView, isSelected: Boolean) {
        if (isSelected) {
            textView.setBackgroundResource(R.drawable.background_button) // Change to selected background
            textView.setTextColor(
                ContextCompat.getColor(
                    this, android.R.color.white
                )
            ) // Change to white text color
        } else {
            textView.setBackgroundResource(R.drawable.background_button_color_change) // Revert to normal background
            textView.setTextColor(
                Color.parseColor("#747688")
            ) // Revert to original text color
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


    private fun observerUserDetailsApiResponse() {
        userDetailsViewModel.userDetailsIdResponse.observe(this, Observer {
            userData = it
            Glide.with(this).load(userData.photo).placeholder(R.drawable.profile)
                .error(R.drawable.profile).into(binding.ivProfile)
            binding.studentName.text = userData.name
            binding.studentName.textSize = 14f
            binding.ivProfile.visibility = View.VISIBLE
            binding.studentName.setTextColor(Color.parseColor("#120D26"))

        })
    }

    private fun observeProgress() {
        userDetailsViewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.progressBar2.visibility = View.VISIBLE
                binding.tvTapToViewDetails.visibility = View.GONE
                binding.userDetailsLayout.visibility = View.GONE
            } else {
                binding.progressBar2.visibility = View.GONE
                binding.tvTapToViewDetails.visibility = View.GONE
                binding.userDetailsLayout.visibility = View.VISIBLE
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        userDetailsViewModel.errorMessage.observe(this, Observer {
            binding.studentName.text =
                "Error!!\nEither student do not exist anymore or there is some server error"
            binding.ivProfile.visibility = View.GONE
            binding.studentName.textSize = 12f
            binding.studentName.setTextColor(Color.parseColor("#FB5C5C"))
        })
    }
}