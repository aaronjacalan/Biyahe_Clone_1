package com.android.biyahe.activities

import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.airbnb.lottie.LottieAnimationView
import com.android.biyahe.R

class SplashActivity : Activity() {

    private lateinit var animationView: LottieAnimationView

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
            override fun onAnimationStart(animation: Animator) { }
            override fun onAnimationCancel(animation: Animator) { }
            override fun onAnimationRepeat(animation: Animator) { }
            override fun onAnimationEnd(animation: Animator) {
                startActivity(Intent(this@SplashActivity, OpeningActivity::class.java))
                finish()
            }
        })
    }

}