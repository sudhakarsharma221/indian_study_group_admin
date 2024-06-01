package com.indiastudygroupadmin.bottom_nav_bar.attendance.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.indiastudygroupadmin.app_utils.ApiCallsConstant
import com.indiastudygroupadmin.app_utils.AppConstant
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.bottom_nav_bar.library.model.LibraryResponseItem
import com.indiastudygroupadmin.bottom_nav_bar.library.viewModel.LibraryViewModel
import com.indiastudygroupadmin.bottom_nav_bar.attendance.ui.adapter.AttendanceLibraryAdapter
import com.indiastudygroupadmin.databinding.FragmentScheduleBinding
import com.indiastudygroupadmin.userDetailsApi.model.UserDetailsResponseModel
import com.indiastudygroupadmin.userDetailsApi.viewModel.UserDetailsViewModel
import java.util.LinkedList
import java.util.Queue


class ScheduleFragment : Fragment() {
    private lateinit var binding: FragmentScheduleBinding

    //    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var userData: UserDetailsResponseModel

    //    private lateinit var libraryDetailsViewModel: LibraryViewModel
    private lateinit var adapter: AttendanceLibraryAdapter
//    private lateinit var libraryList: ArrayList<LibraryResponseItem>
//    private val libraryIdQueue: Queue<String> = LinkedList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleBinding.inflate(layoutInflater)
//        libraryDetailsViewModel = ViewModelProvider(this)[LibraryViewModel::class.java]
//        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
//        libraryList = arrayListOf()
        auth = FirebaseAuth.getInstance()
        requireActivity().window.statusBarColor = Color.WHITE
        // Inflate the layout for this fragment
        //inflater.inflate(R.layout.fragment_schedule, container, false)
//        callGetUserDetails()
//
//        observerIdLibraryApiResponse()
//        observeProgress()
//        observerErrorMessageApiResponse()
        initListener()
        return binding.root
    }

    private fun initListener() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        if (AppConstant.libraryList.isEmpty()) {
            binding.noSchedule.visibility = View.VISIBLE
            binding.swiperefresh.visibility = View.GONE
        } else {
            binding.noSchedule.visibility = View.GONE
            binding.swiperefresh.visibility = View.VISIBLE
            adapter = AttendanceLibraryAdapter(requireContext(), AppConstant.libraryList)
            binding.recyclerView.adapter = adapter
        }

//
//        if (!ApiCallsConstant.apiCallsOnceAttendance) {
//            userData.libraries.forEach { libraryIdQueue.add(it) }
//            processNextLibraryId()
//            ApiCallsConstant.apiCallsOnceAttendance = true
//        }
//
        binding.swiperefresh.setOnRefreshListener {
            binding.swiperefresh.isRefreshing = false

            //            libraryList.clear()
//            userData.libraries.forEach { libraryIdQueue.add(it) }
//            processNextLibraryId()
        }
    }

//    private fun processNextLibraryId() {
//        if (libraryIdQueue.isNotEmpty()) {
//            val nextLibraryId = libraryIdQueue.poll()
//            callIdLibraryDetailsApi(nextLibraryId)
//        } else {
//            adapter = AttendanceLibraryAdapter(requireContext(), libraryList)
//            binding.recyclerView.adapter = adapter
//            binding.swiperefresh.isRefreshing = false
//        }
//    }

//    private fun callIdLibraryDetailsApi(id: String?) {
//        libraryDetailsViewModel.callIdLibrary(id)
//    }
//
//    private fun callGetUserDetails() {
//        userData = userDetailsViewModel.getUserDetailsResponse()!!
//        if (userData.libraries.isEmpty()) {
//            binding.noSchedule.visibility = View.VISIBLE
//            binding.swiperefresh.visibility = View.GONE
//        } else {
//            binding.noSchedule.visibility = View.GONE
//            binding.swiperefresh.visibility = View.VISIBLE
//            initListener()
//        }
//    }
//
//    private fun observeProgress() {
//        libraryDetailsViewModel.showProgress.observe(viewLifecycleOwner, Observer {
//            if (it) {
//                binding.progressBar.visibility = View.VISIBLE
//                binding.swiperefresh.visibility = View.GONE
//            } else {
//                binding.progressBar.visibility = View.GONE
//                binding.swiperefresh.visibility = View.VISIBLE
//            }
//        })
//    }
//
//    private fun observerErrorMessageApiResponse() {
//        libraryDetailsViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
//            ToastUtil.makeToast(requireContext(), it)
//            processNextLibraryId()  // Continue processing even if there's an error
//        })
//    }
//
//    private fun observerIdLibraryApiResponse() {
//        libraryDetailsViewModel.idLibraryResponse.observe(viewLifecycleOwner, Observer {
//            it.libData?.let { libraryItem ->
//                libraryList.add(libraryItem)
//            }
//            Log.d("LIBRARYSIZE", "${libraryList.size}   ${userData.libraries.size}")
//            processNextLibraryId()
//        })
//    }
}
