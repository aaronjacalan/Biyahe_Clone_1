package com.android.biyahe

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.android.biyahe.utils.isInvalid
import com.android.biyahe.utils.toast

class LoginActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "")
        val et_username = findViewById<EditText>(R.id.et_username)
        val et_password = findViewById<EditText>(R.id.et_password)

        et_username.setText(username)

        val btn_login = findViewById<Button>(R.id.btn_login)
        btn_login.setOnClickListener {
            // Temporary
            if(et_username.isInvalid() || et_password.isInvalid()) {
                toast("Please register an account first!")
                return@setOnClickListener
            }

            Log.e("LoginActivity", "Login Button is clicked")
            toast("Welcome Back,   $username")
            startActivity(Intent(this, LandingActivity::class.java))
        }

        // To be decided if functionalities are to be added
        val btnGoogle = findViewById<ImageButton>(R.id.login_google)
        btnGoogle.setOnClickListener {

        }

        val btnOutlook = findViewById<ImageButton>(R.id.login_outlook)
        btnOutlook.setOnClickListener {

        }

        val btnFacebook= findViewById<ImageButton>(R.id.login_facebook)
        btnFacebook.setOnClickListener {

        }
    }
}