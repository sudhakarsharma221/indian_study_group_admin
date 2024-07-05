package com.indiastudygroupadminapp.fillDetails.screens

import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.indiastudygroupadminapp.R
import com.indiastudygroupadminapp.app_utils.ToastUtil
import com.indiastudygroupadminapp.databinding.FragmentPhotoFillBinding
import com.indiastudygroupadminapp.userDetailsApi.model.Address
import com.indiastudygroupadminapp.userDetailsApi.model.UserDetailsPutRequestBodyModel
import com.indiastudygroupadminapp.userDetailsApi.viewModel.UserDetailsViewModel
import java.io.File


class PhotoFillFragment : Fragment() {
    private lateinit var binding: FragmentPhotoFillBinding
    private lateinit var auth: FirebaseAuth

    private var photoUrl: String? = ""
    private var storageRef = Firebase.storage
    private var uri: Uri? = null
    private lateinit var imageUri: Uri
    private val contract =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                binding.ivProfile.visibility = View.VISIBLE
                binding.ivProfile.setImageURI(imageUri)
                uri = imageUri
            } else {
                binding.ivProfile.visibility = View.VISIBLE
                binding.ivProfile.setImageURI(uri)
                uri = null
            }
        }

    private val requestForPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                contract.launch(imageUri)
            } else {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                    showRationaleDialog()
                } else {
                    val message =
                        "You've denied camera permission twice. To enable it, open app settings."
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                }
            }
        }
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    private lateinit var name: String
    private lateinit var pincode: String
    private lateinit var state: String
    private lateinit var city: String
    private lateinit var bio: String
    lateinit var viewModel: UserDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotoFillBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]

        // Inflate the layout for this fragment
        //   inflater.inflate(R.layout.fragment_photo_fill, container, false)
        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance()

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.ivProfile.visibility = View.VISIBLE
                binding.ivProfile.setImageURI(uri)
                this.uri = uri
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
        imageUri = createImageUri()!!
        initListener()
        observeProgress()
        observerErrorMessageApiResponse()
        observerUserFillDetailsApiResponse()
        return binding.root
    }


    private fun initListener() {
        name = requireArguments().getString("name").toString()
        pincode = requireArguments().getString("pincode").toString()
        city = requireArguments().getString("city").toString()
        state = requireArguments().getString("state").toString()
        bio = requireArguments().getString("bio").toString()

        binding.chooseImage.setOnClickListener {
            chooseImage()
        }


        binding.submitButton.setOnClickListener {
            uploadImage()
        }
        binding.tvSkip.setOnClickListener {
            callPutUserDetailsApi(
                auth.currentUser!!.uid, UserDetailsPutRequestBodyModel(
                    name.trim(), "", Address(state.trim(), city, pincode), bio ?: ""
                )
            )
        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun uploadImage() {
        if (uri == null) {
            Toast.makeText(requireContext(), "Please choose an image first", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Wait while uploading profile")
        progressDialog.show()
        val imageRef = storageRef.reference.child("images")
            .child("${System.currentTimeMillis()} ${auth.currentUser!!.uid}")
        imageRef.putFile(uri!!).addOnSuccessListener { task ->
            task.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                photoUrl = uri.toString()
                progressDialog.dismiss()
                callPutUserDetailsApi(
                    auth.currentUser!!.uid, UserDetailsPutRequestBodyModel(
                        name.trim(), photoUrl ?: "", Address(state.trim(), city, pincode), bio ?: ""
                    )
                )
            }?.addOnFailureListener {
                Toast.makeText(
                    requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT
                ).show()
                progressDialog.dismiss()
            }
        }.addOnFailureListener {
            Toast.makeText(
                requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT
            ).show()
            progressDialog.dismiss()
        }
    }


    private fun chooseImage() {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.media_choose_bottom_dialog, null)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        val gallery = view.findViewById<TextView>(R.id.chooseGallery)
        gallery.setOnClickListener {
            dialog.dismiss()
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }
        val camera = view.findViewById<TextView>(R.id.chooseCamera)
        camera.setOnClickListener {
            dialog.dismiss()
            if (checkPermission()) {
                contract.launch(imageUri)
            } else {
                requestForPermission.launch(android.Manifest.permission.CAMERA)
            }

        }
    }

    private fun showRationaleDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Camera Permission")
            .setMessage("This app requires camera permission to take profile photos. If you deny this time you have to manually go to app setting to allow permission.")
            .setPositiveButton("Ok") { _, _ ->
                requestForPermission.launch(android.Manifest.permission.CAMERA)
            }
        builder.create().show()
    }

    private fun checkPermission(): Boolean {
        val permission = android.Manifest.permission.CAMERA
        return ContextCompat.checkSelfPermission(
            requireContext(), permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createImageUri(): Uri? {
        val image = File(requireContext().filesDir, "profile_photos.png")
        return FileProvider.getUriForFile(
            requireContext(), "com.indiastudygroupadmin.fileProvider", image
        )
    }


    private fun callPutUserDetailsApi(
        userId: String?, putUserDetailsPutRequestBodyModel: UserDetailsPutRequestBodyModel
    ) {
        viewModel.callPutUserDetails(userId, putUserDetailsPutRequestBodyModel)
    }


    private fun observeProgress() {
        viewModel.showProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })

    }

    private fun observerErrorMessageApiResponse() {
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })

    }

    private fun observerUserFillDetailsApiResponse() {
        viewModel.userDetailsResponse.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(R.id.action_photoFillFragment_to_mainActivity)
            requireActivity().finish()
        })
    }


}