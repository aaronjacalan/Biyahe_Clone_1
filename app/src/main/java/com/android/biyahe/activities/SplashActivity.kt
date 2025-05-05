package com.android.biyahe.activities

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.WindowManager
import com.airbnb.lottie.LottieAnimationView
import com.android.biyahe.R
import com.android.biyahe.database.FirebaseManager

class SplashActivity : Activity() {

    private lateinit var animationView: LottieAnimationView

    companion object {
        private const val PREF_NAME = "ProfileData"
        private const val KEY_UID = "uid"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        animationView = findViewById(R.id.lottieView)
        animationView.playAnimation()

        animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                val sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                val uid = sharedPref.getString(KEY_UID, null)
                val hasInternet = isOnline(this@SplashActivity)

                if (hasInternet && !uid.isNullOrEmpty()) {
                    FirebaseManager.verifyUserByUid(uid, this@SplashActivity) { result ->
                        if (result == 1) {
                            startActivity(Intent(this@SplashActivity, NavigationActivity::class.java))
                        } else {
                            startActivity(Intent(this@SplashActivity, OpeningActivity::class.java))
                        }
                        finish()
                    }
                } else {
                    startActivity(Intent(this@SplashActivity, OpeningActivity::class.java))
                    finish()
                }
            }
        })
    }

    private fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}