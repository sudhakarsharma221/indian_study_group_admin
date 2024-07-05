package com.indiastudygroupadminapp.add_regular_student.ui

import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.indiastudygroupadminapp.R
import com.indiastudygroupadminapp.add_regular_student.model.AddStudentRequestBody
import com.indiastudygroupadminapp.add_regular_student.viewModel.AddRegularStudentViewModel
import com.indiastudygroupadminapp.app_utils.ToastUtil
import com.indiastudygroupadminapp.bottom_nav_bar.library.model.LibraryResponseItem
import com.indiastudygroupadminapp.databinding.ActivityAddRegularStudentBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit

class AddRegularStudentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddRegularStudentBinding
    private var selectedTimeFromList = ""
    private lateinit var selectedTimeButton: TextView
    private lateinit var libraryData: LibraryResponseItem
    private lateinit var viewModel: AddRegularStudentViewModel
    private var selectedDateInMillis: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRegularStudentBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[AddRegularStudentViewModel::class.java]
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        val receivedIntent = intent

        if (receivedIntent.hasExtra("libraryData")) {
            val userDetails: LibraryResponseItem? = receivedIntent.getParcelableExtra("libraryData")
            userDetails?.let {
                libraryData = it
                initListener()
                1
            }
        } else {
            ToastUtil.makeToast(this, "Library Data not found")
            finish()
        }
        focusChangeListeners()
        observeProgress()
        observerErrorMessageApiResponse()
        observerApiResponse()

    }

    private fun initListener() {
        binding.chooseDate.setOnClickListener {
            binding.requireDate.visibility = View.GONE
            showDatePickerDialog()
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

        binding.addStudentButton.setOnClickListener {
            val name = binding.nameEt.text.toString()
            val phoneNo = binding.phoneEt.text.toString()
            val amount = binding.amountPaidEt.text.toString()

            if (name.trim().isEmpty()) {
                binding.nameEt.error = "Empty Field"
            } else if (name.length < 2) {
                binding.nameEt.error = "Enter minimum 2 characters"
            } else if (name.length > 30) {
                binding.nameEt.error = "Enter less than 30 characters"
            } else if (phoneNo.trim().isEmpty()) {
                binding.phoneEt.error = "Empty Field"
            } else if (phoneNo.length < 10) {
                binding.phoneEt.error = "Please Enter Valid Mobile Number"
            } else if (amount.trim().isEmpty()) {
                binding.amountPaidEt.error = "Empty Field"
            } else if (selectedTimeFromList.isEmpty()) {
                binding.requireTime.visibility = View.VISIBLE
            } else if (binding.chooseDate.text == "Choose Date") {
                binding.requireDate.visibility = View.VISIBLE
            } else {
                val currentDateInMillis = Calendar.getInstance().timeInMillis
                val noOfDays = if (selectedDateInMillis != null) {
                    val diff = selectedDateInMillis!! - currentDateInMillis
                    TimeUnit.MILLISECONDS.toDays(diff).toInt()
                } else {
                    0
                }
                callAddRegularStudentApi(
                    AddStudentRequestBody(
                        phoneNo.toLongOrNull(), libraryData.id, noOfDays.toString(), "", "00", "", "00"
                    )
                )
            }
        }
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun callAddRegularStudentApi(
        addStudentRequestBody: AddStudentRequestBody?
    ) {
        viewModel.callAddRegularStudentApi(addStudentRequestBody)
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


    private fun observerApiResponse() {
        viewModel.addRegularStudentResponse.observe(this, Observer {
            ToastUtil.makeToast(this, "Student Details Added")

        })
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

                // Check if selected date is a future date
                if (selectedDateCalendar.after(Calendar.getInstance())) {
                    // Do something with the selected date
                    val formattedDate =
                        SimpleDateFormat("dd/MM/yyyy").format(selectedDateCalendar.time)
                    binding.chooseDate.text = formattedDate
                    selectedDateInMillis = selectedDateCalendar.timeInMillis
                } else {
                    // Show error message or handle the case when selected date is not a future date
                    // For example:
                    Toast.makeText(this, "Please select a future date", Toast.LENGTH_SHORT).show()
                }
            }, year, month, day
        )

        // Set minimum date to current date
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
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

    private fun focusChangeListeners() {


        binding.nameEt.setOnFocusChangeListener { view, b ->
            if (!b) {
                if (binding.nameEt.text.toString().trim()
                        .isNotEmpty() && binding.nameEt.text.toString().length < 2
                ) {
                    binding.nameEt.error = "Enter Minimum 2 Characters"
                } else if (binding.nameEt.text.toString().trim()
                        .isNotEmpty() && binding.nameEt.text.toString().length > 30
                ) {
                    binding.nameEt.error = "Enter Less Than 30 Characters"
                }
            }
        }

        binding.phoneEt.setOnFocusChangeListener { view, b ->
            if (!b) {
                if (binding.phoneEt.text.toString().trim()
                        .isNotEmpty() && binding.phoneEt.text.toString().length < 10
                ) {
                    binding.phoneEt.error = "Enter Valid Mobile No"
                }
            }
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
    fun convertTo24HourFormat(time: String): Int {
        val timeParts = time.split(" ")
        val timeComponent = timeParts[0]
        val amPmComponent = timeParts[1]

        val hourMinute = timeComponent.split(":")
        var hour = hourMinute[0].toInt()
        val minute = hourMinute[1].toInt()

        if (amPmComponent.equals("PM", ignoreCase = true) && hour != 12) {
            hour += 12
        } else if (amPmComponent.equals("AM", ignoreCase = true) && hour == 12) {
            hour = 0
        }

        return hour
    }

}