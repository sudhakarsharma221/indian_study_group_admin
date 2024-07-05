package com.indiastudygroupadminapp.bottom_nav_bar.attendance.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.indiastudygroupadminapp.R
import com.indiastudygroupadminapp.app_utils.ToastUtil
import com.indiastudygroupadminapp.bottom_nav_bar.gym.model.GymResponseItem
import com.indiastudygroupadminapp.bottom_nav_bar.library.model.History
import com.indiastudygroupadminapp.bottom_nav_bar.library.model.LibraryResponseItem
import com.indiastudygroupadminapp.bottom_nav_bar.library.model.Timing
import com.indiastudygroupadminapp.databinding.ActivityAttendanceHistoryBinding
import com.indiastudygroupadminapp.databinding.ErrorBottomDialogLayoutBinding
import com.indiastudygroupadminapp.userDetailsApi.viewModel.UserDetailsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar

class AttendanceHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAttendanceHistoryBinding
    private lateinit var libraryData: LibraryResponseItem
    private lateinit var gymData: GymResponseItem
    private var selectedTimeFromList = ""
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private var slot = 0
    private lateinit var selectedTimeButton: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        window.statusBarColor = Color.WHITE
        val receivedIntent = intent

        val userData = userDetailsViewModel.getUserDetailsResponse()
        if (userData?.authType == "gym owner") {
            if (receivedIntent.hasExtra("GymData")) {
                val userDetails: GymResponseItem? = receivedIntent.getParcelableExtra("GymData")
                userDetails?.let {
                    gymData = it
                    initListenerGym()
                    1
                }
            } else {
                ToastUtil.makeToast(this, "Library Data not found")
                finish()
            }
        } else if (userData?.authType == "library owner") {

            if (receivedIntent.hasExtra("LibraryData")) {
                val userDetails: LibraryResponseItem? =
                    receivedIntent.getParcelableExtra("LibraryData")
                userDetails?.let {
                    libraryData = it
                    initListenerLibrary()
                    1
                }
            } else {
                ToastUtil.makeToast(this, "Library Data not found")
                finish()
            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initListenerGym() {
        val timing = gymData.timing

        setTimeFunction(timing)
        binding.proceedButton.setOnClickListener {
            val date = binding.chooseDate.text.toString()
            val historyList = ArrayList<History>()
            if (date == "Choose Date") {
                binding.requireDate.visibility = View.VISIBLE
            } else if (selectedTimeFromList.isEmpty()) {
                binding.requireTime.visibility = View.VISIBLE
            } else {
                gymData.history!!.forEach { history ->
                    if (history.date?.substring(0, 10) == date && slot == history.slot) {
                        historyList.add(history)
                    }
                }
                if (historyList.isNotEmpty()) {
                    val intent = Intent(this, AttendanceHistoryShowActivity::class.java)

                    intent.putExtra("historyList", historyList)
                    intent.putExtra("date", date)
                    intent.putExtra("slotNumber", slot)
                    intent.putExtra("slot", selectedTimeFromList)
                    intent.putExtra("totalSeats", gymData.seats)
                    intent.putExtra(
                        "vacantSeats", (gymData.seats?.minus(
                            historyList.size
                        ))
                    )
                    startActivity(intent)
                } else {
                    showErrorBottomDialog("You do not have any booking for this date")
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initListenerLibrary() {
        val timing = libraryData.timing

        setTimeFunction(timing)
        binding.proceedButton.setOnClickListener {
            val date = binding.chooseDate.text.toString()
            val historyList = ArrayList<History>()
            if (date == "Choose Date") {
                binding.requireDate.visibility = View.VISIBLE
            } else if (selectedTimeFromList.isEmpty()) {
                binding.requireTime.visibility = View.VISIBLE
            } else {
                libraryData.history!!.forEach { history ->
                    if (history.date?.substring(0, 10) == date && slot == history.slot) {
                        historyList.add(history)
                    }
                }
                if (historyList.isNotEmpty()) {
                    val intent = Intent(this, AttendanceHistoryShowActivity::class.java)

                    intent.putExtra("historyList", historyList)
                    intent.putExtra("date", date)
                    intent.putExtra("slotNumber", slot)
                    intent.putExtra("slot", selectedTimeFromList)
                    intent.putExtra("totalSeats", libraryData.seats)
                    intent.putExtra(
                        "vacantSeats", (libraryData.seats?.minus(
                            historyList.size
                        ))
                    )
                    startActivity(intent)
                } else {
                    showErrorBottomDialog("You do not have any booking for this date")
                }
            }
        }
    }

    private fun setTimeFunction(timing: ArrayList<Timing>) {
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

                binding.slot3.visibility = View.GONE
                binding.buttonSlot3.visibility = View.GONE
            }

            1 -> {

                val timeStartFormatted = formatTime(timing[0].from?.toInt(), 0)
                val timeEndFormatted = formatTime(timing[0].to?.toInt(), 0)

                binding.buttonSlot1.text = "Slot 1 : $timeStartFormatted to $timeEndFormatted"

                binding.slot2.visibility = View.GONE
                binding.slot3.visibility = View.GONE
                binding.buttonSlot2.visibility = View.GONE
                binding.buttonSlot3.visibility = View.GONE
            }
        }



        setButtonState(binding.buttonSlot1, false)
        setButtonState(binding.buttonSlot2, false)
        setButtonState(binding.buttonSlot3, false)

        binding.buttonSlot1.setOnClickListener {
            slot = 0
            binding.requireTime.visibility = View.GONE
            toggleButtonState(binding.buttonSlot1)
        }
        binding.buttonSlot2.setOnClickListener {
            slot = 1
            binding.requireTime.visibility = View.GONE
            toggleButtonState(binding.buttonSlot2)
        }
        binding.buttonSlot3.setOnClickListener {
            slot = 2
            binding.requireTime.visibility = View.GONE
            toggleButtonState(binding.buttonSlot3)
        }




        binding.chooseDate.setOnClickListener {
            binding.requireDate.visibility = View.GONE
            showDatePickerDialog()
        }


        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun showErrorBottomDialog(message: String) {
        val bottomDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val dialogBinding = ErrorBottomDialogLayoutBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(true)
        bottomDialog.show()
        dialogBinding.messageTv.text = message
        dialogBinding.continueButton.setOnClickListener {
            bottomDialog.dismiss()
        }
    }


    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDateCalendar = Calendar.getInstance()
                selectedDateCalendar.set(selectedYear, selectedMonth, selectedDay)

                val formattedDate = SimpleDateFormat("yyyy-MM-dd").format(selectedDateCalendar.time)
                binding.chooseDate.text = formattedDate

            }, year, month, day
        )
//
//        // Set minimum date to current date
//        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun toggleButtonState(textView: TextView) {
        // Deselect previously selected button
        if (::selectedTimeButton.isInitialized) {
            selectedTimeButton.isSelected = false
            setButtonState(selectedTimeButton, false)
        }
        // Select the clicked button
        textView.isSelected = true
        setButtonState(textView, true)
        selectedTimeButton = textView
        selectedTimeFromList = textView.text.toString()
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
}