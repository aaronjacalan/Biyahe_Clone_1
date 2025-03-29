package com.android.biyahe

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

object AddAccountDialog {

    interface OnAccountAddedListener {
        fun onAccountAdded(accountName: String, accountLink: String)
    }

    fun show(context: Context, listener: OnAccountAddedListener) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_account, null)
        val alertDialog = AlertDialog.Builder(context).create()

        val accountNameInput = dialogView.findViewById<EditText>(R.id.et_enterAccountName)
        val accountLinkInput = dialogView.findViewById<EditText>(R.id.et_enterAccountLink)
        val cancelButton = dialogView.findViewById<Button>(R.id.btnNo)
        val addLinkButton = dialogView.findViewById<Button>(R.id.btnYes)

        addLinkButton.setOnClickListener {
            val accountName = accountNameInput.text.toString().trim()
            val accountLink = accountLinkInput.text.toString().trim()

            if (accountName.isEmpty()) {
                accountNameInput.error = "Please enter an account name"
                return@setOnClickListener
            }

            if (accountLink.isEmpty()) {
                accountLinkInput.error = "Please enter an account link"
                return@setOnClickListener
            }

            listener.onAccountAdded(accountName, accountLink)
            alertDialog.dismiss()
        }

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        // Set up and show the dialog
        alertDialog.setView(dialogView)
        alertDialog.setCancelable(false)
        alertDialog.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)

        alertDialog.show()
    }
}