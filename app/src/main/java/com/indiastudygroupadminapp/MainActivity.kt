package com.indiastudygroupadminapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.indiastudygroupadminapp.addGym.AddGymActivity
import com.indiastudygroupadminapp.addLibrary.AddLibraryActivity
import com.indiastudygroupadminapp.app_utils.ApiCallsConstant
import com.indiastudygroupadminapp.app_utils.IntentUtil
import com.indiastudygroupadminapp.databinding.ActivityMainBinding
import com.indiastudygroupadminapp.registerScreen.SignInActivity
import com.indiastudygroupadminapp.userDetailsApi.viewModel.UserDetailsViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userDetailsViewModel: UserDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
            }
        }
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            IntentUtil.startIntent(this, SignInActivity())
            finish()
        }
        val userData = userDetailsViewModel.getUserDetailsResponse()

        if (userData?.authType == "gym owner") {
            binding.navView.visibility = View.GONE
            val navView: BottomNavigationView = binding.navView2

            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            navView.setupWithNavController(navController)
        } else if (userData?.authType == "library owner") {
            binding.navView2.visibility = View.GONE
            val navView: BottomNavigationView = binding.navView

            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            navView.setupWithNavController(navController)

        }

        binding.addLibrary.setOnClickListener {
            if (userData?.authType == "gym owner") {
                IntentUtil.startIntent(this, AddGymActivity())
            } else if (userData?.authType == "library owner") {
                IntentUtil.startIntent(this, AddLibraryActivity())
            }
        }

    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
        ApiCallsConstant.apiCallsOnceHome = false
        ApiCallsConstant.apiCallsOnceLibrary = false
    }
}