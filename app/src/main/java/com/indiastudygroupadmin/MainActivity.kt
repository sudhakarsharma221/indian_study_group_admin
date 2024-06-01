package com.indiastudygroupadmin

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.indiastudygroupadmin.addLibrary.AddLibraryActivity
import com.indiastudygroupadmin.app_utils.ApiCallsConstant
import com.indiastudygroupadmin.app_utils.IntentUtil
import com.indiastudygroupadmin.databinding.ActivityMainBinding
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
        window.statusBarColor = Color.WHITE
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("TOKENFIREBASE", it.result.toString())
            }
        }


        viewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            IntentUtil.startIntent(this, SignInActivity())
            finish()
        }
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
        binding.addLibrary.setOnClickListener {
            IntentUtil.startIntent(this, AddLibraryActivity())
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        ApiCallsConstant.apiCallsOnceHome = false
        ApiCallsConstant.apiCallsOnceLibrary = false
    }
}