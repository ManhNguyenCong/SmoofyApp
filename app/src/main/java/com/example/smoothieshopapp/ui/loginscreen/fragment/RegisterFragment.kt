package com.example.smoothieshopapp.ui.loginscreen.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.smoothieshopapp.R
import com.example.smoothieshopapp.databinding.FragmentRegisterBinding
import com.example.smoothieshopapp.network.SmoothieApi
import com.example.smoothieshopapp.ui.loginscreen.viewmodel.LoginViewModel
import com.example.smoothieshopapp.ui.loginscreen.viewmodel.LoginViewModelFactory
import com.example.smoothieshopapp.util.findNavControllerSafely

class RegisterFragment : Fragment() {

    // Binding to fragment_register
    private lateinit var binding: FragmentRegisterBinding

    // View Model
    private val viewModel: LoginViewModel by activityViewModels {
        LoginViewModelFactory(SmoothieApi.firebaseAuth)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoggingFragment()
            findNavControllerSafely()?.navigate(action)
        }

        return binding.root
    }

    /**
     * This function is used to validate email and password
     */
    private fun isEntryValid(): Boolean {
        return viewModel.registerEntryValid(
            binding.email.text.toString(),
            binding.password.text.toString()
        )
    }

    /**
     * This function is used to check re-password is same as password
     */
    private fun isRePasswordValid(): Boolean {
        return viewModel.entryRePasswordValid(
            binding.password.text.toString(),
            binding.rePassword.text.toString()
        )
    }

    /**
     * This function is used to register a new account
     */
    private fun registerNewAccount() {
        if (isEntryValid() && isRePasswordValid()) {
            // If email and password is correct, register a new account and
            // navigate to login

            //Sign up a new account
            if(signUp()) {
                // Hidden error line
                binding.error.visibility = View.GONE
                // Navigate to login
                val action = RegisterFragmentDirections
                    .actionRegisterFragmentToLoggingFragment(
                        email = binding.email.text.toString(),
                        password = binding.password.text.toString()
                    )
                findNavControllerSafely()?.navigate(action)
            } else {
                // Case: Duplicate email
                binding.error.text = resources.getStringArray(R.array.errorRegister)[3]
                // Show error line
                binding.error.visibility = View.VISIBLE
            }
        } else {
            // If has error
            if (!isEntryValid()) {
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

    /**
     * This function is used to sign up a new account with email
     */
    private fun signUp(): Boolean {
        return viewModel.signUp(
            binding.email.text.toString(),
            binding.password.text.toString()
        )
    }
}