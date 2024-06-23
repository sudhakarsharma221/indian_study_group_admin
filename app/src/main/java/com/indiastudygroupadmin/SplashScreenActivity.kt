package com.indiastudygroupadmin

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsResponseModel
import com.indiastudygroupadmin.userDetailsApi.viewModel.UserDetailsViewModel
import com.indiastudygroupadmin.app_utils.IntentUtil
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.bottom_nav_bar.library.model.LibraryResponseItem
import com.indiastudygroupadmin.bottom_nav_bar.library.viewModel.LibraryViewModel
import com.indiastudygroupadmin.databinding.ActivitySplashScreenBinding
import com.indiastudygroupadmin.fillDetails.FillUserDetailsActivity
import com.indiastudygroupadmin.registerScreen.SignInActivity
import java.util.ArrayList
import java.util.LinkedList
import java.util.Queue

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    //    private lateinit var libraryDetailsViewModel: LibraryViewModel
//    private var libraryList: ArrayList<LibraryResponseItem> = arrayListOf()
//    private val libraryIdQueue: Queue<String> = LinkedList()
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var userData: UserDetailsResponseModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        auth = FirebaseAuth.getInstance()
//        libraryDetailsViewModel = ViewModelProvider(this)[LibraryViewModel::class.java]


        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        Handler(Looper.getMainLooper()).postDelayed({
            if (loginCheck()) {
                callGetUserDetailsApi(auth.currentUser!!.uid)
            } else {
                IntentUtil.startIntent(this@SplashScreenActivity, SignInActivity())
                finish()
            }
        }, 1000)



        observerUserDetailsApiResponse()
        observeProgress()
        observerErrorMessageApiResponse()
    }

    private fun loginCheck(): Boolean {
        return auth.currentUser != null
    }

    private fun callGetUserDetailsApi(userId: String?) {
        userDetailsViewModel.callGetUserDetails(userId)
    }

    private fun observerUserDetailsApiResponse() {
        userDetailsViewModel.userDetailsResponse.observe(this, Observer {
            userData = it
            userDetailsViewModel.setUserDetailsResponse(it)
            if (it.name?.trim().isNullOrEmpty() || it.address?.pincode?.trim().isNullOrEmpty()) {
                IntentUtil.startIntent(this@SplashScreenActivity, FillUserDetailsActivity())
                finish()
            } else {
                IntentUtil.startIntent(this@SplashScreenActivity, MainActivity())
                finish()
            }
        })
    }

    private fun observeProgress() {
        userDetailsViewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        userDetailsViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
    }

//               if (userData.libraries.isNotEmpty()) {
//                userData.libraries.forEach { libraryIdQueue.add(it) }
//                processNextLibraryId()
//            }
}