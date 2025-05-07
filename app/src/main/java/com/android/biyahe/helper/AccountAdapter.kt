package com.android.biyahe.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.android.biyahe.R
import com.android.biyahe.data.Account
import com.android.biyahe.dialogs.DeleteLinkedAccount
import com.android.biyahe.activities.ProfileEditActivity

class AccountAdapter(
    private val context: Context,
    private val accountList: MutableList<Account>,
    private val onClick: (Account) -> Unit,
    private val getIconResId: (String) -> Int,
    private val onDelete: ((Account) -> Unit)?
) : BaseAdapter() {

    override fun getCount(): Int = accountList.size
    override fun getItem(position: Int): Any = accountList[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.linked_accounts, parent, false)

        val accountIcon = view.findViewById<ImageView>(R.id.accountIcon)
        val accountName = view.findViewById<TextView>(R.id.accountName)
        val accountLink = view.findViewById<TextView>(R.id.accountLink)

        val account = accountList[position]

        accountName.text = account.link
        accountLink.text = account.displayName
        accountIcon.setImageResource(getIconResId(account.iconType))

        view.setOnClickListener { onClick(account) }

        view.setOnLongClickListener {
            if (context is ProfileEditActivity && onDelete != null) {
                DeleteLinkedAccount.show(context, account.link) {
                    onDelete?.let { it1 -> it1(account) }
                }
                true
            } else {
                false
            }
        }

        return view
    }
}