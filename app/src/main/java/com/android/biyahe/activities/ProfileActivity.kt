package com.android.biyahe.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.android.biyahe.R
import com.android.biyahe.helper.AccountAdapter
import com.android.biyahe.data.AccountsList
import com.android.biyahe.database.FirebaseManager
import com.android.biyahe.dialogs.OpenLinkActivity
import com.android.biyahe.utils.toast
import com.google.android.material.imageview.ShapeableImageView

class ProfileActivity : Activity() {

    private lateinit var UIDTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var shortDescTextView: TextView
    private lateinit var listViewLinkedAccounts: ListView
    private var accountAdapter: AccountAdapter? = null
    private lateinit var userIcon: ShapeableImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        UIDTextView = findViewById(R.id.UIDTextView)
        usernameTextView = findViewById(R.id.text_username)
        shortDescTextView = findViewById(R.id.text_shortDescription)
        listViewLinkedAccounts = findViewById(R.id.ListViewLinkedAccounts)
        userIcon = findViewById(R.id.CircleImageIcon)

        loadProfileDataAndSetupAccountsList()

        val buttonGoback = findViewById<ImageView>(R.id.viewProfile_goBack)
        buttonGoback.setOnClickListener {
            Log.e("ProfileActivity", "Go Back")
            finish()
        }

        val buttonEditprofile = findViewById<ImageView>(R.id.viewProfile_editProfile)
        buttonEditprofile.setOnClickListener {
            Log.e("ProfileActivity", "Goto Edit Profile")

            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE)
        }
    }

    private fun loadProfileDataAndSetupAccountsList() {
        val accounts = try {
            if (!FirebaseManager.isUserInitialized) throw IllegalStateException("User not initialized")
            UIDTextView.text = FirebaseManager.current_user.id
            usernameTextView.text = FirebaseManager.current_user.username
            shortDescTextView.text = FirebaseManager.current_user.shortDescription

            AccountsList.listOfAccounts.clear()
            AccountsList.listOfAccounts.addAll(FirebaseManager.current_user.linkedAccounts ?: mutableListOf())
            AccountsList.listOfAccounts
        } catch (e: Exception) {
            UIDTextView.text = ""
            usernameTextView.text = ""
            shortDescTextView.text = ""
            UIDTextView.isEnabled = false
            usernameTextView.isEnabled = false
            shortDescTextView.isEnabled = false
            listViewLinkedAccounts.isEnabled = false
            toast("User not loaded. Please login again.")
            Log.e("ProfileActivity", "current_user is not initialized", e)
            mutableListOf()
        }

        accountAdapter = AccountAdapter(
            this,
            accounts,
            onClick = { account ->
                val link = account.displayName
                OpenLinkActivity.show(this, link)
            },
            getIconResId = { iconType ->
                getIconResId(iconType)
            },
            onDelete = null
        )
        listViewLinkedAccounts.adapter = accountAdapter

        setListViewHeightBasedOnChildren(listViewLinkedAccounts)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == RESULT_OK) {
            loadProfileDataAndSetupAccountsList()
        }
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

    companion object {
        private const val REQUEST_CODE_EDIT_PROFILE = 1
    }
}