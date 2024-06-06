package com.example.smoothieshopapp.data.model

import com.google.gson.annotations.SerializedName

data class Smoothie(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("price")
    val price: Float,
    @SerializedName("rating")
    val rating: Float,
    @SerializedName("quantityInStock")
    val quantityInStock: Int
)

/**
 * This function is used to format price
 */
fun Smoothie.priceFormatted(): String {
    return String.format("%.2f$", this.price)
}

/**
 * This function is used to format rating
 */
fun Smoothie.ratingFormatted(): String {
    return String.format("%.0f", this.rating)
}