package com.android.biyahe

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.android.biyahe.utils.toast

class LoginActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "")

        val button_register = findViewById<Button>(R.id.button_register)
        button_register.setOnClickListener {
            Log.e("LoginActivity", "Register Button is clicked")

            toast("Welcome Back,   $username")

            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}