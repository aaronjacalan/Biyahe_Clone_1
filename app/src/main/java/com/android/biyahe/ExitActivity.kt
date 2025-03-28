package com.android.biyahe

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

object ExitDialog {
    fun show(context: Context) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_exit, null)
        val alertDialog = AlertDialog.Builder(context).create()

        val exitMessage = dialogView.findViewById<TextView>(R.id.tv_exitMessage)
        val yesButton = dialogView.findViewById<Button>(R.id.btnYes)
        val noButton = dialogView.findViewById<Button>(R.id.btnNo)

        exitMessage.text = "Do you want to exit the app?"

        yesButton.setOnClickListener {
            if (context is Activity) {
                context.finishAffinity()
            }
            alertDialog.dismiss()
        }

        noButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.setView(dialogView)
        alertDialog.setCancelable(false)
        alertDialog.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)

        alertDialog.show()
    }
}