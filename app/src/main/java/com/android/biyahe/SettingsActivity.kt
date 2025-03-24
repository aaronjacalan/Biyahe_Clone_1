package com.android.biyahe

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner

class SettingsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonEditProfile = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.gotoMyAccount)
        buttonEditProfile.setOnClickListener {
            Log.e("HELLO", "To Register Button is clicked")
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        val buttonLogout = findViewById<Button>(R.id.logoutProfileButton)
        buttonLogout.setOnClickListener {
            Log.e("HELLO", "To Register Button is clicked")
            showLogoutDialog()
        }

        val languageSpinner: Spinner = findViewById(R.id.language_spinner)
        LanguageSpinnerSetup.setupLanguageSpinner(
            context = this,
            spinner = languageSpinner
        ) { selectedLanguage ->
            when (selectedLanguage) {
                "English" -> handleEnglishSelection()
                "Cebuano" -> handleCebuanoSelection()
                "Tagalog" -> handleTagalogSelection()
            }
        }

    }

    private fun showLogoutDialog() {
        LogoutDialog.show(this)
    }

    private fun handleEnglishSelection() {
        // Handle English language selection
    }

    private fun handleCebuanoSelection() {
        // Handle Cebuano language selection
    }

    private fun handleTagalogSelection() {
        // Handle Tagalog language selection
    }

}

/**
 * LanguageSpinnerSetup provides a utility method to configure a language Spinner.
 */
object LanguageSpinnerSetup {
    /**
     * Sets up a language selection spinner.
     *
     * @param context The context from the activity/fragment
     * @param spinner The spinner view to be configured
     * @param onLanguageSelected Optional callback for language selection
     */
    fun setupLanguageSpinner(
        context: Context,
        spinner: Spinner,
        onLanguageSelected: ((String) -> Unit)? = null
    ) {
        // Ensure you have a languages array in res/values/strings.xml: <string-array name="languages">...</string-array>
        val languages = context.resources.getStringArray(R.array.languages)

        // Create and configure the adapter
        val adapter = ArrayAdapter(context, R.layout.spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set the adapter to the spinner
        spinner.adapter = adapter

        // Set up the selection listener
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedLanguage = languages[position]
                onLanguageSelected?.invoke(selectedLanguage)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Optional: Handle case when nothing is selected
            }
        }
    }
}