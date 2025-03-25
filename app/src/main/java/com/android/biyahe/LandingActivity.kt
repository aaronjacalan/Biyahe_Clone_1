package com.android.biyahe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView

class LandingActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val btn_settings = findViewById<ImageView>(R.id.btn_settings)
        btn_settings.setOnClickListener {
            Log.e("Landing Activity", "Proceeding to settings")
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        val btn_profile = findViewById<ImageButton>(R.id.btn_profile)
        btn_profile.setOnClickListener {
            Log.e("Landing Activity", "Proceeding to profile")
            startActivity(Intent(this, ProfileActivity::class.java))
        }



    }
}