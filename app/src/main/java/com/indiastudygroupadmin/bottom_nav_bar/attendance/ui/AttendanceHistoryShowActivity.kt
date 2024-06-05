package com.indiastudygroupadmin.bottom_nav_bar.attendance.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.bottom_nav_bar.attendance.ui.adapter.SeatAdapter
import com.indiastudygroupadmin.bottom_nav_bar.attendance.ui.adapter.SeatAdapterHistory
import com.indiastudygroupadmin.bottom_nav_bar.library.model.History
import com.indiastudygroupadmin.bottom_nav_bar.library.model.SeatDetails
import com.indiastudygroupadmin.databinding.ActivityAttendanceHistoryShowBinding
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsResponseModel
import com.indiastudygroupadmin.userDetailsApi.viewModel.UserDetailsViewModel

class AttendanceHistoryShowActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAttendanceHistoryShowBinding
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var userData: UserDetailsResponseModel
    private lateinit var adapter: SeatAdapterHistory
    private var selectedSeat: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceHistoryShowBinding.inflate(layoutInflater)
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        window.statusBarColor = Color.WHITE
        setContentView(binding.root)
        initListener()
        observeProgress()
        observerErrorMessageApiResponse()
        observerUserDetailsApiResponse()
    }

    private fun initListener() {
        binding.recyclerView.layoutManager = GridLayoutManager(this, 6)
        binding.backButton.setOnClickListener {
            finish()
        }

        val date = intent.getStringExtra("date")
        val slot = intent.getStringExtra("slot")
        val vacantSeats = intent.getIntExtra("vacantSeats", 0)
        val totalSeats = intent.getIntExtra("totalSeats", 0)
        val historyList: ArrayList<History>? = intent.getParcelableArrayListExtra("historyList")

        binding.textView.text = "Attendance\n$date\n$slot"

        binding.tvSeats.text = "$vacantSeats/$totalSeats seats vacant"
        adapter = SeatAdapterHistory(
            this, totalSeats, vacantSeats
        ) {
            selectedSeat = it
            historyList?.forEach { seat ->
                if (seat.seatNo == selectedSeat - 1 && !seat.studentId.isNullOrEmpty()) {
                    callUserDetailsApi(seat.studentId)
                }
            }
        }
        binding.recyclerView.adapter = adapter

    }

    private fun callUserDetailsApi(id: String?) {
        if (!id.isNullOrEmpty()) {
            userDetailsViewModel.callGetUserIdDetails(id)
        } else {
            ToastUtil.makeToast(this, "Invalid user ID")
        }
    }

    private fun observerUserDetailsApiResponse() {
        userDetailsViewModel.userDetailsIdResponse.observe(this, Observer {
            userData = it
            Glide.with(this).load(userData.photo).placeholder(R.drawable.profile)
                .error(R.drawable.profile).into(binding.ivProfile)
            binding.studentName.text = userData.name
            binding.studentName.textSize = 14f
            binding.ivProfile.visibility = View.VISIBLE
            binding.studentName.setTextColor(Color.parseColor("#120D26"))

        })
    }


    private fun observeProgress() {
        userDetailsViewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.progressBar2.visibility = View.VISIBLE
                binding.tvTapToViewDetails.visibility = View.GONE
                binding.userDetailsLayout.visibility = View.GONE
            } else {
                binding.progressBar2.visibility = View.GONE
                binding.tvTapToViewDetails.visibility = View.GONE
                binding.userDetailsLayout.visibility = View.VISIBLE
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        userDetailsViewModel.errorMessage.observe(this, Observer {
            binding.studentName.text =
                "Error!!\nEither student do not exist anymore or there is some server error"
            binding.ivProfile.visibility = View.GONE
            binding.studentName.textSize = 12f
            binding.studentName.setTextColor(Color.parseColor("#FB5C5C"))
        })
    }
}