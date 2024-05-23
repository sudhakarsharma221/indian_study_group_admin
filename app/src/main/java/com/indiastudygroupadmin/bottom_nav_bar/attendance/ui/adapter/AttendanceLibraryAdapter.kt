package com.indiastudygroupadmin.bottom_nav_bar.attendance.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.bottom_nav_bar.library.model.LibraryResponseItem
import com.indiastudygroupadmin.bottom_nav_bar.attendance.ui.CurrentAttendanceActivity
import com.indiastudygroupadmin.databinding.AttendanceLibraryChooseItemLayoutBinding

class AttendanceLibraryAdapter
    (
    val context: Context, private val list: ArrayList<LibraryResponseItem>
) : RecyclerView.Adapter<AttendanceLibraryAdapter.MyViewHolder>() {
    inner class MyViewHolder(val binding: AttendanceLibraryChooseItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(library: LibraryResponseItem, context: Context, position: Int) {

            binding.tvName.text = library.name
            binding.tvPrice.text = HtmlCompat.fromHtml(
                "<b><font color='#5669FF'>₹${library.pricing?.daily}</font></b> /Day<br/>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            binding.tvAddress.text =
                "${library.address?.street}, ${library.address?.district}, ${library.address?.state}, ${library.address?.pincode}"

            if (library.photo?.isNotEmpty() == true) {
                Glide.with(context).load(library.photo?.get(0)).placeholder(R.drawable.noimage)
                    .error(R.drawable.noimage).into(binding.libImage)
            }
            binding.attendanceLayout.setOnClickListener {
                val intent = Intent(context, CurrentAttendanceActivity::class.java)
                intent.putExtra("LibraryData", library)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AttendanceLibraryChooseItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
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