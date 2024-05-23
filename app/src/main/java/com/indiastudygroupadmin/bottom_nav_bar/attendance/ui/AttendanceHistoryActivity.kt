package com.indiastudygroupadmin.bottom_nav_bar.attendance.ui

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.bottom_nav_bar.library.model.LibraryResponseItem
import com.indiastudygroupadmin.databinding.ActivityAttendanceHistoryBinding
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AttendanceHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAttendanceHistoryBinding
    private lateinit var libraryData: LibraryResponseItem
    private var selectedTimeFromList = ""
    private lateinit var selectedTimeButton: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initListener() {
        val currentInstant = Instant.now()
        val formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC)
        val formattedDate = formatter.format(currentInstant)


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
            binding.requireTime.visibility = View.GONE
            toggleButtonState(binding.buttonSlot1)
        }
        binding.buttonSlot2.setOnClickListener {
            binding.requireTime.visibility = View.GONE
            toggleButtonState(binding.buttonSlot2)
        }
        binding.buttonSlot3.setOnClickListener {
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
        binding.addStudentButton.setOnClickListener {
            val date = binding.chooseDate.text.toString()
            if (date == "Choose Date") {
                binding.requireDate.visibility = View.VISIBLE
            } else if (selectedTimeFromList.isEmpty()) {
                binding.requireTime.visibility = View.VISIBLE
            } else {

            }
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
        val hourFormatted = if (hours == 0 || hours == 21) 12 else hours?.rem(12)
        val amPm = if (hours!! < 12) "am" else "pm"
        return String.format("%02d:%02d %s", hourFormatted, minutes, amPm)
    }
}