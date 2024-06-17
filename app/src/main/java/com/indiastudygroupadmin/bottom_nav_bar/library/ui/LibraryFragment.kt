package com.indiastudygroupadmin.bottom_nav_bar.library.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.indiastudygroupadmin.bottom_nav_bar.library.model.LibraryResponseItem
import com.indiastudygroupadmin.app_utils.ApiCallsConstant
import com.indiastudygroupadmin.app_utils.AppConstant
import com.indiastudygroupadmin.app_utils.IntentUtil
import com.indiastudygroupadmin.bottom_nav_bar.library.ui.adapter.LibraryAdapter
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.bottom_nav_bar.library.viewModel.LibraryViewModel
import com.indiastudygroupadmin.databinding.FragmentHomeBinding
import com.indiastudygroupadmin.message.ui.MessageActivity
import com.indiastudygroupadmin.notification.helper.NotificationHelper
import com.indiastudygroupadmin.notification.ui.NotificationActivity
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsResponseModel
import com.indiastudygroupadmin.userDetailsApi.viewModel.UserDetailsViewModel
import java.util.*

class LibraryFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var libraryDetailsViewModel: LibraryViewModel
    private var libraryList: ArrayList<LibraryResponseItem> = arrayListOf()
    private val libraryIdQueue: Queue<String> = LinkedList()
    private lateinit var auth: FirebaseAuth
    private lateinit var userData: UserDetailsResponseModel
    private lateinit var adapter: LibraryAdapter


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestForPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                ToastUtil.makeToast(requireContext(), "Notification Permission Granted")
            } else {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                    showRationaleDialog()
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        libraryDetailsViewModel = ViewModelProvider(this)[LibraryViewModel::class.java]
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        requireActivity().window.statusBarColor = Color.WHITE

        if (!checkPermission()) {
            requestForPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)

        }
        if (!ApiCallsConstant.apiCallsOnceHome) {
            userDetailsViewModel.callGetUserDetails(auth.currentUser!!.uid)
            ApiCallsConstant.apiCallsOnceHome = true
            ApiCallsConstant.apiCallsOnceLibrary = false
        }

        observerIdLibraryApiResponse()
        observeProgress()
        observerErrorMessageApiResponse()
        observerUserDetailsApiResponse()
        return binding.root
    }


    private fun initListener() {
        searchViewFunction()

        binding.filterButton.setOnClickListener {
            showFilterDialog()
        }

        binding.message.setOnClickListener {
            IntentUtil.startIntent(requireContext(), MessageActivity())
        }
        binding.notification.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivityForResult(
                intent, 2
            )
        }

        if (!ApiCallsConstant.apiCallsOnceLibrary) {
            userData.libraries.forEach { libraryIdQueue.add(it) }
            processNextLibraryId()
            ApiCallsConstant.apiCallsOnceLibrary = true
        }

        binding.swiperefresh.setOnRefreshListener {
            libraryList.clear()
            userData.libraries.forEach { libraryIdQueue.add(it) }
            processNextLibraryId()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun searchViewFunction() {
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                adapter.filter(query)
                if (adapter.itemCount == 0) {
                    binding.noLibAvailable.visibility = View.VISIBLE
                } else {
                    binding.noLibAvailable.visibility = View.GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        binding.searchEt.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                binding.searchEt.clearFocus()
                true
            } else {
                false
            }
        }

//        // OnFocusChangeListener to handle focus changes
//        binding.searchEt.setOnFocusChangeListener { v, hasFocus ->
//            if (!hasFocus) {
//                binding.topPicksLayout.visibility = View.GONE
//                binding.allLibRecyclerView.visibility = View.GONE
//                binding.pincodeRecyclerView.visibility = View.VISIBLE
//                binding.backButton.visibility = View.GONE
//            } else {
//                binding.backButton.visibility = View.VISIBLE
//                binding.topPicksLayout.visibility = View.VISIBLE
//                binding.pincodeRecyclerView.visibility = View.GONE
//            }
//        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showRationaleDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Notification Permission")
            .setMessage("This app requires notification permission to keep you updated. If you deny this time you have to manually go to app setting to allow permission.")
            .setPositiveButton("Ok") { _, _ ->
                requestForPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        builder.create().show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermission(): Boolean {
        val permission = android.Manifest.permission.POST_NOTIFICATIONS
        return ContextCompat.checkSelfPermission(
            requireContext(), permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun processNextLibraryId() {
        if (libraryIdQueue.isNotEmpty()) {
            val nextLibraryId = libraryIdQueue.poll()
            callIdLibraryDetailsApi(nextLibraryId)
        } else {
            AppConstant.libraryList = libraryList
            adapter = LibraryAdapter(requireContext(), libraryList)
            binding.recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            binding.swiperefresh.isRefreshing = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == AppCompatActivity.RESULT_OK) {
            binding.newNotification.visibility = View.GONE
        }
    }

    private fun callIdLibraryDetailsApi(id: String?) {
        libraryDetailsViewModel.callIdLibrary(id)
    }

    private fun observerUserDetailsApiResponse() {
        userDetailsViewModel.userDetailsResponse.observe(viewLifecycleOwner, Observer {
            userData = it

            userData.notifications.forEach { noti ->
                if (noti.status == "unread") {
                    binding.newNotification.visibility = View.VISIBLE
                }
            }

            if (userData.libraries.isEmpty()) {
                binding.noLibAvailable.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.noLibAvailable.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
            binding.currentLocation.text = "${it.address?.district}, ${it.address?.state}"
            initListener()
            userDetailsViewModel.setUserDetailsResponse(it)
            binding.swiperefresh.isRefreshing = false
        })
    }

    private fun observeProgress() {
        libraryDetailsViewModel.showProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.noLibAvailable.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.shimmerLayout.startShimmer()
            } else {
                binding.shimmerLayout.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.shimmerLayout.stopShimmer()
            }
        })
        userDetailsViewModel.showProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.noLibAvailable.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.shimmerLayout.startShimmer()
            } else {
                binding.shimmerLayout.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.shimmerLayout.stopShimmer()
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        userDetailsViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })
        libraryDetailsViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })
    }

    private fun observerIdLibraryApiResponse() {
        libraryDetailsViewModel.idLibraryResponse.observe(viewLifecycleOwner, Observer {

            if (libraryList.size != userData.libraries.size) {
                it.libData?.let { libraryItem -> libraryList.add(libraryItem) }
            }
            processNextLibraryId()


        })
    }

    private fun showFilterDialog() {
        // Implement the filter dialog display logic here if needed
    }
}