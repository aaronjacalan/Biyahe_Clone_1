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
import com.android.biyahe.database.FirebaseManager
import com.android.biyahe.dialogs.LogoutDialog
import com.android.biyahe.databinding.FragmentSettingsBinding
import com.android.biyahe.dialogs.TermsOfService

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val TAG = "SettingsFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment using View Binding
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        loadProfileData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

        val termsOfServiceListener = View.OnClickListener {
            Log.d(TAG, "Terms of Service button clicked")
            TermsOfService.show(requireActivity())
        }

        binding.imageAboutDevelopersPage.setOnClickListener(developerPageClickListener)
        binding.textAboutDevelopersPage.setOnClickListener(developerPageClickListener)
        binding.arrowAboutDevelopersPage.setOnClickListener(developerPageClickListener)

        binding.imageAboutTermsOfService.setOnClickListener(termsOfServiceListener)
        binding.textAboutTermsOfService.setOnClickListener(termsOfServiceListener)
        binding.arrowAboutTermsOfService.setOnClickListener(termsOfServiceListener)
    }

    private fun loadProfileData() {
        val sharedPref = requireContext().getSharedPreferences("ProfileData", Context.MODE_PRIVATE)

        binding.UIDTextView.text = FirebaseManager.current_user.id
        binding.textUsername.text = FirebaseManager.current_user.username

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