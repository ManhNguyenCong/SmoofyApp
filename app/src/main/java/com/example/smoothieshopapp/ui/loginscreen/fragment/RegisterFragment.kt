package com.example.smoothieshopapp.ui.loginscreen.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.smoothieshopapp.R
import com.example.smoothieshopapp.data.repository.UserRepository
import com.example.smoothieshopapp.databinding.FragmentRegisterBinding
import com.example.smoothieshopapp.ui.loginscreen.viewmodel.LoginViewModel
import com.example.smoothieshopapp.ui.loginscreen.viewmodel.LoginViewModelFactory

class RegisterFragment : Fragment() {

    // Binding to fragment_register
    private lateinit var binding: FragmentRegisterBinding

    // View Model
    private val viewModel: LoginViewModel by activityViewModels {
        LoginViewModelFactory(UserRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.cbxAgreeTerms.setOnClickListener {
            // If don't agree terms and conditions, disable button register
            binding.btnRegister.isEnabled = binding.cbxAgreeTerms.isChecked
        }

        // Set event onClick to show terms and conditions
        binding.agreeLawLink.setOnClickListener {
            //todo show terms and conditions
        }

        // Set event onclick for button register
        binding.btnRegister.setOnClickListener {
            // Register a new account
            registerNewAccount()
        }

        // Set event onclick to navigate to login fragment
        binding.loginNav.setOnClickListener {
            activity?.supportFragmentManager?.commit {
                setReorderingAllowed(true)
                replace<LoggingFragment>(R.id.fragmentContainerView)
            }
        }

        return binding.root
    }

    /**
     * This function is used to validate email and password
     */
    private fun entryValid(): Boolean {
        return viewModel.registerEntryValid(
            binding.email.text.toString(), binding.password.text.toString()
        )
    }

    /**
     * This function is used to check re-password is same as password
     */
    private fun confirmPassValid(): Boolean {
        return viewModel.entryRePasswordValid(
            binding.password.text.toString(), binding.confirmPass.text.toString()
        )
    }

    /**
     * This function is used to register a new account
     */
    private fun registerNewAccount() {
        if (entryValid() && confirmPassValid()) {
            // If email and password is correct, register a new account and
            // navigate to login

            viewModel.signUp(binding.email.toString().trim(), binding.password.toString().trim())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        // Hidden error line
                        binding.error.visibility = View.GONE
                        Toast.makeText(requireContext(), "Register successful!", Toast.LENGTH_SHORT)
                            .show()
                        activity?.finish()
                    } else {
                        // Case: Duplicate email
                        binding.error.text = resources.getStringArray(R.array.errorRegister)[3]
                        // Show error line
                        binding.error.visibility = View.VISIBLE
                    }
                }
        } else {
            // If has error
            if (!entryValid()) {
                // with email/password
                binding.error.text = resources.getStringArray(R.array.errorRegister)[1]
            } else {
                // with re-password
                binding.error.text = resources.getStringArray(R.array.errorRegister)[2]
            }

            // Show error line
            binding.error.visibility = View.VISIBLE
        }
    }
}