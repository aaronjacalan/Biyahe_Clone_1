package com.android.biyahe.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.graphics.Color
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.android.biyahe.R
import com.android.biyahe.helper.AccountAdapter
import com.android.biyahe.data.AccountsList
import com.android.biyahe.utils.toast

class ProfileActivity : Activity() {

    private lateinit var UIDTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var shortDescTextView: TextView
    private lateinit var listViewLinkedAccounts: ListView
    private lateinit var accountAdapter: AccountAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        UIDTextView = findViewById(R.id.UIDTextView)
        usernameTextView = findViewById(R.id.text_username)
        shortDescTextView = findViewById(R.id.text_shortDescription)
        listViewLinkedAccounts = findViewById(R.id.ListViewLinkedAccounts)
        loadProfileData()

        val imageView = findViewById<ImageView>(R.id.backgroundImage)
        imageView.setColorFilter(Color.argb(120, 0, 0, 0))

        val buttonGoback = findViewById<ImageView>(R.id.viewProfile_goBack)
        buttonGoback.setOnClickListener {
            Log.e("ProfileActivity", "Go Back")
            finish()
        }

        val buttonEditprofile = findViewById<Button>(R.id.tv_editProfile)
        buttonEditprofile.setOnClickListener {
            Log.e("ProfileActivity", "Goto Edit Profile")

            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE)
        }

        setupAccountsList()
    }

    private fun setupAccountsList() {
        val accounts = AccountsList.listOfAccounts
        accountAdapter = AccountAdapter(this, accounts, onClick = { account ->
            toast("${account.name} Was Clicked")
        })
        listViewLinkedAccounts.adapter = accountAdapter

        setListViewHeightBasedOnChildren(listViewLinkedAccounts)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == RESULT_OK) {
            loadProfileData()
            setupAccountsList()
        }
    }

    private fun loadProfileData() {
        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        UIDTextView.text = sharedPref.getString("UID", "")
        usernameTextView.text = sharedPref.getString("username", "")
        shortDescTextView.text = sharedPref.getString("shortDesc", "")
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

    companion object {
        private const val REQUEST_CODE_EDIT_PROFILE = 1
    }
}