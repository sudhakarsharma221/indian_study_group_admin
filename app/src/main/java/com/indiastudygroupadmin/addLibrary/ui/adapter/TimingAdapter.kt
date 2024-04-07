package com.indiastudygroupadmin.addLibrary.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.indiastudygroupadmin.addLibrary.model.AddTimingsDataClass
import com.indiastudygroupadmin.databinding.TimingItemLayoutBinding

class TimingAdapter(val context: Context, private val list: ArrayList<AddTimingsDataClass>) :
    Adapter<TimingAdapter.TimingViewHolder>() {

    inner class TimingViewHolder(val binding: TimingItemLayoutBinding) : ViewHolder(binding.root) {
        fun bindView(item: AddTimingsDataClass, context: Context, position: Int) {
            binding.tvFrom.text = item.from
            binding.tvTo.text = item.to
            val days = item.days?.joinToString(separator = ", ")
            binding.tvDays.text = days
            binding.delete.setOnClickListener {
                // Remove the item from the list
                list.removeAt(position)
                // Notify the adapter that item is removed
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimingViewHolder {
        val binding =
            TimingItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimingViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TimingViewHolder, position: Int) {
        list[position].let {
            holder.bindView(it, context, position)
        }
    }

    fun addItem(item: AddTimingsDataClass) {
        list.add(item)
        notifyDataSetChanged()
    }
}