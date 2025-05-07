package com.android.biyahe.activities

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.airbnb.lottie.LottieAnimationView
import com.android.biyahe.R
import com.android.biyahe.database.FirebaseManager
import com.android.biyahe.utils.NetworkUtil
import kotlinx.coroutines.*

class SplashActivity : Activity() {

    private var isVerified: Boolean = false
    private lateinit var animationView: LottieAnimationView
    private var animationEnded = false

    private val splashScope = CoroutineScope(Dispatchers.Main + Job())

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

        splashScope.launch {
            loadUserFromPreference()
        }

        animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                animationEnded = true
                splashScope.launch {
                    proceedAfterAnimation()
                }
            }
        })
    }

    override fun onDestroy() {
        splashScope.cancel()
        super.onDestroy()
    }

    private suspend fun proceedAfterAnimation() {
        if (!NetworkUtil.isOnline(this)) {
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

    private suspend fun loadUserFromPreference() {
        val sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val username = sharedPref.getString(KEY_USERNAME, null)
        val password = sharedPref.getString(KEY_PASSWORD, null)
        if (NetworkUtil.isOnline(this) && !username.isNullOrEmpty() && !password.isNullOrEmpty()) {
            firebaseLoad(username, password)
        }
    }

    private fun firebaseLoad(username: String, password: String) {
        FirebaseManager.verifyUser(username, password, this) { result ->
            isVerified = (result == 1)
            if (animationEnded) {
                splashScope.launch {
                    proceedAfterAnimation()
                }
            }
        }
    }

}