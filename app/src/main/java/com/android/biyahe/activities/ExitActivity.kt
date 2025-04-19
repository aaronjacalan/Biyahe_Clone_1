package com.android.biyahe.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.android.biyahe.R

object ExitDialog {
    fun show(context: Context) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_format, null)
        val alertDialog = AlertDialog.Builder(context).create()

        val image = dialogView.findViewById<ImageView>(R.id.imageViewFormat)
        val exitMessage = dialogView.findViewById<TextView>(R.id.tv_customTextFormat)
        val yesButton = dialogView.findViewById<Button>(R.id.btnYes)
        val noButton = dialogView.findViewById<Button>(R.id.btnNo)

        image.setImageResource(R.drawable.background_exit)
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