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
import androidx.recyclerview.widget.LinearLayoutManager
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

            binding.rvDays.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding.tvName.text = library.name
//            binding.tvSeats.text = "${library.vacantSeats} / ${library.seats} seats vacant"

            binding.tvPrice.text = HtmlCompat.fromHtml(
                "<b><font color='#5669FF'>₹${library.pricing?.daily}</font></b> /Day<br/>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            binding.add.setOnClickListener {
                val intent = Intent(context, AddRegularStudentActivity::class.java)
                intent.putExtra("libraryData", library)
                context.startActivity(intent)
            }
            binding.moreButton.setOnClickListener { view ->
                showPopupMenu(view, position, library.id, library.name, library.id)
            }
            val listOfWeekDays = arrayListOf("mon", "tue", "wed", "thu", "fri", "sat", "sun")

            binding.rvDays.adapter = DaysAdapter(context, library.timing[0].days, listOfWeekDays)

            val seats = library.vacantSeats!!

            when (seats.size) {
                3 -> {
                    binding.tvSeats33.text = "${seats[2]} / ${library.seats}"
                    binding.tvSeats22.text = "${seats[1]} / ${library.seats}"
                    binding.tvSeats11.text = "${seats[0]} / ${library.seats}"


                    val timeStartFormatted2 = formatTime(library.timing[2].from?.toInt(), 0)
                    val timeEndFormatted2 = formatTime(library.timing[2].to?.toInt(), 0)

                    binding.tvTime2.text = "$timeStartFormatted2 to $timeEndFormatted2"


                    val timeStartFormatted1 = formatTime(library.timing[1].from?.toInt(), 0)
                    val timeEndFormatted1 = formatTime(library.timing[1].to?.toInt(), 0)

                    binding.tvTime2.text = "$timeStartFormatted1 to $timeEndFormatted1"


                    val timeStartFormatted = formatTime(library.timing[0].from?.toInt(), 0)
                    val timeEndFormatted = formatTime(library.timing[0].to?.toInt(), 0)

                    binding.tvTime1.text = "$timeStartFormatted to $timeEndFormatted"

                }

                2 -> {
                    binding.tvSeats22.text = "${seats[1]} / ${library.seats}"
                    binding.tvSeats11.text = "${seats[0]} / ${library.seats}"
                    val timeStartFormatted1 = formatTime(library.timing[1].from?.toInt(), 0)
                    val timeEndFormatted1 = formatTime(library.timing[1].to?.toInt(), 0)

                    binding.tvTime2.text = "$timeStartFormatted1 to $timeEndFormatted1"


                    val timeStartFormatted = formatTime(library.timing[0].from?.toInt(), 0)
                    val timeEndFormatted = formatTime(library.timing[0].to?.toInt(), 0)

                    binding.tvTime1.text = "$timeStartFormatted to $timeEndFormatted"

                    binding.tvSeats33.visibility = View.GONE
                    binding.tvSeats3.visibility = View.GONE
                    binding.tvTime3.visibility = View.GONE
                }

                1 -> {
                    binding.tvSeats11.text = "${seats[0]} / ${library.seats}"

                    val timeStartFormatted = formatTime(library.timing[0].from?.toInt(), 0)
                    val timeEndFormatted = formatTime(library.timing[0].to?.toInt(), 0)


                    binding.tvTime1.text = "$timeStartFormatted to $timeEndFormatted"

                    binding.tvSeats22.visibility = View.GONE
                    binding.tvSeats2.visibility = View.GONE
                    binding.tvTime2.visibility = View.GONE
                    binding.tvSeats3.visibility = View.GONE
                    binding.tvSeats33.visibility = View.GONE
                    binding.tvTime3.visibility = View.GONE

                }
            }


//            val timing = library.timing
//
//            when (timing.size) {
//                3 -> {
//                    val timeStartFormatted = formatTime(timing[0].from?.toInt(), 0)
//                    val timeEndFormatted = formatTime(timing[0].to?.toInt(), 0)
//                    binding.timeMorning.text = "Morning : $timeStartFormatted to $timeEndFormatted"
//
//                    val timeStartFormatted1 = formatTime(timing[1].from?.toInt(), 0)
//                    val timeEndFormatted1 = formatTime(timing[1].to?.toInt(), 0)
//
//                    binding.timeAfternoon.text =
//                        "Afternoon : $timeStartFormatted1 to $timeEndFormatted1"
//
//                    val timeStartFormatted2 = formatTime(timing[2].from?.toInt(), 0)
//                    val timeEndFormatted2 = formatTime(timing[2].to?.toInt(), 0)
//
//
//
//                    binding.timeEvening.text =
//                        "Evening : $timeStartFormatted2 to $timeEndFormatted2"
//                }
//
//                2 -> {
//
//                    val timeStartFormatted = formatTime(timing[0].from?.toInt(), 0)
//                    val timeEndFormatted = formatTime(timing[0].to?.toInt(), 0)
//
//                    binding.timeMorning.text = "Morning : $timeStartFormatted to $timeEndFormatted"
//
//                    val timeStartFormatted1 = formatTime(timing[1].from?.toInt(), 0)
//                    val timeEndFormatted1 = formatTime(timing[1].to?.toInt(), 0)
//
//                    binding.timeAfternoon.text =
//                        "Afternoon : $timeStartFormatted1 to $timeEndFormatted1"
//                    binding.timeEvening.visibility = View.GONE
//                }
//
//                1 -> {
//
//                    val timeStartFormatted = formatTime(timing[0].from?.toInt(), 0)
//                    val timeEndFormatted = formatTime(timing[0].to?.toInt(), 0)
//
//                    binding.timeMorning.text = "Morning : $timeStartFormatted to $timeEndFormatted"
//                    binding.timeEvening.visibility = View.GONE
//                    binding.timeAfternoon.visibility = View.GONE
//                }
//            }

            binding.tvAddress.text =
                "${library.address?.street}, ${library.address?.district}, ${library.address?.state}, ${library.address?.pincode}"

            if (library.photo?.isNotEmpty() == true) {
                Glide.with(context).load(library.photo?.get(0)).placeholder(R.drawable.noimage)
                    .error(R.drawable.noimage).into(binding.imageView)
            }
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

                else -> false
            }
        }
        popupMenu.setOnDismissListener {
            view.setBackgroundColor(Color.WHITE)
        }
        popupMenu.show()
    }

    private fun formatTime(hours: Int?, minutes: Int?): String {
        val hourFormatted = if (hours == 0 || hours == 21) 12 else hours?.rem(12)
        val amPm = if (hours!! < 12) "am" else "pm"
        return String.format("%02d:%02d %s", hourFormatted, minutes, amPm)
    }
}