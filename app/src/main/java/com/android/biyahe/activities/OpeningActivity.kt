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
import com.android.biyahe.utils.NetworkUtil
import com.android.biyahe.dialogs.NoInternetDialog

class OpeningActivity : Activity() {

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

        val gotoLoginBtn = findViewById<Button>(R.id.btn_loginToApp)
        val gotoRegisterBtn = findViewById<Button>(R.id.btn_signupToApp)

        // Check connectivity and show dialog if needed
        checkConnectivityOrShowDialog()

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

    private fun checkConnectivityOrShowDialog() {
        if (!NetworkUtil.isOnline(this)) {
            NoInternetDialog.show(
                context = this,
                onGuest = {
                    val intent = Intent(this, NavigationActivity::class.java)
                    intent.putExtra("online_mode", false)
                    startActivity(intent)
                    finish()
                },
                onTryAgain = {
                    checkConnectivityOrShowDialog()
                }
            )
        }
    }
}