package com.indiastudygroupadminapp.bottom_nav_bar.more.student_data

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.indiastudygroupadminapp.R
import com.indiastudygroupadminapp.app_utils.ToastUtil
import com.indiastudygroupadminapp.databinding.ActivityStudentDataBinding

class StudentDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentDataBinding
    private var selectedItemFromList = "Regular"
    private lateinit var selectedItemButton: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        initListener()
    }

    private fun initListener() {
        binding.backButton.setOnClickListener {
            finish()
        }


        setButtonState(binding.buttonRegular, true)
        setButtonState(binding.buttonNonRegular, false)

        binding.buttonRegular.setOnClickListener {
            toggleButtonState(binding.buttonRegular)
        }
        binding.buttonNonRegular.setOnClickListener {
            toggleButtonState(binding.buttonNonRegular)
        }

        if (selectedItemFromList == "Regular") {
            ToastUtil.makeToast(this, "Regular Student")
        } else {
            ToastUtil.makeToast(this, "Non Regular Student")
        }
    }

    private fun toggleButtonState(textView: TextView) {
        // Deselect previously selected button
        if (::selectedItemButton.isInitialized) {
            selectedItemButton.isSelected = false
            setButtonState(selectedItemButton, false)
        }
        // Select the clicked button
        textView.isSelected = true
        setButtonState(textView, true)
        selectedItemButton = textView
        selectedItemFromList = textView.text.toString()
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

}