package com.example.smoothieshopapp.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    @SerializedName("email")
    val email: String,
    @SerializedName("favorites")
    val favorites: List<Smoothie>?,
    @SerializedName("cart")
    val cart: List<Cart>?
)
