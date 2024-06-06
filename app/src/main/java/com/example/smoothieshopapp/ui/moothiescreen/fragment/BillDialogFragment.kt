package com.example.smoothieshopapp.ui.moothiescreen.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.smoothieshopapp.R
import com.example.smoothieshopapp.data.model.User
import com.example.smoothieshopapp.databinding.FragmentBillDialogBinding

/**
 * This class is used to setup a bill dialog fragment
 * which show user info and products want to payment
 *
 * @param user
 * @param products They have template is "name x quantity" and split by "\n"
 * @param totalPrice It have template "total$"
 * @param onPositiveButtonClick
 */
class BillDialogFragment(
    private val user: User,
    private val products: String,
    private val totalPrice: String,
    private val onPositiveButtonClick: () -> Unit
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Create dialog builder
            val builder = AlertDialog.Builder(context)
            // Inflate layout for dialog fragment
            val dialogFragment = layoutInflater.inflate(R.layout.fragment_bill_dialog, null)
            // Binding to fragment_bill_dialog
            val binding = FragmentBillDialogBinding.bind(dialogFragment)
            // Load user info
            user.also {
                binding.userName.text = it.name
                binding.phoneNumber.text = it.phoneNumber
                binding.address.text = it.address
            }
            // Load products
            binding.products.text = products
            // Load total price
            binding.totalPrice.text = totalPrice
            // Set layout for dialog
            builder.setView(dialogFragment)
                .setPositiveButton(getString(R.string.oke)) { _, _ ->
                    // Confirm payment event
                    onPositiveButtonClick()
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ -> }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        const val TAG = "BillDialog"
    }
}