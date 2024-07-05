package com.indiastudygroupadminapp.bottom_nav_bar.gym.ui

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.indiastudygroupadminapp.app_utils.ToastUtil
import com.indiastudygroupadminapp.bottom_nav_bar.gym.model.Trainer
import com.indiastudygroupadminapp.bottom_nav_bar.gym.ui.adapter.CoachDetailsAdapter
import com.indiastudygroupadminapp.databinding.ActivityCoachDetailsBinding

class CoachDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCoachDetailsBinding
    private lateinit var trainers: ArrayList<Trainer>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoachDetailsBinding.inflate(layoutInflater)
        window.statusBarColor = Color.WHITE
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }
        val receivedIntent = intent

        if (receivedIntent.hasExtra("Trainers")) {
            val trainer: ArrayList<Trainer>? =
                receivedIntent.getParcelableArrayListExtra("Trainers")
            trainer?.let {
                trainers = it
                initListener()
                1
            }
        } else {
            ToastUtil.makeToast(this, "Trainers not found")
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initListener() {
        binding.reviewRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = CoachDetailsAdapter(this, trainers)
        binding.reviewRecyclerView.adapter = adapter
    }
}