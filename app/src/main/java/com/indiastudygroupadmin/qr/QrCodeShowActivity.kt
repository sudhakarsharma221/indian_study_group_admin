package com.indiastudygroupadmin.qr

import android.app.DownloadManager
import android.app.DownloadManager.Request
import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.app_utils.ToastUtil
import com.indiastudygroupadmin.databinding.ActivityQrCodeShowBinding
import java.io.File

class QrCodeShowActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrCodeShowBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeShowBinding.inflate(layoutInflater)
        window.statusBarColor = Color.WHITE
        setContentView(binding.root)
        initListener()
    }

    private fun initListener() {

        val name = intent.getStringExtra("name").toString()
//        val qrUrlIntent = intent.getStringExtra("qrcode").toString()
        val id = intent.getStringExtra("libId").toString()

        Log.d("QRCODEFROMINTENT", "$name ++  $id")

        if (name.isEmpty() || id.isEmpty()) {
            ToastUtil.makeToast(this, "Some Error Occurred")
            finish()
        }

        binding.backButton.setOnClickListener {
            finish()
        }
        binding.libraryName.text = name

        val qrUrl = "https://quickchart.io/qr?text=${id}&size=400%27"

        Log.d("QRCODEFROMINTENT", qrUrl)

        Glide.with(this).load(qrUrl).error(R.drawable.nolibavailable)
            .placeholder(R.drawable.nolibavailable).into(binding.qrImage)
        binding.download.setOnClickListener {
            // Call a method to download the image
            downloadImage(this, qrUrl, "QR Image")
        }
    }

    private fun downloadImage(context: Context, imageUrl: String, fileName: String) {

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = Request(Uri.parse(imageUrl))
        request.setAllowedNetworkTypes(Request.NETWORK_WIFI or Request.NETWORK_MOBILE)
        request.setTitle("Image Download")
        request.setDescription("Downloading Image")
        request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_PICTURES, File.separator + fileName + ".jpg"
        )

        downloadManager.enqueue(request)

        Toast.makeText(this, "Image Downloaded", Toast.LENGTH_SHORT).show()

    }
}