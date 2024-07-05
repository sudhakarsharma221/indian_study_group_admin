package com.indiastudygroupadminapp.bottom_nav_bar.more.student_data.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.indiastudygroupadminapp.databinding.StudentDataItemLayoutBinding

class StudentDataAdapter(val context: Context, private val list: ArrayList<String>) :
    Adapter<StudentDataAdapter.MyViewHolder>() {
    inner class MyViewHolder(val binding: StudentDataItemLayoutBinding) : ViewHolder(binding.root) {

        fun bindView(item: String, context: Context, position: Int) {

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            StudentDataItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        list[position].let {
            holder.bindView(it, context, position)
        }
    }
}