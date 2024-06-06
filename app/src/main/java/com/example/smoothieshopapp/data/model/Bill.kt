package com.example.smoothieshopapp.data.model

import com.google.gson.annotations.SerializedName

data class Bill(
    @SerializedName("id")
    val id: String,
    @SerializedName("user")
    val user: User,
    @SerializedName("carts")
    val smoothies: List<Cart>,
    @SerializedName("date")
    val date: Long,
    @SerializedName("status")
    val status: Int
)