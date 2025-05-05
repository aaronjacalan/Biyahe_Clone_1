package com.android.biyahe.dialogs

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

    private fun guessIconType(link: String): String {
        val lower = link.lowercase()
        return when {
            "facebook" in lower -> "facebook"
            "google" in lower || "gmail" in lower -> "google"
            "outlook" in lower -> "outlook"
            "github" in lower -> "github"
            else -> "default"
        }
    }

    @SuppressLint("SetTextI18n")
    fun show(context: Context, onAccountAdded: () -> Unit = {}) {
        val activity = context as? Activity ?: throw IllegalArgumentException("Context must be an Activity")

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_account, null)
        val accountNameInput = dialogView.findViewById<EditText>(R.id.et_enterAccountName)
        val accountLinkInput = dialogView.findViewById<EditText>(R.id.et_enterAccountLink)
        val cancelButton = dialogView.findViewById<Button>(R.id.btnCancel)
        val addLinkButton = dialogView.findViewById<Button>(R.id.btnAddLink)

        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        addLinkButton.isEnabled = false
        setErrorState(accountNameInput, false)
        setErrorState(accountLinkInput, false)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val name = accountNameInput.text.toString().trim()
                val link = accountLinkInput.text.toString().trim()
                val nameIsValid = name.isNotEmpty()
                val linkIsValid = link.isNotEmpty() && Patterns.WEB_URL.matcher(link).matches()
                addLinkButton.isEnabled = nameIsValid && linkIsValid
                if (nameIsValid) setErrorState(accountNameInput, false)
                if (link.isNotEmpty()) setErrorState(accountLinkInput, false)
            }
        }

        accountNameInput.addTextChangedListener(textWatcher)
        accountLinkInput.addTextChangedListener(textWatcher)

        addLinkButton.setOnClickListener {
            val accountName = accountNameInput.text.toString().trim()
            val accountLink = accountLinkInput.text.toString().trim()

            val nameIsValid = accountName.isNotEmpty()
            val linkIsValid = accountLink.isNotEmpty() && Patterns.WEB_URL.matcher(accountLink).matches()

            setErrorState(accountNameInput, !nameIsValid)
            setErrorState(accountLinkInput, !linkIsValid)

            if (nameIsValid && linkIsValid) {
                val iconType = guessIconType(accountLink)
                AccountsList.addAccount(accountName, accountLink, iconType)
                alertDialog.dismiss()
                onAccountAdded()
            }
        }

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()

        alertDialog.window?.let { window ->
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenWidth = displayMetrics.widthPixels
            val marginInDp = 30
            val density = context.resources.displayMetrics.density
            val marginInPixels = (marginInDp * density).toInt()
            val dialogWidth = screenWidth - (marginInPixels * 2)
            window.setLayout(dialogWidth, WindowManager.LayoutParams.WRAP_CONTENT)
            val params = window.attributes
            params.gravity = Gravity.CENTER
            window.attributes = params

            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window.setDimAmount(0.8f)
        }
    }

    private fun setErrorState(editText: EditText, isError: Boolean) {
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