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

    private var isVerified: Boolean = false
    private lateinit var animationView: LottieAnimationView
    private var animationEnded = false

    companion object {
        private const val PREF_NAME = "ProfileData"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        animationView = findViewById(R.id.lottieView)
        animationView.setAnimation(R.raw.startup_anim)
        animationView.progress = 0f
        animationView.repeatCount = 0
        animationView.playAnimation()

        animationView.post {
            animationView.invalidate()
            animationView.playAnimation()
        }

        loadUserFromPreference()

        animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                animationEnded = true
                proceedAfterAnimation()
            }
        })
    }

    private fun proceedAfterAnimation() {
        if (!isNetworkAvailable()) {
            startActivity(Intent(this, OpeningActivity::class.java))
            finish()
        } else {
            if (isVerified) {
                startActivity(Intent(this, NavigationActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, OpeningActivity::class.java))
                finish()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun loadUserFromPreference() {
        val sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val username = sharedPref.getString(KEY_USERNAME, null)
        val password = sharedPref.getString(KEY_PASSWORD, null)
        if (isNetworkAvailable() && !username.isNullOrEmpty() && !password.isNullOrEmpty()) {
            firebaseLoad(username, password)
        }
    }

    private fun firebaseLoad(username: String, password: String) {
        FirebaseManager.verifyUser(username, password, this) { result ->
            isVerified = (result == 1)
            if (animationEnded) proceedAfterAnimation()
        }
    }

}