package com.indiastudygroupadmin.add_regular_student

import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.databinding.ActivityAddRegularStudentBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class AddRegularStudentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddRegularStudentBinding
    private var selectedTimeFromList = ""
    private lateinit var selectedTimeButton: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRegularStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        initListener()
        focusChangeListeners()

    }

    private fun initListener() {
        binding.chooseDate.setOnClickListener {
            binding.requireDate.visibility = View.GONE
            showDatePickerDialog()
        }


        setButtonState(binding.buttonMorning, false)
        setButtonState(binding.buttonAfternoon, false)
        setButtonState(binding.buttonEvening, false)

        binding.buttonMorning.setOnClickListener {
            binding.requireTime.visibility = View.GONE
            toggleButtonState(binding.buttonMorning)
        }
        binding.buttonAfternoon.setOnClickListener {
            binding.requireTime.visibility = View.GONE
            toggleButtonState(binding.buttonAfternoon)
        }
        binding.buttonEvening.setOnClickListener {
            binding.requireTime.visibility = View.GONE
            toggleButtonState(binding.buttonEvening)
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
                ToastUtil.makeToast(this, "DONE...")
            }
        }
        binding.backButton.setOnClickListener {
            finish()
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

                // Check if selected date is a future date
                if (selectedDateCalendar.after(Calendar.getInstance())) {
                    // Do something with the selected date
                    val formattedDate =
                        SimpleDateFormat("dd/MM/yyyy").format(selectedDateCalendar.time)
                    binding.chooseDate.text = formattedDate
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
}