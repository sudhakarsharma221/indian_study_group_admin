package com.indiastudygroupadmin.bottom_nav_bar.home.tab_layout.screens.library

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.indiastudygroupadmin.addLibrary.ui.AddLibraryActivity
import com.indiastudygroupadmin.app_utils.IntentUtil
import com.indiastudygroupadmin.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {

    private lateinit var viewModel: LibraryViewModel
    private lateinit var binding: FragmentLibraryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLibraryBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[LibraryViewModel::class.java]
        // inflater.inflate(R.layout.fragment_posts, container, false)

        initListener()
        return binding.root
    }

    private fun initListener() {
        binding.addLibrary.setOnClickListener {
            IntentUtil.startIntent(requireContext(), AddLibraryActivity())
        }
    }


}