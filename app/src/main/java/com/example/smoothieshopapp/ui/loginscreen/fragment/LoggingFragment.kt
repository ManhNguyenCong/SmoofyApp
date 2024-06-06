package com.example.smoothieshopapp.ui.loginscreen.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.smoothieshopapp.R
import com.example.smoothieshopapp.data.repository.UserRepository
import com.example.smoothieshopapp.databinding.FragmentLoggingBinding
import com.example.smoothieshopapp.ui.loginscreen.viewmodel.LoginViewModel
import com.example.smoothieshopapp.ui.loginscreen.viewmodel.LoginViewModelFactory

class LoggingFragment : Fragment() {
    // Binding to fragment_logging
    private lateinit var binding: FragmentLoggingBinding

    // View model
    private val viewModel: LoginViewModel by activityViewModels {
        LoginViewModelFactory(UserRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentLoggingBinding.inflate(inflater, container, false)

        // Set event onClick for "Forget password"
        binding.forgetPassword.setOnClickListener {
            //todo navigation to fragment forget password
            Toast.makeText(
                requireContext(),
                "This request will be released in the future",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Set event onclick for button login
        binding.btnLogin.setOnClickListener {
            if (entryValid()) {
                // Sign in
                signIn()
            } else {
                // Notification error
                Toast.makeText(
                    requireContext(),
                    resources.getStringArray(R.array.errorRegister)[0],
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Set event onclick to navigate to register
        binding.registerNav.setOnClickListener {
            activity?.supportFragmentManager?.commit {
                setReorderingAllowed(true)
                replace<RegisterFragment>(R.id.fragmentContainerView)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * This function is used to validate email and password
     */
    private fun entryValid(): Boolean {
        return viewModel.loginEntryValid(
            binding.email.text.toString(),
            binding.password.text.toString()
        )
    }

    /**
     * This function is used to sign in an account registered
     */
    private fun signIn() {
        viewModel.signIn(
            binding.email.text.toString(),
            binding.password.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    requireContext(),
                    "Login successful!",
                    Toast.LENGTH_SHORT
                ).show()
                activity?.finish()
            } else {
                // Notification error
                Toast.makeText(
                    requireContext(),
                    resources.getStringArray(R.array.errorRegister)[0],
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}