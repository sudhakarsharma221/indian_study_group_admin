package com.indiastudygroupadmin.message.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.databinding.ActivityMessageBinding

class MessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        initListener()
    }

    private fun initListener() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}