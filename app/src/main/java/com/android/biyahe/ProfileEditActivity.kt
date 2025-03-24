package com.android.biyahe

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.android.biyahe.utils.isInvalid
import com.android.biyahe.utils.toast

class ProfileEditActivity : Activity() {

    private lateinit var UID: EditText
    private lateinit var username: EditText
    private lateinit var shortDesc: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        UID = findViewById(R.id.UID_EditText)
        username = findViewById(R.id.UsernameEditText)
        shortDesc = findViewById(R.id.ShortDescriptionText)

        loadProfileData()

        val buttonCancelEdit = findViewById<Button>(R.id.editProfile_cancelButton)
        buttonCancelEdit.setOnClickListener {
            Log.i("ProfileEditActivity", "Cancel Edit Profile")

            AlertDialog.Builder(this)
                .setTitle("Cancel Changes")
                .setMessage("Are you sure you want to cancel editing?")
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }

        val buttonSaveChanges = findViewById<Button>(R.id.editProfile_saveButton)
        buttonSaveChanges.setOnClickListener {

            if (UID.isInvalid() ||
                username.isInvalid() ||
                shortDesc.isInvalid()) {

                toast("Please fill out all fields")
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Confirm Changes")
                    .setMessage("Do you want to proceed with these changes?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        saveUserChanges()
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }
    }

    private fun saveUserChanges() {
        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("UID", UID.text.toString())
            putString("username", username.text.toString())
            putString("shortDesc", shortDesc.text.toString())
            apply()
        }

        toast("Changes saved successfully")
        finish()
    }

    private fun loadProfileData() {
        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        UID.setText(sharedPref.getString("UID", ""))
        username.setText(sharedPref.getString("username", ""))
        shortDesc.setText(sharedPref.getString("shortDesc", ""))
    }
}