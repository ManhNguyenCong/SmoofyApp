package com.example.smoothieshopapp.ui.moothiescreen.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.smoothieshopapp.model.Cart
import com.example.smoothieshopapp.model.Smoothie
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.snapshots
import kotlinx.coroutines.flow.map

private const val TAG = "TAG_TEST"

class SmoothieViewModel(private val database: DatabaseReference) : ViewModel() {
    /**
     * This function is used to get all smoothies in real-time database of firebase
     */
    fun getAllSmoothies(): LiveData<List<Smoothie>> {
        val smoothies = MutableLiveData<List<Smoothie>>()

        database.child("smoothies").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // Get smoothies added as a map
                val mapSmoothieAdded = snapshot.getValue<Map<String, Any?>>()

                if (snapshot.key != null && mapSmoothieAdded != null) {
                    // Convert from Map to Smoothie
                    val smoothie = Smoothie(snapshot.key.toString(), mapSmoothieAdded)
                    // Create a medium list
                    val smoothieList = mutableListOf<Smoothie>()
                    // Add smoothies in live data
                    smoothieList.addAll(smoothies.value ?: listOf())
                    // Add new smoothie
                    smoothieList.add(smoothie)
                    // Update live data
                    smoothies.value = smoothieList
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // Get key of smoothie changed
                val keySmoothieChanged = snapshot.key
                // Get information of smoothie changed as a map
                val mapSmoothieChanged = snapshot.getValue<Map<String, Any?>>()

                if (keySmoothieChanged != null && mapSmoothieChanged != null) {
                    // Init a smoothie with new information
                    val smoothie = Smoothie(snapshot.key.toString(), mapSmoothieChanged)

                    // Create a list of smoothie as a medium list
                    val smoothieList = mutableListOf<Smoothie>()

                    smoothies.value?.also {
                        // Add smoothies added
                        smoothieList.addAll(it)
                        // Get index of smoothie changed
                        val indexOfSmoothieChanged =
                            smoothieList.indexOfFirst { element -> element.id == keySmoothieChanged.toString() }
                        // If smoothie changed is found
                        if (indexOfSmoothieChanged != -1) {
                            // Change to new smoothie
                            smoothieList[indexOfSmoothieChanged] = smoothie
                            // Update live data
                            smoothies.value = smoothieList
                        }
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Get key of smoothie removed
                val keySmoothieRemoved = snapshot.key
                // Get information of smoothie removed as a map
                val mapSmoothieRemoved = snapshot.getValue<Map<String, Any?>>()

                if (keySmoothieRemoved != null && mapSmoothieRemoved != null) {
                    // Convert from Map to Smoothie
                    val smoothie = Smoothie(snapshot.key.toString(), mapSmoothieRemoved)

                    // Create a medium list
                    val smoothieList = mutableListOf<Smoothie>()


                    smoothies.value?.also {
                        // Add smoothies added
                        smoothieList.addAll(it)
                        // Get index of smoothie deleted
                        val indexOfSmoothieRemoved =
                            smoothieList.indexOfFirst { element -> element.id == keySmoothieRemoved.toString() }
                        // If smoothie deleted is found
                        if (indexOfSmoothieRemoved != -1) {
                            // Remove
                            smoothieList.removeAt(indexOfSmoothieRemoved)
                            // Update live data
                            smoothies.value = smoothieList
                        }
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "Moved")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "getAllSmoothies:onCancelled " + error.toException())
            }

        })

        return smoothies
    }

    /**
     * This function is used to get a smoothie by id in real-time of firebase
     */
    fun getSmoothieById(id: String): LiveData<Smoothie> {
        val smoothie = MutableLiveData<Smoothie>()

        database.child("smoothies").child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get key of smoothie
                val keySmoothie = snapshot.key
                // Get smoothie as a Map
                val mapSmoothie = snapshot.getValue<Map<String, Any?>>()

                if (keySmoothie != null && mapSmoothie != null) {
                    // Update live data
                    smoothie.value = Smoothie(keySmoothie, mapSmoothie)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "getSmoothieById: onCancelled " + error.toException())
            }

        })

        return smoothie
    }

    /**
     * This function is used to get smoothies in cart of a user,
     * with user id
     *
     * @param userId
     */
    fun getSmoothiesInCart(userId: String): LiveData<List<Cart>> {
        // List of smoothies in cart
        val smoothiesInCart = MutableLiveData<List<Cart>>()

        database.child("users/$userId/cart").addChildEventListener(
            object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    // Get smoothie id
                    val smoothieId = snapshot.key
                    // Get quantity of smoothie in cart
                    val quantityInCart = snapshot.getValue<Int>()

                    if (smoothieId != null && quantityInCart != null) {
                        // Medium list
                        val list = mutableListOf<Cart>()
                        // Add smoothieInCart in livedata to medium list
                        list.addAll(smoothiesInCart.value ?: listOf())
                        // Add smoothieInCart
                        list.add(Cart(userId, smoothieId, quantityInCart))
                        // Update livedata
                        smoothiesInCart.value = list
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    // Get id of smoothie changed
                    val smoothieId = snapshot.key
                    // Get quantity of smoothie changed in cart
                    val quantityInCart = snapshot.getValue<Int>()

                    if (smoothieId != null && quantityInCart != null) {
                        // Medium list
                        val list = mutableListOf<Cart>()
                        smoothiesInCart.value?.also {
                            // Add smoothieInCart in livedata to medium list
                            list.addAll(it)
                            // Get index of smoothieInCart changed
                            val index = list.indexOfFirst { cart ->
                                cart.smoothieId == smoothieId
                            }
                            if (index != -1) {
                                // Set new smoothieInCart
                                list[index] = Cart(userId, smoothieId, quantityInCart)
                                // Update livedata
                                smoothiesInCart.value = list
                            }
                        }
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    // Get id of smoothie removed
                    val smoothieId = snapshot.key
                    // Get quantity of smoothie removed in cart
                    val quantityInCart = snapshot.getValue<Int>()

                    if (smoothieId != null && quantityInCart != null) {
                        // Medium list
                        val list = mutableListOf<Cart>()
                        smoothiesInCart.value?.also {
                            // Add smoothieInCart in livedata to medium list
                            list.addAll(it)
                            // Get index of smoothieInCart removed
                            val index = list.indexOfFirst { cart ->
                                cart.smoothieId == smoothieId
                            }
                            if (index != -1) {
                                // remove
                                list.removeAt(index)
                                // Update livedata
                                smoothiesInCart.value = list
                            }
                        }
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "onChildMoved: ")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "onCancelled: " + error.toException())
                }

            }
        )

        return smoothiesInCart
    }

    /**
     * This function is used to get list of favorite smoothies by user id
     *
     * @param userId
     */
    fun getAllFavoriteByUserId(userId: String): LiveData<List<String>> {
        val favoriteSmoothies: MutableLiveData<List<String>> by lazy {
            MutableLiveData<List<String>>()
        }

        database.child("users/$userId/favorites").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mapFavorites = snapshot.getValue<Map<String, Any?>>()

                    if (mapFavorites != null) {
                        favoriteSmoothies.value = mapFavorites.keys.toList()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "onCancelled: " + error.toException())
                }

            }
        )

        return favoriteSmoothies
    }

    /**
     * This function is used to add a favorite smoothie to favorites of a user
     *
     * @param userId
     * @param smoothieId
     */
    fun addFavorite(userId: String, smoothieId: String) {
        database.child("users/$userId/favorites").child(smoothieId).setValue("")
    }

    /**
     * This function is used to remove a favorite smoothie in favorites of a user
     */
    fun removeFavorite(userId: String, smoothieId: String) {
        database.child("users/$userId/favorites").child(smoothieId).removeValue()
    }

    /**
     * This function is used to add a smoothie to cart of a user
     *
     * @param userId
     * @param smoothieId
     * @param quantity
     */
    fun addSmoothieToCart(userId: String, smoothieId: String, quantity: Int): Boolean {
        // Add smoothie to cart
        return database.child("users/$userId/cart").child(smoothieId)
            .setValue(quantity)
            .addOnFailureListener {
                Log.e(TAG, "addSmoothieToCart: " + it.message)
            }
            .isSuccessful
    }

    /**
     * This function is used to update quantity in stock of a smoothie when add it to cart
     * or increase/reduce quantity in cart of it
     *
     * @param smoothieId
     * @param quantityExtra if(it > 0) increase else reduce
     */
    fun updateQuantityInStock(smoothieId: String, quantityExtra: Int) {
        // It is used to stop update when first update complete
        // (If not it, when update quantity -> data change -> update quantity, it is endless loop)
        var isComplete = false

        database.child("smoothies/${smoothieId}/quantityInStock").also {
            it.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // If first update isn't complete
                        if(!isComplete) {
                            // Get current quantity in stock
                            val current = snapshot.getValue<Int>() ?: 0
                            // Update quantity in stock
                            it.setValue(current + quantityExtra)
                            // Set completed
                            isComplete = true
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d(TAG, "onCancelled: " + error.toException())
                    }
                }
            )
        }
    }

    /**
     * This function is used to check "Is existed smoothie in user's cart?"
     *
     * @param userId
     * @param smoothieId
     */
    fun isExistedSmoothieInCart(userId: String, smoothieId: String): LiveData<Boolean> {
        return database.child("users/$userId/cart").snapshots.map {
            it.children.map { child ->
                child.key
            }.contains(smoothieId)
        }.asLiveData()
    }

    /**
     * This function is used to update quantity in cart of a smoothie
     *
     * @param userId
     * @param smoothieId
     * @param quantity
     */
    fun updateQuantityInCart(userId: String, smoothieId: String, quantity: Int): Boolean {
        return database.child("users/$userId/cart").child(smoothieId)
            .setValue(quantity)
            .addOnFailureListener {
                Log.e(TAG, "updateQuantityInCart: " + it.message)
            }
            .isSuccessful
    }

    /**
     * This function is used to delete smoothie in cart
     *
     * @param userId
     * @param smoothieId
     */
    fun removeSmoothieFromCart(userId: String, smoothieId: String): Boolean {
        return database.child("users/$userId/cart").child(smoothieId).removeValue().isSuccessful
    }
}

class SmoothieViewModelFactory(private val database: DatabaseReference) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SmoothieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SmoothieViewModel(database) as T
        }
        return super.create(modelClass)
    }
}