package com.indiastudygroupadmin.editLibraryRequest

import android.app.Dialog
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.app_utils.IntentUtil
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.databinding.ActivityEditLibraryRequestBinding
import com.indiastudygroupadmin.registerScreen.SignInActivity

class EditLibraryRequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditLibraryRequestBinding
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var auth: FirebaseAuth

    private var photoUrl: String? = ""
    private var storageRef = Firebase.storage
    private var uri: Uri? = null
    private lateinit var imageUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditLibraryRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE


        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
//                binding.ivProfile.visibility = View.VISIBLE
//                binding.ivProfile.setImageURI(uri)
                this.uri = uri
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
        initListener()
    }

    private fun initListener() {
        binding.addImage.setOnClickListener {
            uploadImageDialog()
        }
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun uploadImageDialog() {
        val builder = Dialog(this)
        val view = layoutInflater.inflate(R.layout.upload_image_dialog, null)
        builder.setContentView(view)
        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)
        builder.show()
        builder.setCancelable(true)
        val upload = view.findViewById<ImageView>(R.id.upload)
        val browse = view.findViewById<TextView>(R.id.browseButton)
        upload.setOnClickListener {
            builder.dismiss()
        }
        browse.setOnClickListener {
            builder.dismiss()
        }
    }


    //  private fun uploadImage() {
    //        if (uri == null) {
    //            Toast.makeText(requireContext(), "Please choose an image first", Toast.LENGTH_SHORT)
    //                .show()
    //            return
    //        }
    //
    //        val progressDialog = ProgressDialog(requireContext())
    //        progressDialog.setCancelable(false)
    //        progressDialog.setMessage("Wait while uploading profile")
    //        progressDialog.show()
    //        val imageRef = storageRef.reference.child("images")
    //            .child("${System.currentTimeMillis()} ${auth.currentUser!!.uid}")
    //        imageRef.putFile(uri!!).addOnSuccessListener { task ->
    //            task.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
    //                photoUrl = uri.toString()
    //                progressDialog.dismiss()
    //                callPutUserDetailsApi(
    //                    auth.currentUser!!.uid, UserDetailsPutRequestBodyModel(
    //                        name.trim(), photoUrl ?: "", Address(state.trim(), city, pincode), bio ?: ""
    //                    )
    //                )
    //            }?.addOnFailureListener {
    //                Toast.makeText(
    //                    requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT
    //                ).show()
    //                progressDialog.dismiss()
    //            }
    //        }.addOnFailureListener {
    //            Toast.makeText(
    //                requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT
    //            ).show()
    //            progressDialog.dismiss()
    //        }
    //    }
}