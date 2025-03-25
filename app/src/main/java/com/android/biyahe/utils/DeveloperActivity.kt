package com.android.biyahe.utils

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import com.android.biyahe.R

class DeveloperActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)

        val btnBack = findViewById<ImageButton>(R.id.developer_previous)
        btnBack.setOnClickListener() {
            Log.e("DeveloperActivity", "Retracing back towards SettingActivity")
            finish()
        }

    }
}