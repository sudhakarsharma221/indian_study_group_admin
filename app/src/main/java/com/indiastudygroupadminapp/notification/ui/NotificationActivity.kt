package com.indiastudygroupadminapp.notification.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.indiastudygroupadminapp.app_utils.ToastUtil
import com.indiastudygroupadminapp.databinding.ActivityNotificationBinding
import com.indiastudygroupadminapp.notification.model.NotificationStatusChangeRequestModel
import com.indiastudygroupadminapp.notification.ui.adapter.NotificationAdapter
import com.indiastudygroupadminapp.notification.viewModel.NotificationViewModel
import com.indiastudygroupadminapp.userDetailsApi.model.Notifications
import com.indiastudygroupadminapp.userDetailsApi.viewModel.UserDetailsViewModel

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter: NotificationAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var notificationList: ArrayList<Notifications>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        notificationViewModel = ViewModelProvider(this)[NotificationViewModel::class.java]
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        window.statusBarColor = Color.WHITE
        setResult(RESULT_OK)
        userDetailsViewModel.callGetUserDetails(auth.currentUser!!.uid)
        initListener()
        observeUserDetails()
        observeProgress()
        observerErrorMessageApiResponse()
        observerNotificationResponse()
    }

    private fun initListener() {

//        binding.apply {
//            markAsRead.setOnClickListener {
//                for (notification in notificationList) {
//                    if (notification.status == "unread") notificationViewModel.callPostChangeNotificationStatus(
//                        auth.currentUser!!.uid,
//                        NotificationStatusChangeRequestModel(notification.id)
//                    )
//                }
//
//            }
//            markAsReadIcon.setOnClickListener {
//                for (notification in notificationList) {
//                    if (notification.status == "unread") notificationViewModel.callPostChangeNotificationStatus(
//                        auth.currentUser!!.uid,
//                        NotificationStatusChangeRequestModel(notification.id)
//                    )
//                }
//            }
//        }


        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        notificationList = arrayListOf()

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun observeUserDetails() {
        userDetailsViewModel.userDetailsResponse.observe(this, Observer {
            notificationList = it?.notifications!!

            if (notificationList.isEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.noNotiAvailable.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.noNotiAvailable.visibility = View.GONE
                adapter = NotificationAdapter(this, notificationList.asReversed()) { id ->
                    notificationViewModel.callPostChangeNotificationStatus(
                        auth.currentUser!!.uid, NotificationStatusChangeRequestModel(id)
                    )
                }
                binding.recyclerView.adapter = adapter
            }
        })
    }

    private fun observerNotificationResponse() {
        notificationViewModel.notificationChangeResponse.observe(this, Observer {
            notificationList = it.updatedUser?.notifications!!

            adapter = NotificationAdapter(this, notificationList.asReversed()) { id ->
                notificationViewModel.callPostChangeNotificationStatus(
                    auth.currentUser!!.uid, NotificationStatusChangeRequestModel(id)
                )
            }
            binding.recyclerView.adapter = adapter
        })
    }

    private fun observeProgress() {

        userDetailsViewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.recyclerView.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        userDetailsViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
        notificationViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
    }

}