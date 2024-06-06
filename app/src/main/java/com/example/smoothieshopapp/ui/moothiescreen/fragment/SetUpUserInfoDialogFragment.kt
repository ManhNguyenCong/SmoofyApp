package com.example.smoothieshopapp.ui.moothiescreen.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.smoothieshopapp.R
import com.example.smoothieshopapp.data.model.User
import com.example.smoothieshopapp.databinding.FragmentSetupUserInfoDialogBinding

/**
 * This class is used to create a setup user info dialog
 *
 * @param user current user information
 * @param saveNewUserInfo
 */
class SetUpUserInfoDialogFragment(
    private val user: User,
    private val saveNewUserInfo: (String, String, String) -> Unit
) : DialogFragment() {

    private lateinit var binding: FragmentSetupUserInfoDialogBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Create dialog builder
            val builder = AlertDialog.Builder(context)
            // Inflate layout for dialog
            val dialogLayout =
                layoutInflater.inflate(R.layout.fragment_setup_user_info_dialog, null)
            // Binding to fragment_setup_user_info_dialog
            binding = FragmentSetupUserInfoDialogBinding.bind(dialogLayout)
            // Set variable "userInfor" in fragment
            binding.userInfo = user

            // Set layout for dialog
            builder.setView(dialogLayout)
                .setTitle(getString(R.string.setupUserInfoDialogTitle))
                .setPositiveButton(getString(R.string.oke)) { _, _ ->
                    // Set event positive button is clicked
                    saveNewUserInfo(
                        binding.txtName.text.toString(),
                        binding.txtAddress.text.toString(),
                        binding.txtPhoneNumber.text.toString()
                    )
                }

            // Create dialog
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        const val TAG = "SetUpUserInfoDialog"
    }
}