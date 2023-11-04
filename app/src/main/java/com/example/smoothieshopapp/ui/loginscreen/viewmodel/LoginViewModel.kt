package com.example.smoothieshopapp.ui.loginscreen.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel(private val firebaseAuth: FirebaseAuth) : ViewModel() {

    /**
     * This function is used to validate email and password in register screen,
     * email as a email regex(string@string.string) and password contains 8 digits
     *
     * @param email
     * @param password
     */
    fun loginEntryValid(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            return false
        }
        return true
    }

    /**
     * This function is used to validate email and password in register screen,
     * email as a email regex(string@string.string) and password contains 8 digits
     *
     * @param email
     * @param password
     */
    fun registerEntryValid(email: String, password: String): Boolean {
        if (email.matches(Regex("^\\S+@\\S+\\.\\S+$"))
            && password.matches(Regex("\\d{8}"))
        ) {
            return true
        }

        return false
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
     * This function is used to check to see if the user is currently signed in.
     */
    fun isSigned(): Boolean {
        return firebaseAuth.currentUser != null
    }

    /**
     * This function is used to sign up a new account with email
     *
     * @param email
     * @param password
     */
    fun signUp(email: String, password: String): Boolean {
        return firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .isSuccessful
    }

    /**
     * This function is used to sign in an account registered
     *
     * @param email
     * @param password
     * @param action action when successful or failure
     */
    fun signIn(email: String, password: String, action: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                action(task.isSuccessful)

                if (!task.isSuccessful) {
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                }
            }
    }
}

class LoginViewModelFactory(private val firebaseAuth: FirebaseAuth) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(firebaseAuth) as T
        }
        return super.create(modelClass)
    }
}