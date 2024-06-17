package com.indiastudygroupadmin.notification.ui.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.app_utils.ApiCallsConstant
import com.indiastudygroupadmin.app_utils.IntentUtil
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.bottom_nav_bar.library.ui.LibraryDetailsActivity
import com.indiastudygroupadmin.databinding.NotificationDetailDialogBinding
import com.indiastudygroupadmin.databinding.NotificationItemLayoutBinding
import com.indiastudygroupadmin.qr.QrCodeShowActivity
import com.indiastudygroupadmin.registerScreen.SignInActivity
import com.indiastudygroupadmin.userDetailsApi.model.Notifications
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class NotificationAdapter(
    val context: Context,
    private val list: List<Notifications>,
    private val onMarkClick: (String?) -> Unit
) : Adapter<NotificationAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: NotificationItemLayoutBinding) :
        ViewHolder(binding.root) {
        fun bindView(item: Notifications, context: Context, position: Int) {
            binding.tvHeading.text = item.title
            binding.tvSubHeading.text = item.subtitle
            binding.tvDate.text = formatDate(item.date)


            binding.more.setOnClickListener { view ->
                showPopupMenu(view, item.id)
            }

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val date: Date = inputFormat.parse(item.date)!!
            val currentTime = Date()
            val diffInMillis = currentTime.time - date.time

            // Convert the difference into a human-readable format
            val time = getTimeAgo(diffInMillis)
            binding.tvTime.text = time


            if (item.status == "unread") {
                binding.newNotification.visibility = View.VISIBLE
            } else {
                binding.newNotification.visibility = View.GONE
            }

            binding.notiLayout.setOnClickListener {
                notificationDetailDialog(item, time)
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = NotificationItemLayoutBinding.inflate(
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

    private fun formatDate(input: String?): String {
        // Define the input date format
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        // Parse the timestamp into a Date object
        val date: Date = inputFormat.parse(input)!!

        // Define the output date format
        val outputFormat = SimpleDateFormat("dd/MM/yyyy")
        outputFormat.timeZone = TimeZone.getDefault()

        // Format the Date object into the desired format
        return outputFormat.format(date)
    }

    fun getTimeAgo(diffInMillis: Long): String {
        val diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis)
        val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
        val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        return when {
            diffInSeconds < 60 -> "$diffInSeconds sec ago"
            diffInMinutes < 60 -> "$diffInMinutes min ago"
            diffInHours < 24 -> "$diffInHours hr ago"
            else -> "$diffInDays days ago"
        }
    }

    private fun showPopupMenu(
        view: View, id: String?
    ) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.notification_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_markread -> {
                    onMarkClick(id)
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
        if (hours == null || minutes == null) {
            throw IllegalArgumentException("Hours and minutes cannot be null")
        }

        val adjustedHours = when {
            hours == 24 -> 0
            hours == 0 -> 12
            hours > 12 -> hours - 12
            hours == 12 -> 12
            else -> hours
        }

        val amPm = if (hours < 12 || hours == 24) "AM" else "PM"

        return String.format("%02d:%02d %s", adjustedHours, minutes, amPm)
    }

    private fun notificationDetailDialog(item: Notifications, time: String) {
        val builder = Dialog(context)
        val dialogBinding = NotificationDetailDialogBinding.inflate(builder.layoutInflater)
        builder.setContentView(dialogBinding.root)
        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)
        builder.show()
        builder.setCancelable(true)
        if (item.message?.slotTime != null) {
            val timeStartFormatted1 = formatTime(item.message.slotTime.from?.toInt(), 0)
            val timeEndFormatted1 = formatTime(item.message.slotTime.to?.toInt(), 0)

            dialogBinding.slotTime.text = "Slot Time : $timeStartFormatted1 to $timeEndFormatted1"
        }




        dialogBinding.title.text = item.title
        dialogBinding.date.text = formatDate(item.date)
        dialogBinding.time.text = time
        dialogBinding.slot.text = "Slot Number : ${item.message?.slot?.plus(1)}"
        dialogBinding.subTitle.text = item.subtitle
        dialogBinding.sender.text = item.message?.senderName
        Glide.with(context).load(item.message?.senderDp).placeholder(R.drawable.profile)
            .error(R.drawable.profile).into(dialogBinding.senderPic)
    }

}