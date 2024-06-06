package com.example.smoothieshopapp.ui.loginscreen.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smoothieshopapp.data.network.SmoothieApi.firebaseAuth
import com.example.smoothieshopapp.data.repository.UserRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    /**
     * This function is used to validate email and password in register screen,
     * email as a email regex(string@string.string) and password contains 8 digits
     *
     * @param email
     * @param password
     */
    fun loginEntryValid(email: String, password: String): Boolean {
        return !email.isNullOrEmpty() && !password.isNullOrEmpty()
    }

    /**
     * This function is used to validate email and password in register screen,
     * email as a email regex(string@string.string) and password contains 8 digits
     *
     * @param email
     * @param password
     */
    fun registerEntryValid(email: String, password: String): Boolean {
        return email.matches(Regex("^\\S+@\\S+\\.\\S+$"))
                && password.matches(Regex("\\d{8}"))
    }

    /**
     * This function is used to check password re-entered is same as password entered
     *
     * @param pass password entered
     * @param rePass password re-entered
     */
    fun entryRePasswordValid(pass: String, rePass: String): Boolean {
        return pass == rePass
    }

    /**
     * This function is used to sign up a new account with email
     *
     * @param email
     * @param password
     */
    fun signUp(email: String, password: String): Task<AuthResult> {
        return userRepository.register(email, password)
    }

    /**
     * This function is used to sign in an account registered
     *
     * @param email
     * @param password
     */
    fun signIn(email: String, password: String): Task<AuthResult> {
        return userRepository.login(email, password)
    }
}

class LoginViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userRepository) as T
        }
        return super.create(modelClass)
    }
}