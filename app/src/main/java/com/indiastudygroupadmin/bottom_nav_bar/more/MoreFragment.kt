package com.indiastudygroupadmin.bottom_nav_bar.more

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.app_utils.ApiCallsConstant
import com.indiastudygroupadmin.app_utils.IntentUtil
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.databinding.FragmentMoreBinding
import com.indiastudygroupadmin.edit_profile.ui.EditProfileActivity
import com.indiastudygroupadmin.registerScreen.SignInActivity
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsResponseModel
import com.indiastudygroupadmin.userDetailsApi.viewModel.UserDetailsViewModel

class MoreFragment : Fragment() {
    private val EDIT_PROFILE_REQUEST_CODE = 100

    private lateinit var binding: FragmentMoreBinding

    private lateinit var viewModel: MoreViewModel
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var userData: UserDetailsResponseModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MoreViewModel::class.java]
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
//        inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        initListener()

        if (!ApiCallsConstant.apiCallsOnceMore) {
            userDetailsViewModel.callGetUserDetails(auth.currentUser!!.uid)
            ApiCallsConstant.apiCallsOnceMore = true
        }

        observeProgress()
        observerErrorMessageApiResponse()
        observerUserDetailsApiResponse()
        return binding.root
    }

    private fun initListener() {
        binding.tvSignOut.setOnClickListener {
            signOutDialog()
        }
        binding.tvEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            intent.putExtra("name", userData.name)
            intent.putExtra("address", userData.address?.street)
            intent.putExtra("pincode", userData.address?.pincode)
            intent.putExtra("state", userData.address?.state)
            intent.putExtra("district", userData.address?.district)
            intent.putExtra("profile", userData.photo)
            startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE)
        }
    }

    private fun signOutDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setCancelable(false)
        alertDialog.setTitle("Sign Out")
        alertDialog.setMessage("Are you sure you want to Sign Out")
        alertDialog.setPositiveButton("yes") { _, _ ->
            auth.signOut()
            ToastUtil.makeToast(requireContext(), "Successful Sign Out")
            IntentUtil.startIntent(requireContext(), SignInActivity())
            requireActivity().finish()
        }
        alertDialog.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.create().show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            // Refresh the user details here
            userDetailsViewModel.callGetUserDetails(auth.currentUser!!.uid)
        }
    }

    private fun observerUserDetailsApiResponse() {
        userDetailsViewModel.userDetailsResponse.observe(viewLifecycleOwner, Observer {

            userData = it
            binding.tvName.text = it.name
            binding.tvPhoneNumber.text = it.contact
            Glide.with(requireContext()).load(it.photo).placeholder(R.drawable.profile)
                .error(R.drawable.profile).into(binding.ivProfile)
        })
    }

    private fun observeProgress() {
        userDetailsViewModel.showProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.mainView.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.mainView.visibility = View.VISIBLE
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        userDetailsViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })
    }

}
