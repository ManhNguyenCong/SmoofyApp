package com.example.smoothieshopapp.util

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.smoothieshopapp.R

/**
 * This function is used to findNavController safer :)))
 *
 */
fun Fragment.findNavControllerSafely(): NavController? {
    return try {
        findNavController()
    } catch (e: IllegalStateException) {
        null
    }
}

/**
 * This function is used to load image with image url
 *
 * @param imageUrl String
 */
fun ImageView.loadImageWithImageUrl(imageUrl: String) {
    // Convert url string to uri object
    val imageUri = imageUrl.toUri().buildUpon().scheme("https").build()
    // Load image
    this.load(imageUri) {
        placeholder(R.drawable.ic_image_loadding)
        error(R.drawable.ic_image_broken)
    }
}