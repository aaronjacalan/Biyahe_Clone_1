package com.android.biyahe.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.android.biyahe.R

class OpeningActivity : Activity() {

    private var randomBackground: Int = 0
    private lateinit var lottieView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opening)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        lottieView = findViewById(R.id.lottieView)
        lottieView.apply {
            repeatCount = LottieDrawable.INFINITE
            repeatMode = LottieDrawable.RESTART
            playAnimation()
        }

        val headerText = findViewById<TextView>(R.id.tv_loginTitleHeader)
        val textLine1 = findViewById<TextView>(R.id.tv_loginDescription1)
        val textLine2 = findViewById<TextView>(R.id.tv_loginDescription2)
        val textLine3 = findViewById<TextView>(R.id.tv_loginDescription3)
        val textLine4 = findViewById<TextView>(R.id.tv_loginDescription4)

        val gotoLoginBtn = findViewById<Button>(R.id.btn_loginToApp)
        val gotoRegisterBtn = findViewById<Button>(R.id.btn_signupToApp)

        setupBackground()

        gotoLoginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("BACKGROUND_ID", randomBackground)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

        gotoRegisterBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("BACKGROUND_ID", randomBackground)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

    }

    private fun setupBackground() {
        val layout = findViewById<ConstraintLayout>(R.id.main)
        val backgrounds = listOf(
            R.drawable.background_grainy1,
            R.drawable.background_grainy2,
            R.drawable.background_grainy3,
            R.drawable.background_grainy4
        )
        randomBackground = backgrounds.random()
        val backgroundImage = ContextCompat.getDrawable(this, randomBackground)
        layout.background = backgroundImage
    }

}