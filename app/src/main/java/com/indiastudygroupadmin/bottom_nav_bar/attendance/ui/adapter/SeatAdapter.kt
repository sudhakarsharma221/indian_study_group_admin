package com.indiastudygroupadmin.bottom_nav_bar.attendance.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.bottom_nav_bar.library.model.SeatDetails
import com.indiastudygroupadmin.databinding.SeatsItemLayoutBinding

class SeatAdapter(
    val context: Context,
    private val totalSeats: Int,
    private val vacantSeats: Int,
    private val list: ArrayList<SeatDetails>,
    private val onSeatSelected: (Int) -> Unit
) : RecyclerView.Adapter<SeatAdapter.MyViewHolder>() {

    private var selectedSeatIndex: Int? = null

    inner class MyViewHolder(val binding: SeatsItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: SeatDetails, context: Context, position: Int) {

            // Set seat drawable
            binding.imageView.setImageResource(
                if (!item.isBooked!!) {
                    R.drawable.availableseat
                } else {
                    when (item.status) {
                        "booked" -> {
                            R.drawable.notavailableseat
                        }

                        "ongoing" -> {
                            binding.imageView.setColorFilter(
                                context.resources.getColor(R.color.blue1)
                            )
                            R.drawable.bookseat
                        }

                        else -> {
                            R.drawable.availableseat

                        }
                    }
                }
            )


            binding.imageView.setOnClickListener {
                if (item.isBooked!!) {
                    // Handle vacant seat click
//                selectedSeatIndex = if (selectedSeatIndex == position) null else position
                    onSeatSelected(position + 1)
                    notifyDataSetChanged()
                    // Show a toast message if the seat is not vacant
//                Toast.makeText(
//                    context, "Seat at position $position is not available", Toast.LENGTH_SHORT
//                ).show()

                }

                // Deselect the previously selected seat if the position matches the selectedSeatIndex
                if (selectedSeatIndex == position) {
                    binding.imageView.setColorFilter(
                        context.resources.getColor(R.color.blue1)
                    )
                }
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SeatsItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return totalSeats
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        list[position].let {
            holder.bindView(it, context, position)
        }


//        val isVacant = position < vacantSeats
//        val isSelected = selectedSeatIndex == position


    }
}