package com.indiastudygroupadminapp.bottom_nav_bar.more.student_data.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.indiastudygroupadminapp.databinding.ActivityStudentDataDetailBinding

class StudentDataDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentDataDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDataDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        initListener()
    }

    private fun initListener() {

    }
}