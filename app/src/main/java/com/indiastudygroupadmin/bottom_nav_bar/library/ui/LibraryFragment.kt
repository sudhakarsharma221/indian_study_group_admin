package com.indiastudygroupadmin.bottom_nav_bar.library.ui

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.indiastudygroupadmin.bottom_nav_bar.library.model.LibraryResponseItem
import com.indiastudygroupadmin.app_utils.ApiCallsConstant
import com.indiastudygroupadmin.app_utils.IntentUtil
import com.indiastudygroupadmin.bottom_nav_bar.library.ui.adapter.LibraryAdapter
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.bottom_nav_bar.library.viewModel.LibraryViewModel
import com.indiastudygroupadmin.databinding.FragmentHomeBinding
import com.indiastudygroupadmin.message.ui.MessageActivity
import com.indiastudygroupadmin.notification.ui.NotificationActivity
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsResponseModel
import com.indiastudygroupadmin.userDetailsApi.viewModel.UserDetailsViewModel
import java.util.*

class LibraryFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var userData: UserDetailsResponseModel
    private lateinit var libraryDetailsViewModel: LibraryViewModel
    private lateinit var adapter: LibraryAdapter
    private lateinit var libraryList: ArrayList<LibraryResponseItem>
    private val libraryIdQueue: Queue<String> = LinkedList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        libraryDetailsViewModel = ViewModelProvider(this)[LibraryViewModel::class.java]
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        libraryList = arrayListOf()
        auth = FirebaseAuth.getInstance()
        requireActivity().window.statusBarColor = Color.WHITE

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
        binding.filterButton.setOnClickListener {
            showFilterDialog()
        }

        binding.message.setOnClickListener {
            IntentUtil.startIntent(requireContext(), MessageActivity())
        }
        binding.notification.setOnClickListener {
            IntentUtil.startIntent(requireContext(), NotificationActivity())
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

    private fun processNextLibraryId() {
        if (libraryIdQueue.isNotEmpty()) {
            val nextLibraryId = libraryIdQueue.poll()
            callIdLibraryDetailsApi(nextLibraryId)
        } else {
            adapter = LibraryAdapter(requireContext(), libraryList)
            binding.recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            binding.swiperefresh.isRefreshing = false
        }
    }

    private fun callIdLibraryDetailsApi(id: String?) {
        libraryDetailsViewModel.callIdLibrary(id)
    }

    private fun observerUserDetailsApiResponse() {
        userDetailsViewModel.userDetailsResponse.observe(viewLifecycleOwner, Observer {
            userData = it
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
            it.libData?.let { libraryItem -> libraryList.add(libraryItem) }
            processNextLibraryId()
        })
    }

    private fun showFilterDialog() {
        // Implement the filter dialog display logic here if needed
    }
}
