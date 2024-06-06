package com.example.smoothieshopapp.data.network

import com.example.smoothieshopapp.data.model.Bill
import com.example.smoothieshopapp.data.model.Cart
import com.example.smoothieshopapp.data.model.Smoothie
import com.example.smoothieshopapp.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

private const val BASE_URL = "https://smoofy-39ecc-default-rtdb.firebaseio.com/"

private val gson = GsonBuilder()
    .setLenient()
    .create()

private val retrofit =
    Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(BASE_URL)
        .build()

interface SmoothieApiService {
    @GET("users/{uid}.json")
    fun getUserByUID(@Path("uid") uid: String): Call<User>

    @PUT("users/{uid}.json")
    fun updateUserInfo(
        @Path("uid") uid: String,
        @Field("name") name: String,
        @Field("address") address: String,
        @Field("phoneNumber") phoneNumber: String
    ) : Call<Void>

    @GET("smoothies.json")
    fun getAllSmoothies(): Call<Map<String, Smoothie>>

    @GET("smoothies/{id}.json")
    fun getSmoothieById(@Path("id") id: String): Call<Smoothie>

    @GET("users/{uid}/favorites.json")
    fun getFavorites(@Path("uid") uid: String): Call<List<Smoothie>>

    @GET("users/{uid}/cart.json")
    fun getCart(@Path("uid") uid: String): Call<List<Cart>>

    @PUT("users/{uid}/cart.json")
    fun addToCart(@Path("uid") uid: String, @Body carts: List<Cart>): Call<Void>

    @PUT("smoothies/{id}/quantityInStock.json")
    fun updateQuantityInStock(@Path("id") id: String, @Body quantity: Int): Call<Void>

    @PUT("users/{uid}/favorites.json")
    fun setFavorite(@Path("uid") uid: String, @Body favorites: List<Smoothie>): Call<Void>

    @PUT("users/{uid}/cart.json")
    fun updateCart(@Path("uid") uid: String, @Body carts: List<Cart>): Call<List<Cart>>

    @DELETE("users/{uid}/cart.json")
    fun removeAllCart(@Path("uid") uid: String): Call<Void>

    @PUT("bills/{id}.json")
    fun addBill(@Path("id") id: String, @Body bill: Bill): Call<Void>
}

object SmoothieApi {
    // Firebase Authentication
    val firebaseAuth: FirebaseAuth = Firebase.auth

    val api: SmoothieApiService by lazy {
        retrofit.create(SmoothieApiService::class.java)
    }
}