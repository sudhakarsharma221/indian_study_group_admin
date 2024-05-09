package com.indiastudygroupadmin.fillDetails

import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.indiastudygroupadmin.userDetailsApi.model.Address
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsPutRequestBodyModel
import com.indiastudygroupadmin.userDetailsApi.viewModel.UserDetailsViewModel
import com.indiastudygroupadmin.MainActivity
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.app_utils.IntentUtil
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.databinding.ActivityFillUserDetailsBinding
import com.indiastudygroupadmin.pincode.PincodeViewModel
import java.io.File

class FillUserDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFillUserDetailsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFillUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE

    }
}