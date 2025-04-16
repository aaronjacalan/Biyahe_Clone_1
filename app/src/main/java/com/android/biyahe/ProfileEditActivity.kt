package com.android.biyahe

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.android.biyahe.helper.AccountAdapter
import com.android.biyahe.data.AccountsList
import com.android.biyahe.utils.isEmpty
import com.android.biyahe.utils.toast

class ProfileEditActivity : Activity() {

    private lateinit var uid: EditText
    private lateinit var username: EditText
    private lateinit var shortDesc: EditText
    private lateinit var listViewLinkedAccountsEdit: ListView
    private lateinit var accountAdapter: AccountAdapter
    private lateinit var usernameError: TextView
    private lateinit var shortDescriptionError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        uid = findViewById(R.id.UID_EditText)
        username = findViewById(R.id.UsernameEditText)
        shortDesc = findViewById(R.id.ShortDescriptionText)
        listViewLinkedAccountsEdit = findViewById(R.id.listViewLinkedAccountsEdit)
        usernameError = findViewById(R.id.tv_usernameError)
        shortDescriptionError = findViewById(R.id.tv_shortDescriptionError)

        usernameError.visibility = View.VISIBLE
        shortDescriptionError.visibility = View.VISIBLE

        resetErrorMessages()

        username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    usernameError.text = "USERNAME CANNOT BE EMPTY"
                    usernameError.visibility = View.VISIBLE
                } else if (s.length < 5) {
                    usernameError.text = "USERNAME MUST BE AT LEAST 5 CHARACTERS"
                    usernameError.visibility = View.VISIBLE
                } else {
                    usernameError.visibility = View.INVISIBLE
                }
            }
        })

        shortDesc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    shortDescriptionError.text = "DESCRIPTION CANNOT BE EMPTY"
                    shortDescriptionError.visibility = View.VISIBLE
                } else {
                    shortDescriptionError.visibility = View.INVISIBLE
                }
            }
        })

        loadProfileData()

        val buttonCancelEdit = findViewById<Button>(R.id.editProfile_cancelButton)
        buttonCancelEdit.setOnClickListener {
            Log.i("ProfileEditActivity", "Cancel Edit Profile")
            ExitEditProfile.show(this)

//            AlertDialog.Builder(this)
//                .setTitle("Cancel Changes")
//                .setMessage("Are you sure you want to cancel editing?")
//                .setPositiveButton("Yes") { dialog, _ ->
//                    dialog.dismiss()
//                    finish()
//                }
//                .setNegativeButton("No") { dialog, _ ->
//                    dialog.dismiss()
//                }
//                .create()
//                .show()
        }

        val buttonSaveChanges = findViewById<Button>(R.id.editProfile_saveButton)
        buttonSaveChanges.setOnClickListener {
            if (validateInputFields()) {
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

        val addNewAccount = findViewById<Button>(R.id.editProfile_addAccountButton)
        addNewAccount.setOnClickListener {
            AddAccountDialog.show(this) {
                refreshAccountsList()
            }
        }

        setupAccountsList()
    }

    private fun resetErrorMessages() {
        usernameError.text = ""
        shortDescriptionError.text = ""
    }

    private fun validateInputFields(): Boolean {
        var isValid = true

        if (username.isEmpty()) {
            usernameError.text = "USERNAME CANNOT BE EMPTY"
            isValid = false
        } else if (username.text.toString().length < 5) {
            usernameError.text = "USERNAME MUST BE AT LEAST 5 CHARACTERS"
            isValid = false
        }

        if (shortDesc.isEmpty()) {
            shortDescriptionError.text = "DESCRIPTION CANNOT BE EMPTY"
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
            putString("UID", uid.text.toString())
            putString("username", username.text.toString())
            putString("shortDesc", shortDesc.text.toString())
            apply()
        }

        toast("Changes saved successfully")
        setResult(RESULT_OK)
        finish()
    }

    private fun loadProfileData() {
        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        uid.setText(sharedPref.getString("UID", ""))
        username.setText(sharedPref.getString("username", ""))
        shortDesc.setText(sharedPref.getString("shortDesc", ""))
    }
}