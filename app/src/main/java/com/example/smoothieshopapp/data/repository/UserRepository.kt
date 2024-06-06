package com.example.smoothieshopapp.data.repository

import com.example.smoothieshopapp.data.model.User
import com.example.smoothieshopapp.data.network.SmoothieApi
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import retrofit2.Call

class UserRepository() {

    fun login(email: String, password: String): Task<AuthResult> =
        SmoothieApi.firebaseAuth.signInWithEmailAndPassword(email, password)

    fun register(email: String, password: String): Task<AuthResult> =
        SmoothieApi.firebaseAuth.createUserWithEmailAndPassword(email, password)

    fun getUserByUID(uid: String): Call<User> = SmoothieApi.api.getUserByUID(uid)

    fun updateUserInfo(name: String, address: String, phoneNumber: String): Call<Void>? {
        val uid = SmoothieApi.firebaseAuth.currentUser?.uid ?: return null

        return SmoothieApi.api.updateUserInfo(uid, name, address, phoneNumber)
    }
}