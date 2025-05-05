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
import com.android.biyahe.R

object NoInternetDialog {

    @SuppressLint("SetTextI18n")
    fun show(
        context: Context,
        onGuest: () -> Unit,
        onTryAgain: () -> Unit
    ) {
        val activity = context as? Activity ?: return

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_text, null)
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val header = dialogView.findViewById<TextView>(R.id.tv_textHeader)
        val textMessage = dialogView.findViewById<TextView>(R.id.tv_textDesc)
        val yesButton = dialogView.findViewById<Button>(R.id.btnProceed)
        val noButton = dialogView.findViewById<Button>(R.id.btnCancel)

        header.text = "No Internet Connection"
        textMessage.text =
            "To access the full experience, please connect to the internet. Alternatively, you may continue as a guest, but note that some features will be limited or unavailable in this mode."
        yesButton.text = "LOGIN AS GUEST"
        noButton.text = "TRY AGAIN"

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
            onGuest()
        }

        noButton.setOnClickListener {
            alertDialog.dismiss()
            onTryAgain()
        }
    }
}