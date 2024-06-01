package com.indiastudygroupadmin.addLibrary.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.indiastudygroupadmin.databinding.ImageItemLayoutBinding

class ImageAdapter(val context: Context, private val list: ArrayList<Uri>) :
    Adapter<ImageAdapter.TimingViewHolder>() {

    inner class TimingViewHolder(val binding: ImageItemLayoutBinding) : ViewHolder(binding.root) {
        fun bindView(imageUri: Uri, context: Context, position: Int) {
            Glide.with(itemView.context).load(imageUri).into(binding.image)
            binding.delete.setOnClickListener {
                list.removeAt(position)
                notifyItemRemoved(position)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimingViewHolder {
        val binding =
            ImageItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    fun addItem(item: Uri) {
        list.add(item)
        notifyDataSetChanged()
    }
}