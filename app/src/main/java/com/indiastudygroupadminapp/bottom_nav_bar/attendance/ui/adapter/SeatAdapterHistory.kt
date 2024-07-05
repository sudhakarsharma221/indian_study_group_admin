package com.indiastudygroupadminapp.bottom_nav_bar.attendance.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.indiastudygroupadminapp.R
import com.indiastudygroupadminapp.databinding.SeatsItemLayoutBinding

class SeatAdapterHistory(
    val context: Context,
    private val totalSeats: Int,
    private val vacantSeats: Int,
    private val onSeatSelected: (Int) -> Unit
) : RecyclerView.Adapter<SeatAdapterHistory.MyViewHolder>() {

    private var selectedSeatIndex: Int? = null

    inner class MyViewHolder(val binding: SeatsItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {}

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
        val isVacant = position < vacantSeats
        val isSelected = selectedSeatIndex == position

        // Set seat drawable
        holder.binding.imageView.setImageResource(
            if (isVacant) {
                R.drawable.availableseat
            } else {
                if (isSelected) R.drawable.bookseat
                else R.drawable.notavailableseat
            }
        )


        holder.itemView.setOnClickListener {
            if (!isVacant) {
                // Handle vacant seat click
//                selectedSeatIndex = if (selectedSeatIndex == position) null else position
                onSeatSelected(totalSeats - position)
                notifyDataSetChanged()
                // Show a toast message if the seat is not vacant
//                Toast.makeText(
//                    context, "Seat at position $position is not available", Toast.LENGTH_SHORT
//                ).show()

            }

            // Deselect the previously selected seat if the position matches the selectedSeatIndex
            if (selectedSeatIndex == position) {
                holder.binding.imageView.setColorFilter(
                    context.resources.getColor(R.color.blue1)
                )
            }
        }
    }
}