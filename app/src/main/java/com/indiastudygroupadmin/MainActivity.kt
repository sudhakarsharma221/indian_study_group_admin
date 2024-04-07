package com.indiastudygroupadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.indiastudygroupadmin.app_utils.IntentUtil
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.databinding.ActivityMainBinding
import com.indiastudygroupadmin.registerScreen.FillUserDetailsActivity
import com.indiastudygroupadmin.registerScreen.SignInActivity
import com.indiastudygroupadmin.userDetailsApi.viewModel.UserDetailsViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: UserDetailsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            IntentUtil.startIntent(this, SignInActivity())
            finish()
        }

        val navView: BottomNavigationView = binding.navView
//
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_chat, R.id.navigation_profile
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
//        callGetUserDetailsApi(auth.currentUser!!.uid)
//        observeProgress()
//        observerErrorMessageApiResponse()
//        observerUserDetailsApiResponse()
    }

    private fun callGetUserDetailsApi(userId: String?) {
        viewModel.callGetUserDetails(userId)
    }

//    private fun observerUserDetailsApiResponse() {
//        viewModel.userDetailsResponse.observe(this, Observer {
//            Log.d("RESPONSEDONEMAINACTIVITY", it.toString())
//        })
//    }

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