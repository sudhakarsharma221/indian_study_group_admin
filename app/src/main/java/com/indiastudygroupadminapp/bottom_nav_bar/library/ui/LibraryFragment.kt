package com.indiastudygroupadminapp.bottom_nav_bar.library.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.indiastudygroupadminapp.bottom_nav_bar.gym.viewModel.GymViewModel
import com.indiastudygroupadminapp.bottom_nav_bar.library.model.LibraryResponseItem
import com.indiastudygroupadminapp.app_utils.ApiCallsConstant
import com.indiastudygroupadminapp.app_utils.AppConstant
import com.indiastudygroupadminapp.app_utils.IntentUtil
import com.indiastudygroupadminapp.bottom_nav_bar.library.ui.adapter.LibraryAdapter
import com.indiastudygroupadminapp.app_utils.ToastUtil
import com.indiastudygroupadminapp.bottom_nav_bar.gym.model.GymResponseItem
import com.indiastudygroupadminapp.bottom_nav_bar.gym.ui.adapter.GymAdapter
import com.indiastudygroupadminapp.bottom_nav_bar.library.viewModel.LibraryViewModel
import com.indiastudygroupadminapp.databinding.FragmentHomeBinding
import com.indiastudygroupadminapp.message.ui.MessageActivity
import com.indiastudygroupadminapp.notification.ui.NotificationActivity
import com.indiastudygroupadminapp.userDetailsApi.model.UserDetailsResponseModel
import com.indiastudygroupadminapp.userDetailsApi.viewModel.UserDetailsViewModel
import java.util.*

class LibraryFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var libraryDetailsViewModel: LibraryViewModel
    private lateinit var gymDetailsViewModel: GymViewModel
    private var libraryList: ArrayList<LibraryResponseItem> = arrayListOf()
    private var gymList: ArrayList<GymResponseItem> = arrayListOf()
    private val libraryIdQueue: Queue<String> = LinkedList()
    private val gymIdQueue: Queue<String> = LinkedList()
    private lateinit var auth: FirebaseAuth
    private lateinit var userData: UserDetailsResponseModel
    private lateinit var libraryAdapter: LibraryAdapter
    private lateinit var gymAdapter: GymAdapter


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
        gymDetailsViewModel = ViewModelProvider(this)[GymViewModel::class.java]
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
            ApiCallsConstant.apiCallsOnceGym = false
        }

        observerIdLibraryApiResponse()
        observerIdGymApiResponse()
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

        if (userData.authType == "library owner") {
            if (!ApiCallsConstant.apiCallsOnceLibrary) {
                userData.libraries.forEach { libraryIdQueue.add(it) }
                processNextLibraryId()
                ApiCallsConstant.apiCallsOnceLibrary = true
            }
        } else if (userData.authType == "gym owner") {
            if (!ApiCallsConstant.apiCallsOnceGym) {
                userData.gyms.forEach { gymIdQueue.add(it) }
                processNextGymId()
                ApiCallsConstant.apiCallsOnceGym = true
            }
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

                if (userData.authType == "library owner") {
                    libraryAdapter.filter(query)
                    if (libraryAdapter.itemCount == 0) {
                        binding.noLibAvailable.visibility = View.VISIBLE
                    } else {
                        binding.noLibAvailable.visibility = View.GONE
                    }
                } else if (userData.authType == "gym owner") {
                    gymAdapter.filter(query)
                    if (gymAdapter.itemCount == 0) {
                        binding.noLibAvailable.visibility = View.VISIBLE
                    } else {
                        binding.noLibAvailable.visibility = View.GONE
                    }
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
            libraryAdapter = LibraryAdapter(requireContext(), libraryList)
            binding.recyclerView.adapter = libraryAdapter
            libraryAdapter.notifyDataSetChanged()
            binding.swiperefresh.isRefreshing = false
        }
    }

    private fun processNextGymId() {
        if (gymIdQueue.isNotEmpty()) {
            val nextGymId = gymIdQueue.poll()
            callIdGymDetailsApi(nextGymId)
        } else {
            AppConstant.gymList = gymList
            gymAdapter = GymAdapter(requireContext(), gymList)
            binding.recyclerView.adapter = gymAdapter
            gymAdapter.notifyDataSetChanged()
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

    private fun callIdGymDetailsApi(id: String?) {
        gymDetailsViewModel.callIdGym(id)
    }

    private fun observerUserDetailsApiResponse() {
        userDetailsViewModel.userDetailsResponse.observe(viewLifecycleOwner, Observer {
            userData = it

            userData.notifications.forEach { noti ->
                if (noti.status == "unread") {
                    binding.newNotification.visibility = View.VISIBLE
                }
            }
            if (userData.authType == "library owner") {
                if (userData.libraries.isEmpty()) {
                    binding.noLibAvailable.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.noLibAvailable.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                }
            } else if (userData.authType == "gym owner") {
                if (userData.gyms.isEmpty()) {
                    binding.noLibAvailable.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.noLibAvailable.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                }
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
        gymDetailsViewModel.showProgress.observe(viewLifecycleOwner, Observer {
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
        gymDetailsViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
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

    private fun observerIdGymApiResponse() {
        gymDetailsViewModel.idGymResponse.observe(viewLifecycleOwner, Observer {

            if (gymList.size != userData.gyms.size) {
                it.gymData?.let { gymItem -> gymList.add(gymItem) }
            }
            processNextGymId()


        })
    }


    private fun showFilterDialog() {
        // Implement the filter dialog display logic here if needed
    }
}