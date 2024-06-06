package com.example.smoothieshopapp.ui.loginscreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.smoothieshopapp.R
import com.example.smoothieshopapp.ui.loginscreen.fragment.LoggingFragment

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportFragmentManager.commit {
            add<LoggingFragment>(R.id.fragmentContainerView)
        }
    }
}