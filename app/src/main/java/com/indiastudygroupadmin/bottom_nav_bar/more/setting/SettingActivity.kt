package com.indiastudygroupadmin.bottom_nav_bar.more.setting

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.app_utils.IntentUtil
import com.indiastudygroupadmin.bottom_nav_bar.more.setting.screens.AboutUsActivity
import com.indiastudygroupadmin.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        initListener()
    }

    private fun initListener() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.aboutUs.setOnClickListener {
            IntentUtil.startIntent(this, AboutUsActivity())
        }
    }
}