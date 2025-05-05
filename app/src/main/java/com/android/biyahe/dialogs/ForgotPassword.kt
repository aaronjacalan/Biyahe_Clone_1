package com.android.biyahe.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.app.Activity
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.Toast
import android.content.ClipData
import android.content.ClipboardManager
import android.view.View
import com.android.biyahe.R

object ForgotPassword {

    private var currentDialog: AlertDialog? = null

    @SuppressLint("SetTextI18n")
    fun show(context: Context) {
        val activity = context as? Activity ?: return
        if (currentDialog?.isShowing == true) return

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_text, null)
        val alertDialog = AlertDialog.Builder(context).create()
        currentDialog = alertDialog
        alertDialog.setView(dialogView)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val header = dialogView.findViewById<TextView>(R.id.tv_textHeader)
        val textMessage = dialogView.findViewById<TextView>(R.id.tv_textDesc)
        val yesButton = dialogView.findViewById<Button>(R.id.btnProceed)
        val noButton = dialogView.findViewById<Button>(R.id.btnCancel)

        noButton.visibility = View.GONE

        header.text = "Forgot Password"
        yesButton.text = "CONFIRM"

        val text = "If you've forgotten your password, please contact the support team via: aaronjacalan.business@gmail.com as this is currently the only method for account recovery. Please be advised that any fraudulent or prank requests may result in permanent account deletion."
        val email = "aaronjacalan.business@gmail.com"

        val spannable = SpannableString(text)
        val start = text.indexOf(email)
        val end = start + email.length
        if (start >= 0 && end <= text.length) {
            spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        textMessage.text = spannable

        if (start >= 0 && end <= text.length) {
            textMessage.setOnLongClickListener {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Email", email)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Email copied to clipboard", Toast.LENGTH_SHORT).show()
                true
            }
        }

        alertDialog.setCancelable(false)
        alertDialog.show()

        alertDialog.window?.let {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenWidth = displayMetrics.widthPixels
            val marginInDp = 30
            val density = context.resources.displayMetrics.density
            val marginInPixels = (marginInDp * density).toInt()
            val dialogWidth = screenWidth - (marginInPixels * 2)

            val layoutParams = it.attributes
            layoutParams.width = dialogWidth
            layoutParams.gravity = Gravity.CENTER
            it.attributes = layoutParams
            it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it.setDimAmount(0.8f)
        }

        yesButton.setOnClickListener {
            alertDialog.dismiss()
            currentDialog = null
        }

        alertDialog.setOnDismissListener {
            currentDialog = null
        }
    }

    fun dismiss() {
        currentDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
            currentDialog = null
        }
    }

}