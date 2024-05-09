package com.indiastudygroupadmin.bottom_nav_bar.schedule.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.databinding.FragmentScheduleBinding


class ScheduleFragment : Fragment() {
    private lateinit var binding: FragmentScheduleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        //inflater.inflate(R.layout.fragment_schedule, container, false)
        return binding.root
    }
}