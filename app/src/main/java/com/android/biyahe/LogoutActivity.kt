package com.android.biyahe

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

object LogoutDialog {
    fun show(context: Context) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_logout, null)
        val alertDialog = AlertDialog.Builder(context).create()

        val textMessage = dialogView.findViewById<TextView>(R.id.tvLogoutMessage)
        val yesButton = dialogView.findViewById<Button>(R.id.btnYes)
        val noButton = dialogView.findViewById<Button>(R.id.btnNo)

        textMessage.text = "Are you sure you want to logout?"

        yesButton.setOnClickListener {
            context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()

            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)

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