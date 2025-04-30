package com.android.biyahe.dialogs

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.android.biyahe.R
import android.app.Activity
import android.util.DisplayMetrics
import android.view.Gravity
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.android.biyahe.activities.LoginActivity
import com.android.biyahe.activities.OpeningActivity
import com.android.biyahe.data.RouteDataManager
import com.android.biyahe.database.FirebaseManager

object LogoutDialog {

    @SuppressLint("SetTextI18n")
    fun show(context: Context) {
        val activity = context as Activity

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_logout, null)
        val alertDialog = AlertDialog.Builder(context).create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val textHeader = dialogView.findViewById<TextView>(R.id.tv_customTextFormat)
        val textMessage = dialogView.findViewById<TextView>(R.id.tv_descriptionText)
        val yesButton = dialogView.findViewById<Button>(R.id.btnYes)
        val noButton = dialogView.findViewById<Button>(R.id.btnNo)
        val lottieView = dialogView.findViewById<LottieAnimationView>(R.id.lottieView)

        textHeader.text = "Logout"
        textMessage.text = "Are you sure you want to log out? Logging out will end your current session, require you to sign in again to access your account, and cause any unsaved changes to be lost."
        yesButton.text = "LOGOUT"
        noButton.text = "CANCEL"

        lottieView.repeatCount = 0
        lottieView.playAnimation()

        lottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                lottieView.pauseAnimation()
            }
        })

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
            it.setDimAmount(0.7f)
        }
        yesButton.setOnClickListener {
            context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()

            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)

            FirebaseManager.updateBookmark(RouteDataManager.bookmarked)
            FirebaseManager.remoteSaveUserInstance()
            RouteDataManager.bookmarked.clear()

            alertDialog.dismiss()
        }

        noButton.setOnClickListener {
            alertDialog.dismiss()
        }
    }

}