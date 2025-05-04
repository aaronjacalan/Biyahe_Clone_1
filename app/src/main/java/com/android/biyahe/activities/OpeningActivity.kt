package com.android.biyahe.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
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

        gotoLoginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        gotoRegisterBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

    }

}