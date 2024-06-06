package com.example.smoothieshopapp.data.model

import com.google.gson.annotations.SerializedName

data class Cart(
    @SerializedName("smoothie")
    val smoothie: Smoothie,
    @SerializedName("quantity")
    val quantity: Int
)