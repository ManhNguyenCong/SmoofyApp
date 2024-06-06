package com.example.smoothieshopapp.data.repository

import android.util.Log
import com.example.smoothieshopapp.data.model.Bill
import com.example.smoothieshopapp.data.model.Cart
import com.example.smoothieshopapp.data.model.Smoothie
import com.example.smoothieshopapp.data.network.SmoothieApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SmoothieRepository() {
    fun getAllSmoothies(): Call<Map<String, Smoothie>> = SmoothieApi.api.getAllSmoothies()

    fun getSmoothieById(id: String): Call<Smoothie> = SmoothieApi.api.getSmoothieById(id)

    fun getFavorites(): Call<List<Smoothie>>? {
        val uid = SmoothieApi.firebaseAuth.currentUser?.uid ?: return null

        return SmoothieApi.api.getFavorites(uid)
    }

    fun getCart(uid: String): Call<List<Cart>> = SmoothieApi.api.getCart(uid)

    fun addToCart(uid: String, cart: Cart, carts: List<Cart>): Call<Void> {
        SmoothieApi.api.updateQuantityInStock(
            cart.smoothie.id,
            cart.smoothie.quantityInStock - cart.quantity
        ).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("Test Smoothie", "onResponse: $response")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("Test Smoothie", "onFailure: ${t.stackTraceToString()}")
            }
        })

        val tmp = mutableListOf<Cart>()
        tmp.addAll(carts)
        tmp.add(cart)
        return SmoothieApi.api.addToCart(uid, tmp)
    }

    fun setFavorite(smoothie: Smoothie, favorites: List<Smoothie>): Call<Void>? {
        val uid = SmoothieApi.firebaseAuth.currentUser?.uid ?: return null

        val tmp = mutableListOf<Smoothie>()
        tmp.addAll(favorites)
        tmp.add(smoothie)
        return SmoothieApi.api.setFavorite(uid, tmp)
    }

    fun removeFavorite(smoothie: Smoothie, favorites: List<Smoothie>): Call<Void>? {
        val uid = SmoothieApi.firebaseAuth.currentUser?.uid ?: return null

        val tmp = mutableListOf<Smoothie>()
        tmp.addAll(favorites)
        tmp.remove(smoothie)
        return SmoothieApi.api.setFavorite(uid, tmp)
    }

    fun updateCart(cart: Cart, carts: List<Cart>): Call<List<Cart>>? {
        val uid = SmoothieApi.firebaseAuth.currentUser?.uid ?: return null

        val oldCart = carts.first { it.smoothie.id == cart.smoothie.id }
        SmoothieApi.api.updateQuantityInStock(
            cart.smoothie.id,
            cart.smoothie.quantityInStock + (cart.quantity - oldCart.quantity)
        ).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("Test Smoothie", "onResponse: $response")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("Test Smoothie", "onFailure: ${t.stackTraceToString()}")
            }
        })

        val tmp = mutableListOf<Cart>()
        tmp.addAll(carts)
        tmp[tmp.indexOfFirst { it.smoothie.id == cart.smoothie.id }] = cart
        return SmoothieApi.api.updateCart(uid, tmp)
    }

    fun removeCart(cart: Cart, carts: List<Cart>): Call<List<Cart>>? {
        val uid = SmoothieApi.firebaseAuth.currentUser?.uid ?: return null

        SmoothieApi.api.updateQuantityInStock(
            cart.smoothie.id,
            cart.smoothie.quantityInStock + cart.quantity
        ).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("Test Smoothie", "onResponse: $response")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("Test Smoothie", "onFailure: ${t.stackTraceToString()}")
            }
        })

        val tmp = mutableListOf<Cart>()
        tmp.addAll(carts)
        tmp.removeIf { it.smoothie.id == cart.smoothie.id }
        return SmoothieApi.api.updateCart(uid, tmp)
    }

    fun addBill(bill: Bill): Call<Void> {
        SmoothieApi.api.removeAllCart(bill.user.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("Test Smoothie", "onResponse: $response")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("Test Smoothie", "onFailure: ${t.stackTraceToString()}")
            }
        })
        return SmoothieApi.api.addBill(bill.id, bill)
    }
}