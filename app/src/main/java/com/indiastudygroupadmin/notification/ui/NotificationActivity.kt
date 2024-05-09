package com.indiastudygroupadmin.notification.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
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