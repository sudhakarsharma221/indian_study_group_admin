package com.indiastudygroupadmin.bottom_nav_bar.more

import android.app.Dialog
import android.graphics.Color
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.app_utils.ApiCallsConstant
import com.indiastudygroupadmin.app_utils.AppConstant
import com.indiastudygroupadmin.app_utils.IntentUtil
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.bottom_nav_bar.more.help_desk.HelpDeskActivity
import com.indiastudygroupadmin.bottom_nav_bar.more.setting.SettingActivity
import com.indiastudygroupadmin.bottom_nav_bar.more.setting.policy.viewModel.PolicyViewModel
import com.indiastudygroupadmin.bottom_nav_bar.more.student_data.StudentDataActivity
import com.indiastudygroupadmin.bottom_nav_bar.more.wallet.WalletActivity
import com.indiastudygroupadmin.databinding.FragmentMoreBinding
import com.indiastudygroupadmin.editProfile.EditProfileActivity
import com.indiastudygroupadmin.registerScreen.SignInActivity
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsResponseModel
import com.indiastudygroupadmin.userDetailsApi.viewModel.UserDetailsViewModel

class MoreFragment : Fragment() {
    private val EDIT_PROFILE_REQUEST_CODE = 100
    private lateinit var viewModel: PolicyViewModel


    private lateinit var binding: FragmentMoreBinding

    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var userData: UserDetailsResponseModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(this)[PolicyViewModel::class.java]

        requireActivity().window.statusBarColor = Color.WHITE

        initListener()
        if (!ApiCallsConstant.apiCallsOnceMore) {
            callPolicyDetails()
            ApiCallsConstant.apiCallsOnceMore = true
        }

//        inflater.inflate(R.layout.fragment_profile, container, false)
        observeProgress()
        observerPolicyApiResponse()
        observerErrorMessageApiResponse()

        return binding.root
    }

    private fun initListener() {


        binding.studentData.setOnClickListener {
//            ToastUtil.makeToast(requireContext(), "Coming Soon...")

            IntentUtil.startIntent(requireContext(), StudentDataActivity())
        }
        binding.tvWallet.setOnClickListener {
            ToastUtil.makeToast(requireContext(), "Coming Soon...")

            //IntentUtil.startIntent(requireContext(), WalletActivity())
        }
        binding.tvSetting.setOnClickListener {
            IntentUtil.startIntent(requireContext(), SettingActivity())
        }
        binding.tvHelpDesk.setOnClickListener {
            IntentUtil.startIntent(requireContext(), HelpDeskActivity())
        }

        binding.tvSignOut.setOnClickListener {
            signOutDialog()
        }
    }

    private fun signOutDialog() {
        val builder = Dialog(requireContext())
        val view = layoutInflater.inflate(R.layout.logout_dialog, null)
        builder.setContentView(view)
        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)
        builder.show()
        builder.setCancelable(true)
        val logout = view.findViewById<MaterialCardView>(R.id.logout)
        val cancel = view.findViewById<TextView>(R.id.cancelButton)
        cancel.setOnClickListener {
            builder.dismiss()
        }
        logout.setOnClickListener {
            auth.signOut()
            ToastUtil.makeToast(requireContext(), "Successful Sign Out")
            IntentUtil.startIntent(requireContext(), SignInActivity())
            ApiCallsConstant.apiCallsOnceHome = false
            ApiCallsConstant.apiCallsOnceProfile = false
            ApiCallsConstant.apiCallsOnceMore = false
            ApiCallsConstant.apiCallsOnceLibrary = false
            ApiCallsConstant.apiCallsOnceGym = false
            AppConstant.libraryList.clear()
            AppConstant.gymList.clear()
            requireActivity().finish()
        }
    }

    private fun callPolicyDetails(
    ) {
        viewModel.callPolicyDetails()
    }

    private fun observerPolicyApiResponse() {
        viewModel.policyDetailsResponse.observe(viewLifecycleOwner, Observer {
            viewModel.setPolicyDetailsResponse(it)
        })
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


}