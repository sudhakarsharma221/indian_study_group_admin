package com.indiastudygroupadmin.bottom_nav_bar.library.ui.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.indiastudygroupadmin.bottom_nav_bar.library.model.LibraryResponseItem
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.add_regular_student.AddRegularStudentActivity
import com.indiastudygroupadmin.app_utils.IntentUtil
import com.indiastudygroupadmin.bottom_nav_bar.library.ui.LibraryDetailsActivity
import com.indiastudygroupadmin.databinding.LibraryShowItemLayoutBinding
import com.indiastudygroupadmin.qr.QrCodeShowActivity

class LibraryAdapter(
    val context: Context, private val list: ArrayList<LibraryResponseItem>
) : Adapter<LibraryAdapter.MyViewHolder>() {
    inner class MyViewHolder(val binding: LibraryShowItemLayoutBinding) : ViewHolder(binding.root) {
        fun bindView(library: LibraryResponseItem, context: Context, position: Int) {

//            binding.favourite.setOnClickListener {
//                binding.favourite.setCardBackgroundColor(Color.RED)
//            }
            binding.tvName.text = library.name
            binding.tvSeats.text = "${library.vacantSeats} / ${library.seats} seats vacant"

            binding.tvPrice.text = HtmlCompat.fromHtml(
                "<b><font color='#5669FF'>₹${library.pricing?.daily}</font></b> /Day<br/>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            binding.add.setOnClickListener {
                IntentUtil.startIntent(context, AddRegularStudentActivity())
            }
            binding.moreButton.setOnClickListener { view ->
                showPopupMenu(view, position, library.id, library.name, library.id)
            }
            val timing = library.timing

            when (timing.size) {
                3 -> {
                    binding.timeMorning.text = "Morning : ${timing[0].from} to ${timing[0].to}"
                    binding.timeAfternoon.text = "Afternoon : ${timing[1].from} to ${timing[1].to}"
                    binding.timeEvening.text = "Evening : ${timing[2].from} to ${timing[2].to}"
                }

                2 -> {
                    binding.timeMorning.text = "Morning ${timing[0].from} to ${timing[0].to}"
                    binding.timeAfternoon.text = "Afternoon ${timing[1].from} to ${timing[1].to}"
                    binding.timeEvening.visibility = View.GONE
                }

                1 -> {
                    binding.timeMorning.text = "Morning ${timing[0].from} to ${timing[0].to}"
                    binding.timeEvening.visibility = View.GONE
                    binding.timeAfternoon.visibility = View.GONE
                }
            }

            binding.tvAddress.text = library.address?.street
            Glide.with(context).load(library.photo).placeholder(R.drawable.noimage)
                .error(R.drawable.noimage).into(binding.imageView)
            binding.layoutView.setOnClickListener {
                val intent = Intent(context, LibraryDetailsActivity::class.java)
                intent.putExtra("LibraryId", library.id)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            LibraryShowItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    private fun showPopupMenu(
        view: View, position: Int, libId: String?, name: String?, id: String?
    ) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.library_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_qr -> {
                    val intent = Intent(context, QrCodeShowActivity::class.java)
                    intent.putExtra("name", name ?: "")
                    intent.putExtra("libId", libId ?: "")

                    context.startActivity(intent)
                    true
                }

                R.id.action_info -> {
                    val intent = Intent(context, LibraryDetailsActivity::class.java)
                    intent.putExtra("LibraryId", id)
                    context.startActivity(intent)
                    true
                }

//                R.id.action_pause_slot -> {
//                    // Implement your delete action here
//                    true
//                }
//
//                R.id.action_pause_id -> {
//                    // Implement your delete action here
//                    true
//                }

                else -> false
            }
        }
        popupMenu.setOnDismissListener {
            view.setBackgroundColor(Color.WHITE)
        }
        popupMenu.show()
    }
}