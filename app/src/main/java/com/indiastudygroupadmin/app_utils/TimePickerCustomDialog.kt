package com.indiastudygroupadmin.app_utils

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimePickerCustomDialog(private val textView: TextView) : DialogFragment(),
    TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(requireActivity(), this, hour, minute, false)
    }

    override fun onTimeSet(view: android.widget.TimePicker?, hourOfDay: Int, minute: Int) {
        // Convert 24-hour time to 12-hour format
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        // Format the selected time
        val formattedTime = sdf.format(calendar.time)

        // Set the selected time to the TextView
        textView.text = formattedTime
    }
}