package com.android.biyahe.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.Html
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.android.biyahe.R

object OpenLinkActivity {

    @SuppressLint("SetTextI18n")
    fun show(context: Context, url: String) {
        val activity = context as? Activity ?: return

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_text, null)
        val alertDialog = AlertDialog.Builder(context).create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val header = dialogView.findViewById<TextView>(R.id.tv_textHeader)
        val textMessage = dialogView.findViewById<TextView>(R.id.tv_textDesc)
        val yesButton = dialogView.findViewById<Button>(R.id.btnProceed)
        val noButton = dialogView.findViewById<Button>(R.id.btnCancel)

        header.text = "Open External Link"
        textMessage.text = Html.fromHtml("You are about to leave the app and visit an external link: <u>" + url + "</u>. Do you want to continue?");
        yesButton.text = "YES"
        noButton.text = "NO"

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
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (e: Exception) {
            }
        }

        noButton.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}