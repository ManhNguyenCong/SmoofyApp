package com.example.smoothieshopapp.ui.loginscreen.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smoothieshopapp.R
import com.example.smoothieshopapp.databinding.FragmentStartBinding
import com.example.smoothieshopapp.util.findNavControllerSafely
import com.google.android.material.bottomnavigation.BottomNavigationView


class StartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentStartBinding.inflate(inflater)

        activity?.findViewById<View>(R.id.mainContent)
            ?.findViewById<BottomNavigationView>(R.id.bottomNavView)
            ?.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get ConnectivityManager
        val connectivityManager =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        // Get NetworkCapabilities
        val capabilities =
            connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)

        // Check internet connected
        val connected = capabilities?.let {
            it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) // mobile internet
                    || it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) // wifi internet
                    || it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) // ethernet
        } ?: false

        @Suppress("DEPRECATION")
        android.os.Handler().postDelayed({
            // If has internet connect, navigation to login screen
            if (connected) {
                val action = StartFragmentDirections.actionStartFragmentToLoggingFragment()
                findNavControllerSafely()?.navigate(action)
            } else {
                // If don't has internet connect, notification "No internet connected"
                Toast.makeText(requireContext(), "No internet connected!!!", Toast.LENGTH_SHORT)
                    .show()
            }
        }, 3000) // Delayed 3 seconds before navigation
    }
}