package com.android.biyahe

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout

class OpeningActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
//        val uid = sharedPref.getString("UID", null)
//
//        if (uid != null) {
//            startActivity(Intent(this, LandingActivity::class.java))
//            finish()
//            return
//        }

        setContentView(R.layout.activity_opening)

        val gotoRegister = findViewById<Button>(R.id.btn_getStarted)
        gotoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

}