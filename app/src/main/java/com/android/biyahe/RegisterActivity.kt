package com.android.biyahe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import com.android.biyahe.utils.toast

class RegisterActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val gotoLogin = findViewById<TextView>(R.id.tv_gotoLogin)
        gotoLogin.setOnClickListener {
            toast("Going to Login")
            startActivity(Intent(this, LoginActivity::class.java))
        }

        val buttonRegister = findViewById<Button>(R.id.btn_register)
        buttonRegister.setOnClickListener {
            Log.e("HELLO", "To Login Button is clicked")

            toast("You have registered")

            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
        }

        val btnFacebook = findViewById<FrameLayout>(R.id.btn_facebook)
        btnFacebook.setOnClickListener {
            toast("FB is CLICKED")
        }

        val btnGoogle = findViewById<FrameLayout>(R.id.btn_google)
        btnGoogle.setOnClickListener {
            toast("Google is CLICKED")
        }

        val btnOutlook = findViewById<FrameLayout>(R.id.btn_outlook)
        btnOutlook.setOnClickListener {
            toast("Outlook is CLICKED")
        }

    }


}