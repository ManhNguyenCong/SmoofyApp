package com.example.smoothieshopapp.network

import com.example.smoothieshopapp.model.NotificationPush
import com.example.smoothieshopapp.util.Constants.Companion.BASE_URL
import com.example.smoothieshopapp.util.Constants.Companion.CONTENT_TYPE
import com.example.smoothieshopapp.util.Constants.Companion.SERVER_KEY
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface NotificationService {
    @Headers("Authorization: key = $SERVER_KEY", "Content-type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(@Body notification: NotificationPush): Response<ResponseBody>
}

object SmoothieApi {
    // Database reference
    val dbRef: DatabaseReference = Firebase.database.reference
    // Firebase Authentication
    val firebaseAuth: FirebaseAuth = Firebase.auth

    // Notification service with retrofit
    val notificationService: NotificationService by lazy {
        retrofit.create(NotificationService::class.java)
    }

}