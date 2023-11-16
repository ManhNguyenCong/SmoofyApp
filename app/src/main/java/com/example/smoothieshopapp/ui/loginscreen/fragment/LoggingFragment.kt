package com.example.smoothieshopapp.ui.loginscreen.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.smoothieshopapp.R
import com.example.smoothieshopapp.databinding.FragmentLoggingBinding
import com.example.smoothieshopapp.network.SmoothieApi
import com.example.smoothieshopapp.ui.loginscreen.viewmodel.LoginViewModel
import com.example.smoothieshopapp.ui.loginscreen.viewmodel.LoginViewModelFactory
import com.example.smoothieshopapp.util.findNavControllerSafely

class LoggingFragment : Fragment() {
    // Arguments of LoggingFragment in nav_graph
    private val args: LoggingFragmentArgs by navArgs()

    // Binding to fragment_logging
    private lateinit var binding: FragmentLoggingBinding

    // View model
    private val viewModel: LoginViewModel by activityViewModels {
        LoginViewModelFactory(SmoothieApi.firebaseAuth)
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

            if (isEntryValid()) {
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
            val action = LoggingFragmentDirections.actionLoggingFragmentToRegisterFragment()
            findNavControllerSafely()?.navigate(action)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If navigate from register fragment after register successful,
        // fill email and password
        if (args.email != "none" && args.password != "none") {
            binding.email.setText(args.email)
            binding.password.setText(args.password)
        }
    }

    /**
     * This function is used to validate email and password
     */
    private fun isEntryValid(): Boolean {
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
        ) { isSuccessful ->
            if(isSuccessful) {
                // Navigation to SmoothieListFragment()
                val action = LoggingFragmentDirections.actionLoggingFragmentToSmoothieListFragment()
                findNavControllerSafely()?.navigate(action)
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