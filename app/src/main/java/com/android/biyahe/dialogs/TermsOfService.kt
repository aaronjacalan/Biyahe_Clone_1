package com.android.biyahe.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.android.biyahe.R

object TermsOfService {

    @SuppressLint("SetTextI18n")
    fun show(context: Context) {
        val activity = context as? Activity ?: return

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_terms_of_service, null)
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnProceed = dialogView.findViewById<Button>(R.id.btnProceed)
        val tvTextDesc = dialogView.findViewById<TextView>(R.id.tv_textDesc)
        tvTextDesc.text = Html.fromHtml(context.getString(R.string.terms_of_service_body), Html.FROM_HTML_MODE_LEGACY)
        tvTextDesc.movementMethod = LinkMovementMethod.getInstance()

        alertDialog.setView(dialogView)
        alertDialog.setCancelable(false)
        alertDialog.show()

        alertDialog.window?.let { window ->
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenWidth = displayMetrics.widthPixels

            val marginInDp = 15
            val density = context.resources.displayMetrics.density
            val marginInPixels = (marginInDp * density).toInt()
            val dialogWidth = screenWidth - (marginInPixels * 2)

            val layoutParams = window.attributes
            layoutParams.width = dialogWidth
            layoutParams.gravity = Gravity.CENTER
            window.attributes = layoutParams

            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window.setDimAmount(0.8f)
        }

        btnProceed.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}