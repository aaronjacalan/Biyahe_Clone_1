package com.android.biyahe.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.android.biyahe.R
import com.android.biyahe.databinding.ActivitySettingsBinding
import com.android.biyahe.dialogs.LogoutDialog
import com.google.android.material.imageview.ShapeableImageView

class SettingsActivity : Activity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var userIcon: ShapeableImageView
    private lateinit var username: TextView
    private lateinit var userID: TextView
    private val     TAG = "SettingsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userIcon = findViewById(R.id.CircleImageIcon)
        username = findViewById(R.id.text_username)
        userID = findViewById(R.id.UIDTextView)

        setupClickListeners()
        loadProfileData()
    }

    private fun setupClickListeners() {
        binding.settingsViewProfile.setOnClickListener {
            Log.d(TAG, "Profile button clicked")
            navigateTo(ProfileActivity::class.java)
        }

        binding.logoutProfileButton.setOnClickListener {
            Log.d(TAG, "Logout button clicked")
            showLogoutDialog()
        }

        binding.settingsGoBack.setOnClickListener {
            Log.d(TAG, "Go back button clicked")
            finish()
        }

        val developerPageClickListener = View.OnClickListener {
            Log.d(TAG, "Developer page button clicked")
            navigateTo(DeveloperActivity::class.java)
        }

        binding.imageAboutDevelopersPage.setOnClickListener(developerPageClickListener)
        binding.textAboutDevelopersPage.setOnClickListener(developerPageClickListener)
        binding.arrowAboutDevelopersPage.setOnClickListener(developerPageClickListener)
    }

    private fun loadProfileData() {
        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)

        val sharedUserid = sharedPref.getString("UID", "")
        val sharedUsername = sharedPref.getString("username", "")

        userID.text = sharedUserid
        username.text = sharedUsername

        val savedImageUri = sharedPref.getString("profileImageUri", null)
        if (savedImageUri != null) {
            try {
                userIcon.setImageURI(Uri.parse(savedImageUri))
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Failed to load saved profile image: ${e.message}")
                userIcon.setImageResource(R.drawable.icon_user)
            }
        } else {
            userIcon.setImageResource(R.drawable.icon_user)
        }
    }

    private fun navigateTo(destinationClass: Class<*>) {
        startActivity(Intent(this, destinationClass))
    }

    private fun showLogoutDialog() {
        LogoutDialog.show(this)
    }
}