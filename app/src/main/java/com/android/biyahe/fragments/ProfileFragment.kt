package com.android.biyahe.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.android.biyahe.activities.ProfileEditActivity
import com.android.biyahe.R
import com.android.biyahe.data.AccountsList
import com.android.biyahe.helper.AccountAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private lateinit var UIDTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var shortDescTextView: TextView
    private lateinit var listViewLinkedAccounts: ListView
    private lateinit var accountAdapter: AccountAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        UIDTextView = view.findViewById(R.id.UIDTextView)
        usernameTextView = view.findViewById(R.id.text_username)
        shortDescTextView = view.findViewById(R.id.text_shortDescription)
        listViewLinkedAccounts = view.findViewById(R.id.ListViewLinkedAccounts)
        loadProfileData()

        val imageView = view.findViewById<ImageView>(R.id.backgroundImage)
        imageView.setColorFilter(Color.argb(120, 0, 0, 0))


        val buttonEditprofile = view.findViewById<Button>(R.id.tv_editProfile)
        buttonEditprofile.setOnClickListener {
            Log.e("ProfileActivity", "Goto Edit Profile")

            val intent = Intent(requireContext(), ProfileEditActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE)
        }

        setupAccountsList()
    }

    private fun setupAccountsList() {
//        val accounts = AccountsList.listOfAccounts
//        accountAdapter = AccountAdapter(requireContext(), accounts, onClick = { account -> toast("${account.name} Was Clicked") })
//        listViewLinkedAccounts.adapter = accountAdapter
//        setListViewHeightBasedOnChildren(listViewLinkedAccounts)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == Activity.RESULT_OK) {
            loadProfileData()
            setupAccountsList()
        }
    }

    private fun loadProfileData() {
        val sharedPref = requireContext().getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
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