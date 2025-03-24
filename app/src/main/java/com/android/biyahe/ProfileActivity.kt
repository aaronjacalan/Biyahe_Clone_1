package com.android.biyahe

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView

class ProfileActivity : Activity() {

    private lateinit var UIDTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var shortDescTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        UIDTextView = findViewById(R.id.UIDTextView)
        usernameTextView = findViewById(R.id.text_username)
        shortDescTextView = findViewById(R.id.text_shortDescription)

        loadProfileData()

        val button_goBack = findViewById<ImageView>(R.id.viewProfile_goBack)
        button_goBack.setOnClickListener {
            Log.e("ProfileActivity", "Go Back")
            finish()
        }

        val button_editProfile = findViewById<ImageView>(R.id.viewProfile_Edit)
        button_editProfile.setOnClickListener {
            Log.e("ProfileActivity", "Goto Edit Profile")

            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == Activity.RESULT_OK) {
            loadProfileData()
        }
    }

    private fun loadProfileData() {
        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        UIDTextView.text = sharedPref.getString("UID", "")
        usernameTextView.text = sharedPref.getString("username", "")
        shortDescTextView.text = sharedPref.getString("shortDesc", "")
    }

    companion object {
        private const val REQUEST_CODE_EDIT_PROFILE = 1
    }
}