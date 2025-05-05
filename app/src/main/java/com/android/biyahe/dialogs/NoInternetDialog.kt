package com.android.biyahe.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.app.Activity
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import com.android.biyahe.R

object NoInternetDialog {

    private var currentDialog: AlertDialog? = null

    @SuppressLint("SetTextI18n")
    fun show(
        context: Context,
        onGuest: () -> Unit,
        onTryAgain: () -> Unit
    ) {
        val activity = context as? Activity ?: return

        if (currentDialog?.isShowing == true) return

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_text, null)
        val alertDialog = AlertDialog.Builder(context).create()
        currentDialog = alertDialog
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val header = dialogView.findViewById<TextView>(R.id.tv_textHeader)
        val textMessage = dialogView.findViewById<TextView>(R.id.tv_textDesc)
        val yesButton = dialogView.findViewById<Button>(R.id.btnProceed)
        val noButton = dialogView.findViewById<Button>(R.id.btnCancel)

        noButton.visibility = View.GONE

        header.text = "Offline Connection"
        textMessage.text =
            "You're currently offline, which means some parts of the app may not function as intended. To unlock the complete experience, please connect to the internet. \n\nIf you prefer, you may continue using the app as a guest. However, be aware that certain functionalities may be restricted or unavailable until you're back online."
        yesButton.text = "LOGIN AS GUEST"

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
            it.setDimAmount(0.8f)
        }

        yesButton.setOnClickListener {
            alertDialog.dismiss()
            currentDialog = null
            onGuest()
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