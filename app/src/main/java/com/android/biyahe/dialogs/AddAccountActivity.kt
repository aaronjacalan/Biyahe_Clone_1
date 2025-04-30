package com.android.biyahe.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import com.android.biyahe.R
import com.android.biyahe.data.AccountsList

object AddAccountDialog {

    @SuppressLint("SetTextI18n")
    fun show(context: Context, onAccountAdded: () -> Unit = {}) {
        val activity = context as Activity

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_account, null)
        val alertDialog = AlertDialog.Builder(context).create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val accountNameInput = dialogView.findViewById<EditText>(R.id.et_enterAccountName)
        val accountLinkInput = dialogView.findViewById<EditText>(R.id.et_enterAccountLink)
        val cancelButton = dialogView.findViewById<Button>(R.id.btnCancel)
        val addLinkButton = dialogView.findViewById<Button>(R.id.btnAddLink)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s === accountNameInput.editableText) setErrorState(accountNameInput, false)
                else if (s === accountLinkInput.editableText) setErrorState(accountLinkInput, false)
            }
        }

        accountNameInput.addTextChangedListener(textWatcher)
        accountLinkInput.addTextChangedListener(textWatcher)

        setErrorState(accountNameInput, false)
        setErrorState(accountLinkInput, false)
        addLinkButton.isEnabled = false

        alertDialog.setView(dialogView)
        alertDialog.setCancelable(false)

        alertDialog.show()

        val window = alertDialog.window
        window?.let {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenWidth = displayMetrics.widthPixels

            val marginInDp = 30
            val density = context.resources.displayMetrics.density
            val marginInPixels = (marginInDp * density).toInt()

            val dialogWidth = screenWidth - (marginInPixels * 2)

            val layoutParams = WindowManager.LayoutParams().apply {
                copyFrom(it.attributes)
                width = dialogWidth
                gravity = Gravity.CENTER
            }

            it.attributes = layoutParams

            it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it.setDimAmount(0.7f)
        }

        addLinkButton.setOnClickListener {
            val accountName = accountNameInput.text.toString().trim()
            val accountLink = accountLinkInput.text.toString().trim()

            val nameIsValid = accountName.isNotEmpty()
            val linkIsValid = accountLink.isNotEmpty() && Patterns.WEB_URL.matcher(accountLink).matches()

            setErrorState(accountNameInput, !nameIsValid)
            setErrorState(accountLinkInput, !linkIsValid)

            if (nameIsValid && linkIsValid) {
                AccountsList.addAccount(accountName, accountLink)
                alertDialog.dismiss()
                onAccountAdded()
            }
        }

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun setErrorState(editText: EditText, isError: Boolean) {
        editText.isActivated = isError

        if (isError) {
            when (editText.id) {
                R.id.et_enterAccountName -> {
                    editText.error = "Account name cannot be empty"
                }
                R.id.et_enterAccountLink -> {
                    val link = editText.text.toString().trim()
                    if (link.isEmpty()) {
                        editText.error = "Account link cannot be empty"
                    } else if (!Patterns.WEB_URL.matcher(link).matches()) {
                        editText.error = "Please enter a valid URL"
                    }
                }
            }
        } else {
            editText.error = null
        }
    }

}