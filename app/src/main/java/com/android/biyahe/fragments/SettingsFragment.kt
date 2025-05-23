package com.android.biyahe.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
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

    companion object {
        var notificationsAreBeingSent: Boolean = true
        var routeNotificationsAreBeingSent: Boolean = true
        var trafficNotificationsAreBeingSent: Boolean = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNotificationSwitches()
        setupClickListeners()
        loadProfileData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun setupNotificationSwitches() {
        val masterSwitch = binding.switchNotificationsEnableNotif
        val routeSwitch = binding.switchNotificationsRouteNotif
        val trafficSwitch = binding.switchNotificationsTrafficAlerts

        val prefs = requireContext().getSharedPreferences("notification_settings", Context.MODE_PRIVATE)
        val masterEnabled = prefs.getBoolean("master_notifications_enabled", true)
        val routeEnabled = prefs.getBoolean("route_notifications_enabled", true)
        val trafficEnabled = prefs.getBoolean("traffic_notifications_enabled", true)

        masterSwitch.isChecked = masterEnabled
        routeSwitch.isChecked = routeEnabled
        trafficSwitch.isChecked = trafficEnabled

        setSubSwitchesEnabled(masterEnabled, routeSwitch, trafficSwitch)
        setSubSwitchesOpacity(masterEnabled, routeSwitch, trafficSwitch)

        notificationsAreBeingSent = prefs.getBoolean("notificationsAreBeingSent", true)
        routeNotificationsAreBeingSent = prefs.getBoolean("routeNotificationsAreBeingSent", true)
        trafficNotificationsAreBeingSent = prefs.getBoolean("trafficNotificationsAreBeingSent", true)

        masterSwitch.setOnCheckedChangeListener { _, isChecked ->
            setSubSwitchesEnabled(isChecked, routeSwitch, trafficSwitch)
            setSubSwitchesOpacity(isChecked, routeSwitch, trafficSwitch)
            prefs.edit().putBoolean("master_notifications_enabled", isChecked).apply()

            notificationsAreBeingSent = isChecked
            prefs.edit().putBoolean("notificationsAreBeingSent", notificationsAreBeingSent).apply()

            if (!isChecked) {
                routeNotificationsAreBeingSent = false
                prefs.edit().putBoolean("routeNotificationsAreBeingSent", false).apply()
                routeSwitch.isChecked = false

                trafficNotificationsAreBeingSent = false
                prefs.edit().putBoolean("trafficNotificationsAreBeingSent", false).apply()
                trafficSwitch.isChecked = false
            } else {
                routeNotificationsAreBeingSent = routeSwitch.isChecked
                prefs.edit().putBoolean("routeNotificationsAreBeingSent", routeNotificationsAreBeingSent).apply()
                trafficNotificationsAreBeingSent = trafficSwitch.isChecked
                prefs.edit().putBoolean("trafficNotificationsAreBeingSent", trafficNotificationsAreBeingSent).apply()
            }

            Log.d(TAG, "Notifications are now ${if (notificationsAreBeingSent) "ENABLED" else "DISABLED"} for master, route: $routeNotificationsAreBeingSent, traffic: $trafficNotificationsAreBeingSent")
        }

        routeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (masterSwitch.isChecked) {
                routeNotificationsAreBeingSent = isChecked
                prefs.edit().putBoolean("route_notifications_enabled", isChecked).apply()
                prefs.edit().putBoolean("routeNotificationsAreBeingSent", isChecked).apply()
                Log.d(TAG, "Route notifications are now ${if (routeNotificationsAreBeingSent) "ENABLED" else "DISABLED"}")
            } else {
                routeSwitch.isChecked = false
                routeNotificationsAreBeingSent = false
                prefs.edit().putBoolean("routeNotificationsAreBeingSent", false).apply()
            }
        }

        trafficSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (masterSwitch.isChecked) {
                trafficNotificationsAreBeingSent = isChecked
                prefs.edit().putBoolean("traffic_notifications_enabled", isChecked).apply()
                prefs.edit().putBoolean("trafficNotificationsAreBeingSent", isChecked).apply()
                Log.d(TAG, "Traffic notifications are now ${if (trafficNotificationsAreBeingSent) "ENABLED" else "DISABLED"}")
            } else {
                trafficSwitch.isChecked = false
                trafficNotificationsAreBeingSent = false
                prefs.edit().putBoolean("trafficNotificationsAreBeingSent", false).apply()
            }
        }
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun setSubSwitchesEnabled(enabled: Boolean, vararg switches: Switch) {
        switches.forEach { it.isEnabled = enabled }
    }

    private fun setSubSwitchesOpacity(enabled: Boolean, vararg switches: Switch) {
        val alphaValue = if (enabled) 1.0f else 0.4f
        switches.forEach { it.alpha = alphaValue }
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