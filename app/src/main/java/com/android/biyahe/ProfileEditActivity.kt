package com.android.biyahe

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import com.android.biyahe.helper.AccountAdapter
import com.android.biyahe.data.AccountsList
import com.android.biyahe.utils.isEmpty
import com.android.biyahe.utils.toast

class ProfileEditActivity : Activity() {

    private lateinit var uid: EditText
    private lateinit var username: EditText
    private lateinit var shortDesc: EditText
    private lateinit var listViewLinkedAccountsEdit: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        uid = findViewById(R.id.UID_EditText)
        username = findViewById(R.id.UsernameEditText)
        shortDesc = findViewById(R.id.ShortDescriptionText)
        listViewLinkedAccountsEdit = findViewById(R.id.listViewLinkedAccountsEdit)

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

            if (uid.isEmpty() ||
                username.isEmpty() ||
                shortDesc.isEmpty()) {

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

        val addNewAccount = findViewById<Button>(R.id.editProfile_addAccountButton)
        addNewAccount.setOnClickListener {
            AddAccountDialog.show(this, object : AddAccountDialog.OnAccountAddedListener {
                override fun onAccountAdded(accountName: String, accountLink: String) {
                    addLinkedAccountToUser(accountName, accountLink)
                }
            })
        }

        val accounts = AccountsList.listOfAccounts
        val adapter = AccountAdapter(this, accounts, onClick = { accounts ->
            toast("${accounts.name} Was Clicked")
        })
        listViewLinkedAccountsEdit.adapter = adapter

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

    private fun addLinkedAccountToUser(accountName: String, accountLink: String) {

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