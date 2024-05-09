package com.indiastudygroupadmin.bottom_nav_bar.more.wallet

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.indiastudygroupadmin.R
import com.indiastudygroupadmin.databinding.ActivityWalletBinding

class WalletActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWalletBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        initListener()
    }

    private fun initListener() {

        binding.addMoneyButton.setOnClickListener {
            binding.paymentHistoryLayout.visibility = View.GONE
            binding.addMoneyLayout.visibility = View.VISIBLE
        }


        binding.paymentHistoryButton.setOnClickListener {
            binding.paymentHistoryLayout.visibility = View.VISIBLE
            binding.addMoneyLayout.visibility = View.GONE
        }

//        binding.filterButton.setOnClickListener {
//            showFilterDialog()
//        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

//    private fun showFilterDialog() {
//        val bottomDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
//        val dialogBinding = FilterWalletBottomDialogBinding.inflate(layoutInflater)
//        bottomDialog.setContentView(dialogBinding.root)
//        bottomDialog.setCancelable(true)
//        bottomDialog.show()
////        dialogBinding.messageTv.text = message
////        dialogBinding.continueButton.setOnClickListener {
////            HideKeyboard.hideKeyboard(requireContext(), binding.phoneEt.windowToken)
////            bottomDialog.dismiss()
////        }
//    }
}