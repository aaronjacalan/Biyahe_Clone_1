package com.android.biyahe.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.android.biyahe.R
import com.android.biyahe.data.Accounts

class AccountAdapter(private val context: Context,
                     private val accountList: List<Accounts>,
                     private val onClick: (Accounts) -> Unit
                    ): BaseAdapter() {
    override fun getCount(): Int = accountList.size
    override fun getItem(position: Int): Any = accountList[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.linked_accounts, parent, false)

        val accountIcon = view.findViewById<ImageView>(R.id.accountIcon)
        val accountName = view.findViewById<TextView>(R.id.accountName)
        val accountLink = view.findViewById<TextView>(R.id.accountLink)

        val account = accountList[position]

        accountIcon.setImageResource(account.iconResId)
        accountName.text = account.name
        accountLink.text = account.link

        view.setOnClickListener { onClick(account) }

        return view
    }


}