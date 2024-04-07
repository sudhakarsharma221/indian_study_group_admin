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
import com.indiastudygroupadmin.databinding.ActivitySplashScreenBinding
import com.indiastudygroupadmin.registerScreen.FillUserDetailsActivity
import com.indiastudygroupadmin.registerScreen.SignInActivity

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var viewModel: UserDetailsViewModel
    private lateinit var userData: UserDetailsResponseModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.BLACK
        auth = FirebaseAuth.getInstance()

        viewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
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
        viewModel.callGetUserDetails(userId)
    }

    private fun observerUserDetailsApiResponse() {
        viewModel.userDetailsResponse.observe(this, Observer {
            userData = it
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
        viewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        viewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
    }
}