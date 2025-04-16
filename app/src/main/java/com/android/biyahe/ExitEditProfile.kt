package com.android.biyahe

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

object ExitEditProfile {
    fun show(context: Context) {
        val activity = context as? Activity

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_format, null)
        val alertDialog = AlertDialog.Builder(context).create()

        val image = dialogView.findViewById<ImageView>(R.id.imageViewFormat)
        val textMessage = dialogView.findViewById<TextView>(R.id.tv_customTextFormat)
        val yesButton = dialogView.findViewById<Button>(R.id.btnYes)
        val noButton = dialogView.findViewById<Button>(R.id.btnNo)

        image.visibility = View.GONE
        textMessage.text = "Do you want to cancel Editing?"
        yesButton.text = "LEAVE"
        noButton.text = "CANCEL"

        yesButton.setOnClickListener {
            alertDialog.dismiss()
            activity?.finish()
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