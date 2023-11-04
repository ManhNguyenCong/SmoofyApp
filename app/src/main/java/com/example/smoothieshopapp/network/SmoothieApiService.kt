package com.example.smoothieshopapp.network

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

object SmoothieApi {
    // Database reference
    val dbRef: DatabaseReference = Firebase.database.reference

    // Firebase Authentication
    val firebaseAuth: FirebaseAuth = Firebase.auth
}