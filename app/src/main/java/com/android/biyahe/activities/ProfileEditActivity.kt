package com.android.biyahe.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.biyahe.R
import com.android.biyahe.helper.AccountAdapter
import com.android.biyahe.data.AccountsList
import com.android.biyahe.dialog.AddAccountDialog
import com.android.biyahe.dialogs.ExitEditProfile
import com.android.biyahe.utils.isEmpty
import com.android.biyahe.utils.toast
import com.google.android.material.imageview.ShapeableImageView

class ProfileEditActivity : Activity() {

    private lateinit var UIDTextView: EditText
    private lateinit var usernameTextView: EditText
    private lateinit var shortDescTextView: EditText
    private lateinit var listViewLinkedAccountsEdit: ListView
    private lateinit var accountAdapter: AccountAdapter
    private lateinit var userIcon: ShapeableImageView

    private val PICK_IMAGE_REQUEST = 1001
    private val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1002

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        UIDTextView = findViewById(R.id.et_enterUID)
        usernameTextView = findViewById(R.id.et_enterUsername)
        shortDescTextView = findViewById(R.id.et_enterShortDescription)
        listViewLinkedAccountsEdit = findViewById(R.id.listViewLinkedAccountsEdit)
        userIcon = findViewById(R.id.CircleImageIcon)

        usernameTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
            }
        })

        loadProfileData()

        userIcon.setOnClickListener {
            checkPermissionAndOpenGallery()
        }

        val buttonCancelEdit = findViewById<ImageView>(R.id.editProfile_goBack)
        buttonCancelEdit.setOnClickListener {
            Log.i("ProfileEditActivity", "Cancel Edit Profile")
            ExitEditProfile.show(this)
        }

        val buttonSaveChanges = findViewById<ImageView>(R.id.editProfile_saveChanges)
        buttonSaveChanges.setOnClickListener {
            if (validateInputFields()) {
                saveUserChanges()
            }
        }

        val addNewAccount = findViewById<Button>(R.id.editProfile_addAccountButton)
        addNewAccount.setOnClickListener {
            AddAccountDialog.show(this) {
                refreshAccountsList()
            }
        }

        setupAccountsList()
    }

    private fun checkPermissionAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            openGallery()
        } else {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                    openGallery()
                }
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE ) -> {
                    AlertDialog.Builder(this)
                        .setTitle("Permission Required")
                        .setMessage("Storage permission is required to select an image from gallery")
                        .setPositiveButton("Grant") { _, _ ->
                            requestStoragePermission()
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                            toast("Cannot access gallery without permission")
                        }
                        .create()
                        .show()
                }
                else -> {
                    requestStoragePermission()
                }
            }
        }
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_EXTERNAL_STORAGE_PERMISSION_CODE
        )
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            userIcon.setImageURI(selectedImageUri)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openGallery()
                } else {
                    toast("Permission denied. Cannot access gallery.")
                }
                return
            }
        }
    }

    private fun validateInputFields(): Boolean {
        var isValid = true

        if (usernameTextView.isEmpty()) {
            toast("USERNAME CANNOT BE EMPTY")
            isValid = false
        } else if (usernameTextView.text.toString().length < 5) {
            toast("USERNAME MUST BE AT LEAST 5 CHARACTERS")
            isValid = false
        }

        return isValid
    }

    private fun setupAccountsList() {
        val accounts = AccountsList.listOfAccounts
        accountAdapter = AccountAdapter(this, accounts, onClick = { account ->
            toast("${account.name} Was Clicked")
        })
        listViewLinkedAccountsEdit.adapter = accountAdapter

        setListViewHeightBasedOnChildren(listViewLinkedAccountsEdit)
    }

    private fun refreshAccountsList() {
        accountAdapter.notifyDataSetChanged()
        setListViewHeightBasedOnChildren(listViewLinkedAccountsEdit)
    }

    private fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter = listView.adapter ?: return

        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            totalHeight += listItem.measuredHeight
        }

        val params = listView.layoutParams
        params.height = totalHeight + (listView.dividerHeight * (listAdapter.count - 1))
        listView.layoutParams = params
        listView.requestLayout()
    }

    private fun saveUserChanges() {
        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("UID", UIDTextView.text.toString())
            putString("username", usernameTextView.text.toString())
            putString("shortDesc", shortDescTextView.text.toString())

            selectedImageUri?.let {
                putString("profileImageUri", it.toString())
            }

            apply()
        }
        toast("Changes saved successfully")
        setResult(RESULT_OK)
        finish()
    }

    private fun loadProfileData() {
        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)

        val uidValue = sharedPref.getString("UID", "") ?: ""
        val usernameValue = sharedPref.getString("username", "") ?: ""
        val shortDescValue = sharedPref.getString("shortDesc", "") ?: ""

        UIDTextView.setText(uidValue)
        usernameTextView.setText(usernameValue)
        shortDescTextView.setText(shortDescValue)

        val savedImageUri = sharedPref.getString("profileImageUri", null)
        if (!savedImageUri.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(savedImageUri)
                userIcon.setImageURI(uri)
                selectedImageUri = uri
            } catch (e: Exception) {
                Log.e("ProfileEditActivity", "Failed to load saved profile image: ${e.message}")
                userIcon.setImageResource(R.drawable.icon_user)
            }
        } else {
            userIcon.setImageResource(R.drawable.icon_user)
        }
    }

}