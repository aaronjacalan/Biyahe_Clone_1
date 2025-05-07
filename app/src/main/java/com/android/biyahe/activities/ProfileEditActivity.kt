package com.android.biyahe.activities

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.android.biyahe.R
import com.android.biyahe.data.Account
import com.android.biyahe.helper.AccountAdapter
import com.android.biyahe.data.AccountsList
import com.android.biyahe.database.FirebaseManager
import com.android.biyahe.dialogs.AddAccountDialog
import com.android.biyahe.dialogs.ExitEditProfile
import com.android.biyahe.utils.getPasswordValidationError
import com.android.biyahe.utils.isEmpty
import com.android.biyahe.utils.toast
import com.google.android.material.imageview.ShapeableImageView

class ProfileEditActivity : Activity() {

    private lateinit var UIDTextView: EditText
    private lateinit var usernameTextView: EditText
    private lateinit var shortDescTextView: EditText
    private lateinit var passwordTextView: EditText
    private lateinit var listViewLinkedAccountsEdit: ListView
    private var accountAdapter: AccountAdapter? = null
    private lateinit var userIcon: ShapeableImageView
    private lateinit var accounts: MutableList<Account>

    private var accountsAddedThisSession = 0
    private lateinit var originalAccountsSnapshot: MutableList<Account>
    private var originalUsername: String = ""
    private var originalPassword: String = ""
    private var originalShortDesc: String = ""

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
        passwordTextView = findViewById(R.id.et_enterPassword)
        listViewLinkedAccountsEdit = findViewById(R.id.listViewLinkedAccountsEdit)
        userIcon = findViewById(R.id.CircleImageIcon)

        usernameTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        setupShowHidePassword()
        loadProfileDataAndSetupAccountsList()

        originalAccountsSnapshot = AccountsList.listOfAccounts.map { it.copy() }.toMutableList()
        originalUsername = usernameTextView.text.toString()
        originalPassword = passwordTextView.text.toString()
        originalShortDesc = shortDescTextView.text.toString()

        val buttonCancelEdit = findViewById<ImageView>(R.id.editProfile_goBack)
        buttonCancelEdit.setOnClickListener {
            Log.i("ProfileEditActivity", "Cancel Edit Profile")
            handleCancelChanges()
        }

        val buttonSaveChanges = findViewById<ImageView>(R.id.editProfile_saveChanges)
        buttonSaveChanges.setOnClickListener {
            if (!hasChanges()) {
                finish()
                return@setOnClickListener
            }
            if (validateInputFields()) {
                saveUserChanges()
                accountsAddedThisSession = 0
                originalAccountsSnapshot = AccountsList.listOfAccounts.map { it.copy() }.toMutableList()
                originalUsername = usernameTextView.text.toString()
                originalPassword = passwordTextView.text.toString()
                originalShortDesc = shortDescTextView.text.toString()
            }
        }

        val addNewAccount = findViewById<Button>(R.id.editProfile_addAccountButton)
        addNewAccount.setOnClickListener {
            if (AccountsList.listOfAccounts.size < 8) {
                AddAccountDialog.show(this) {
                    accountsAddedThisSession++
                    refreshAccountsList()
                }
            }
        }
    }

    private fun hasChanges(): Boolean {
        val currentUsername = usernameTextView.text.toString()
        val currentPassword = passwordTextView.text.toString()
        val currentShortDesc = shortDescTextView.text.toString()

        if (currentUsername != originalUsername) return true
        if (currentPassword != originalPassword) return true
        if (currentShortDesc != originalShortDesc) return true

        if (originalAccountsSnapshot.size != AccountsList.listOfAccounts.size) return true
        for (i in originalAccountsSnapshot.indices) {
            if (originalAccountsSnapshot[i] != AccountsList.listOfAccounts[i]) {
                return true
            }
        }

        return false
    }

    private fun validateInputFields(): Boolean {
        var isValid = true

        setEditTextActivated(usernameTextView, false)
        setEditTextActivated(passwordTextView, false)

        if (usernameTextView.isEmpty()) {
            toast("USERNAME CANNOT BE EMPTY")
            setEditTextActivated(usernameTextView, true)
            isValid = false
        } else if (usernameTextView.text.toString().length < 5) {
            toast("USERNAME MUST BE AT LEAST 5 CHARACTERS")
            setEditTextActivated(usernameTextView, true)
            isValid = false
        }

        if (passwordTextView.isEmpty()) {
            toast("PASSWORD CANNOT BE EMPTY")
            setEditTextActivated(passwordTextView, true)
            isValid = false
        }

        passwordTextView.getPasswordValidationError()?.let {
            toast(it)
            setEditTextActivated(passwordTextView, true)
            isValid = false
        }

        return isValid
    }

    private fun setEditTextActivated(editText: EditText, activated: Boolean) {
        editText.isActivated = activated
        editText.refreshDrawableState()
    }

    private fun setupShowHidePassword() {
        val showPassImage = findViewById<ImageView>(R.id.iv_showPassword)
        val showPassText = findViewById<TextView>(R.id.tv_showPassword)
        var isPasswordVisible = false

        showPassImage.setOnClickListener {
            val tf = passwordTextView.typeface

            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordTextView.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showPassImage.setImageResource(R.drawable.icon_hide)
                showPassText.text = "Hide"
            } else {
                passwordTextView.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showPassImage.setImageResource(R.drawable.icon_show)
                showPassText.text = "Show"
            }

            passwordTextView.typeface = tf
            passwordTextView.setSelection(passwordTextView.text.length)
        }
    }

    private fun loadProfileDataAndSetupAccountsList() {
        accounts = try {
            if (!FirebaseManager.isUserInitialized) {
                throw IllegalStateException("User not initialized")
            }
            UIDTextView.setText(FirebaseManager.current_user.id)
            usernameTextView.setText(FirebaseManager.current_user.username)
            shortDescTextView.setText(FirebaseManager.current_user.shortDescription)
            passwordTextView.setText(FirebaseManager.current_user.password)

            AccountsList.listOfAccounts.clear()
            AccountsList.listOfAccounts.addAll(FirebaseManager.current_user.linkedAccounts ?: mutableListOf())
            AccountsList.listOfAccounts
        } catch (e: Exception) {
            UIDTextView.setText("")
            usernameTextView.setText("")
            shortDescTextView.setText("")
            passwordTextView.setText("")
            UIDTextView.isEnabled = false
            usernameTextView.isEnabled = false
            shortDescTextView.isEnabled = false
            passwordTextView.isEnabled = false
            listViewLinkedAccountsEdit.isEnabled = false
            toast("User not loaded. Please login again.")
            Log.e("ProfileEditActivity", "current_user is not initialized", e)
            mutableListOf()
        }

        accountAdapter = AccountAdapter(
            this,
            accounts,
            onClick = { },
            getIconResId = { iconType -> getIconResId(iconType) }
        ) { account ->
            accounts.remove(account)
            AccountsList.listOfAccounts.remove(account)
            refreshAccountsList()
        }
        listViewLinkedAccountsEdit.adapter = accountAdapter
        setListViewHeightBasedOnChildren(listViewLinkedAccountsEdit)
    }

    private fun refreshAccountsList() {
        accountAdapter?.notifyDataSetChanged()
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

    private fun getIconResId(iconType: String): Int = when (iconType) {
        "facebook" -> R.drawable.icon_facebook
        "google" -> R.drawable.icon_google
        "outlook" -> R.drawable.icon_outlook
        "github" -> R.drawable.icon_github
        else -> R.drawable.icon_link
    }

    private fun saveUserChanges() {
        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        val newUsername = usernameTextView.text.toString()
        val shortDesc = shortDescTextView.text.toString()
        val newPassword = passwordTextView.text.toString()

        with(sharedPref.edit()) {
            putString("username", newUsername)
            putString("password", newPassword)
            apply()
        }

        if (!FirebaseManager.isUserInitialized) {
            toast("User not loaded. Please login again.")
            Log.e("ProfileEditActivity", "Cannot save changes: current_user not initialized")
            return
        }

        FirebaseManager.saveUserProfileChanges(
            newUsername = newUsername,
            newPassword = newPassword,
            shortDescription = shortDesc,
            linkedAccounts = AccountsList.listOfAccounts
        ) { success ->
            if (success) {
                toast("Changes saved successfully")
                setResult(RESULT_OK)
                finish()
            } else {
                toast("Failed to save changes to server")
            }
        }
    }

    private fun handleCancelChanges() {
        if (accountsAddedThisSession > 0) {
            val currentList = AccountsList.listOfAccounts
            if (currentList.size >= accountsAddedThisSession) {
                repeat(accountsAddedThisSession) { currentList.removeAt(currentList.lastIndex) }
            }
            refreshAccountsList()
        }
        accountsAddedThisSession = 0
        ExitEditProfile.show(this)
    }

}