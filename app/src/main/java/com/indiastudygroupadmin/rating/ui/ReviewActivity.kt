package com.indiastudygroupadmin.rating.ui

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.bottom_nav_bar.library.model.Reviews
import com.indiastudygroupadmin.bottom_nav_bar.library.ui.adapter.ReviewAdapter
import com.indiastudygroupadmin.databinding.ActivityReviewBinding

class ReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding
    private lateinit var reviews: ArrayList<Reviews>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        window.statusBarColor = Color.WHITE
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }
        val receivedIntent = intent

        if (receivedIntent.hasExtra("Reviews")) {
            val review: ArrayList<Reviews>? = receivedIntent.getParcelableArrayListExtra("Reviews")
            review?.let {
                reviews = it
                initListener()
                1
            }
        } else {
            ToastUtil.makeToast(this, "Reviews not found")
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initListener() {
        binding.reviewRecyclerView.layoutManager = LinearLayoutManager(this)
        Log.d("ReviewList", reviews.toString())
        val adapter = ReviewAdapter(this, reviews)
        binding.reviewRecyclerView.adapter = adapter
    }
}