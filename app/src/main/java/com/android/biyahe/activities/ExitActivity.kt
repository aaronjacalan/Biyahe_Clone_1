package com.android.biyahe.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.android.biyahe.R
import jp.wasabeef.blurry.Blurry
import android.app.Activity
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup

object ExitDialog {
    fun show(context: Context) {
        val activity = context as Activity
        val rootView = activity.window.decorView.findViewById<View>(android.R.id.content)
        val rootViewLocation = IntArray(2)
        rootView.getLocationInWindow(rootViewLocation)

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_format, null)
        val alertDialog = AlertDialog.Builder(context).create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val image = dialogView.findViewById<ImageView>(R.id.imageViewFormat)
        val textHeader = dialogView.findViewById<TextView>(R.id.tv_customTextFormat)
        val textMessage = dialogView.findViewById<TextView>(R.id.tv_descriptionText)
        val yesButton = dialogView.findViewById<Button>(R.id.btnYes)
        val noButton = dialogView.findViewById<Button>(R.id.btnNo)

        image.setImageResource(R.drawable.background_exit)
        textHeader.text = "EXIT"
        textMessage.text = "Are you sure you want to exit?"
        yesButton.text = "EXIT"
        noButton.text = "CANCEL"

        val components = arrayOf(
            image, textHeader, textMessage, yesButton, noButton
        )

        for (component in components) {
            component.visibility = View.VISIBLE
        }

        alertDialog.setView(dialogView)
        alertDialog.setCancelable(false)

        val blurContainer = ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                rootView.isDrawingCacheEnabled = true
                rootView.buildDrawingCache()
                val screenshot = Bitmap.createBitmap(rootView.drawingCache)
                rootView.isDrawingCacheEnabled = false

                Blurry.with(context)
                    .radius(20)
                    .sampling(4)
                    .async()
                    .from(screenshot)
                    .into(blurContainer)
            }
        })

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

            try {
                val decorView = it.decorView
                (decorView as ViewGroup).addView(blurContainer, 0)
            } catch (e: Exception) {
                it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                it.setDimAmount(0.7f)
            }
        }

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
    }

}