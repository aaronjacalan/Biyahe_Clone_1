package com.android.biyahe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.android.biyahe.utils.toast

class RegisterActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val button_register = findViewById<Button>(R.id.button_register)
        button_register.setOnClickListener {
            Log.e("HELLO", "To Login Button is clicked")

            toast("You have registered")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }


}