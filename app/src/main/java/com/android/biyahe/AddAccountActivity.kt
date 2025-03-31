package com.android.biyahe

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
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

            AccountsList.addAccount(accountName, accountLink)
            alertDialog.dismiss()

            // Call the callback to notify that an account was added
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