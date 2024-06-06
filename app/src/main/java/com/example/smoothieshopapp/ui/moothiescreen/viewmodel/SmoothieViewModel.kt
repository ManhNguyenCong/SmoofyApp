package com.example.smoothieshopapp.ui.moothiescreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smoothieshopapp.data.model.Bill
import com.example.smoothieshopapp.data.model.Cart
import com.example.smoothieshopapp.data.model.Smoothie
import com.example.smoothieshopapp.data.model.User
import com.example.smoothieshopapp.data.repository.SmoothieRepository
import com.example.smoothieshopapp.data.repository.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "SmoothieViewModel"

class SmoothieViewModel(
    private val userRepository: UserRepository,
    private val smoothieRepository: SmoothieRepository
) : ViewModel() {

    /**
     * This function is used to get user information by user id
     *
     * @param uid
     */
    fun getUserByUID(uid: String, onSuccess: (User?) -> Unit, onFailure: (String) -> Unit) {
        userRepository.getUserByUID(uid).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    onSuccess(response.body())
                } else {
                    // TODO on get user response fail
                    onFailure(response.toString())
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                // TODO on get user fail
                onFailure(t.stackTraceToString())
            }
        })
    }

    fun getSpecialSmoothies(onSuccess: (List<Smoothie>) -> Unit, onFailure: (String) -> Unit) {
        smoothieRepository.getAllSmoothies().enqueue(object : Callback<Map<String, Smoothie>> {
            override fun onResponse(
                call: Call<Map<String, Smoothie>>,
                response: Response<Map<String, Smoothie>>
            ) {
                if (response.isSuccessful) {
                    val specials = response.body()?.values?.toList()?.let { smoothies ->
                        smoothies.filter { smoothie ->
                            smoothie.rating >= 4
                        }.sortedByDescending { smoothie -> smoothie.rating }
                    } ?: listOf()

                    onSuccess(specials)
                } else {
                    onFailure(response.toString())
                }
            }

            override fun onFailure(call: Call<Map<String, Smoothie>>, t: Throwable) {
                onFailure(t.stackTraceToString())
            }
        })
    }

    fun getAllSmoothies(onSuccess: (List<Smoothie>) -> Unit, onFailure: (String) -> Unit) {
        smoothieRepository.getAllSmoothies().enqueue(object : Callback<Map<String, Smoothie>> {
            override fun onResponse(
                call: Call<Map<String, Smoothie>>,
                response: Response<Map<String, Smoothie>>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body()?.values?.toList()?.sortedBy { it.name } ?: listOf())
                } else {
                    onFailure(response.toString())
                }
            }

            override fun onFailure(call: Call<Map<String, Smoothie>>, t: Throwable) {
                onFailure(t.stackTraceToString())
            }
        })
    }

    fun getRandomSmoothies(onSuccess: (List<Smoothie>) -> Unit, onFailure: (String) -> Unit) {
        smoothieRepository.getAllSmoothies().enqueue(object : Callback<Map<String, Smoothie>> {
            override fun onResponse(
                call: Call<Map<String, Smoothie>>,
                response: Response<Map<String, Smoothie>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.values?.toList()?.let { smoothies ->
                        onSuccess(
                            if (smoothies.size > 5) {
                                val indexRandom = (0..(smoothies.size - 1 - 5)).random()
                                smoothies.subList(indexRandom, indexRandom + 4)
                            } else {
                                smoothies
                            }
                        )
                    } ?: onFailure("null")
                } else {
                    onFailure(response.toString())
                }
            }

            override fun onFailure(call: Call<Map<String, Smoothie>>, t: Throwable) {
                onFailure(t.stackTraceToString())
            }
        })
    }

    fun getSmoothieById(id: String, onSuccess: (Smoothie) -> Unit, onFailure: (String) -> Unit) {
        smoothieRepository.getSmoothieById(id).enqueue(object : Callback<Smoothie> {
            override fun onResponse(call: Call<Smoothie>, response: Response<Smoothie>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onFailure(response.toString())
                }
            }

            override fun onFailure(call: Call<Smoothie>, t: Throwable) {
                onFailure(t.stackTraceToString())
            }
        })
    }

    fun getFavorites(onSuccess: (List<Smoothie>?) -> Unit, onFailure: (String) -> Unit) {
        smoothieRepository.getFavorites()?.enqueue(object : Callback<List<Smoothie>> {
            override fun onResponse(
                call: Call<List<Smoothie>>,
                response: Response<List<Smoothie>>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body())
                } else {
                    onFailure(response.toString())
                }
            }

            override fun onFailure(call: Call<List<Smoothie>>, t: Throwable) {
                onFailure(t.stackTraceToString())
            }

        }) ?: onFailure("No login")
    }

    fun getCart(uid: String, onSuccess: (List<Cart>) -> Unit, onFailure: (String) -> Unit) {
        smoothieRepository.getCart(uid).enqueue(object : Callback<List<Cart>> {
            override fun onResponse(call: Call<List<Cart>>, response: Response<List<Cart>>) {
                if (response.isSuccessful) {
                    onSuccess(response.body() ?: listOf())
                } else {
                    onFailure(response.toString())
                }
            }

            override fun onFailure(call: Call<List<Cart>>, t: Throwable) {
                onFailure(t.stackTraceToString())
            }
        })
    }

    fun addToCart(
        uid: String,
        cart: Cart,
        carts: List<Cart>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        smoothieRepository.addToCart(uid, cart, carts).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(response.toString())
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure(t.stackTraceToString())
            }
        })
    }

    fun setFavorite(
        smoothie: Smoothie,
        favorites: List<Smoothie>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        smoothieRepository.setFavorite(smoothie, favorites)?.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(response.toString())
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure(t.stackTraceToString())
            }
        }) ?: onFailure("No Login")
    }

    fun removeFavorite(
        smoothie: Smoothie,
        favorites: List<Smoothie>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        smoothieRepository.removeFavorite(smoothie, favorites)?.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(response.toString())
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure(t.stackTraceToString())
            }
        }) ?: onFailure("No Login")
    }

    fun updateUserInfo(
        name: String,
        address: String,
        phoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        userRepository.updateUserInfo(name, address, phoneNumber)?.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(response.toString())
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure(t.stackTraceToString())
            }
        }) ?: onFailure("No login")
    }

    fun updateCart(
        cart: Cart,
        carts: List<Cart>,
        onSuccess: (List<Cart>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        smoothieRepository.updateCart(cart, carts)?.enqueue(object : Callback<List<Cart>> {
            override fun onResponse(call: Call<List<Cart>>, response: Response<List<Cart>>) {
                if (response.isSuccessful) {
                    onSuccess(response.body() ?: listOf())
                } else {
                    onFailure(response.toString())
                }
            }

            override fun onFailure(call: Call<List<Cart>>, t: Throwable) {
                onFailure(t.stackTraceToString())
            }
        }) ?: onFailure("No login")
    }

    fun removeCart(
        cart: Cart,
        carts: List<Cart>,
        onSuccess: (List<Cart>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        smoothieRepository.removeCart(cart, carts)?.enqueue(object : Callback<List<Cart>> {
            override fun onResponse(call: Call<List<Cart>>, response: Response<List<Cart>>) {
                if (response.isSuccessful) {
                    onSuccess(response.body() ?: listOf())
                } else {
                    onFailure(response.toString())
                }
            }

            override fun onFailure(call: Call<List<Cart>>, t: Throwable) {
                onFailure(t.stackTraceToString())
            }
        }) ?: onFailure("No login")
    }

    fun addBill(bill: Bill, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        smoothieRepository.addBill(bill).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(response.toString())
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure(t.stackTraceToString())
            }
        })
    }
}

class SmoothieViewModelFactory(
    private val userRepository: UserRepository,
    private val smoothieRepository: SmoothieRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SmoothieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SmoothieViewModel(userRepository, smoothieRepository) as T
        }
        return super.create(modelClass)
    }
}