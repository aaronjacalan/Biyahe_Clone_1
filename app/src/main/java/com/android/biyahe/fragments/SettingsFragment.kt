package com.android.biyahe.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.biyahe.R
import com.android.biyahe.activities.DeveloperActivity
import com.android.biyahe.activities.ProfileActivity
import com.android.biyahe.dialogs.LogoutDialog
import com.android.biyahe.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val TAG = "SettingsFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment using View Binding
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        val developerPageClickListener = View.OnClickListener {
            Log.d(TAG, "Developer page button clicked")
            navigateTo(DeveloperActivity::class.java)
        }

        binding.imageAboutDevelopersPage.setOnClickListener(developerPageClickListener)
        binding.textAboutDevelopersPage.setOnClickListener(developerPageClickListener)
        binding.arrowAboutDevelopersPage.setOnClickListener(developerPageClickListener)
    }

    private fun loadProfileData() {
        val sharedPref = requireContext().getSharedPreferences("ProfileData", Context.MODE_PRIVATE)

        val sharedUserid = sharedPref.getString("UID", "")
        val sharedUsername = sharedPref.getString("username", "")

        binding.UIDTextView.text = sharedUserid
        binding.textUsername.text = sharedUsername

        val savedImageUri = sharedPref.getString("profileImageUri", null)
        if (savedImageUri != null) {
            try {
                binding.CircleImageIcon.setImageURI(Uri.parse(savedImageUri))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load saved profile image: ${e.message}")
                binding.CircleImageIcon.setImageResource(R.drawable.icon_user)
            }
        } else {
            binding.CircleImageIcon.setImageResource(R.drawable.icon_user)
        }
    }

    private fun navigateTo(destinationClass: Class<*>) {
        val intent = Intent(requireContext(), destinationClass)
        startActivity(intent)
    }

    private fun showLogoutDialog() {
        LogoutDialog.show(requireActivity())
    }

}