package com.android.biyahe

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.android.biyahe.data.AccountsList

object AddAccountDialog {

    @SuppressLint("SetTextI18n")
    fun show(context: Context, onAccountAdded: () -> Unit = {}) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_account, null)
        val alertDialog = AlertDialog.Builder(context).create()

        val accountNameInput = dialogView.findViewById<EditText>(R.id.et_enterAccountName)
        val accountLinkInput = dialogView.findViewById<EditText>(R.id.et_enterAccountLink)
        val cancelButton = dialogView.findViewById<Button>(R.id.btnNo)
        val addLinkButton = dialogView.findViewById<Button>(R.id.btnYes)

        val accountNameError = dialogView.findViewById<TextView>(R.id.tv_AccountName_error)
        val accountLinkError = dialogView.findViewById<TextView>(R.id.tv_AccountLink_error)

        var accountNameEdited = false
        var accountLinkEdited = false

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val accountName = accountNameInput.text.toString().trim()
                val accountLink = accountLinkInput.text.toString().trim()

                if (s === accountNameInput.editableText) {
                    accountNameEdited = true
                    if (accountNameEdited && accountName.isEmpty()) {
                        accountNameError.text = "Please enter an account name"
                        accountNameError.visibility = View.INVISIBLE
                    } else {
                        accountNameError.visibility = View.INVISIBLE
                    }
                } else if (s === accountLinkInput.editableText) {
                    accountLinkEdited = true
                    if (accountLinkEdited) {
                        if (accountLink.isEmpty()) {
                            accountLinkError.text = "Please enter an account link"
                            accountLinkError.visibility = View.VISIBLE
                        } else if (!Patterns.WEB_URL.matcher(accountLink).matches()) {
                            accountLinkError.text = "Please enter a valid URL"
                            accountLinkError.visibility = View.VISIBLE
                        } else {
                            accountLinkError.visibility = View.INVISIBLE
                        }
                    }
                }

                addLinkButton.isEnabled = accountName.isNotEmpty() &&
                        accountLink.isNotEmpty() &&
                        Patterns.WEB_URL.matcher(accountLink).matches()
            }
        }

        accountNameInput.addTextChangedListener(textWatcher)
        accountLinkInput.addTextChangedListener(textWatcher)

        accountNameError.visibility = View.INVISIBLE
        accountLinkError.visibility = View.INVISIBLE
        addLinkButton.isEnabled = false

        addLinkButton.setOnClickListener {
            val accountName = accountNameInput.text.toString().trim()
            val accountLink = accountLinkInput.text.toString().trim()

            if (accountName.isEmpty()) {
                accountNameError.text = "Please enter an account name"
                accountNameError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (accountLink.isEmpty()) {
                accountLinkError.text = "Please enter an account link"
                accountLinkError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (!Patterns.WEB_URL.matcher(accountLink).matches()) {
                accountLinkError.text = "Please enter a valid URL"
                accountLinkError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            AccountsList.addAccount(accountName, accountLink)
            alertDialog.dismiss()
            onAccountAdded()
        }

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.setView(dialogView)
        alertDialog.setCancelable(false)
        alertDialog.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)

        alertDialog.show()
    }
}