package com.android.biyahe.activities

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.*
import com.android.biyahe.R
import com.android.biyahe.database.FirebaseManager
import com.android.biyahe.utils.isEmpty
import com.android.biyahe.utils.toast

class LoginActivity : Activity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var buttonLogin: Button
    private lateinit var cardLogin: FrameLayout
    private lateinit var progressBar : ProgressBar

    // Lazy loading for preferences and credentials
    private val sharedPref by lazy {
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    private val savedUsername by lazy {
        sharedPref.getString(KEY_USERNAME, "") ?: ""
    }

    private val savedPassword by lazy {
        sharedPref.getString(KEY_PASSWORD, "") ?: ""
    }

    companion object {
        private const val PREF_NAME = "ProfileData"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"

        private const val ERROR_EMPTY_USERNAME = "USERNAME CANNOT BE EMPTY"
        private const val ERROR_EMPTY_PASSWORD = "PASSWORD CANNOT BE EMPTY"
        private const val ERROR_WRONG_USERNAME = "USERNAME DOES NOT EXIST"
        private const val ERROR_WRONG_PASSWORD = "INCORRECT PASSWORD"

        private const val ANIMATION_DURATION = 500L // Animation duration in ms
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        initializeViews()
        setupEditTextListeners() // Use the improved error reset listeners
        setupClickListeners()
        animateCardLoginIn()
    }

    private fun initializeViews() {
        username = findViewById(R.id.et_enterUsername)
        password = findViewById(R.id.et_enterPassword)
        buttonLogin = findViewById(R.id.btn_loginToApp)
        cardLogin = findViewById(R.id.card_login)
        progressBar = findViewById(R.id.pb_login)

        // Initially set the card login to be slightly below and invisible for animation
        cardLogin.translationY = 100f
        cardLogin.alpha = 0f
    }

    private fun animateCardLoginIn() {
        ObjectAnimator.ofFloat(cardLogin, "translationY", 100f, 0f).apply {
            duration = ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
            start()
        }

        ObjectAnimator.ofFloat(cardLogin, "alpha", 0f, 1f).apply {
            duration = ANIMATION_DURATION - 75
            start()
        }
    }

    private fun animateCardLoginOut(onAnimationEnd: () -> Unit) {
        ObjectAnimator.ofFloat(cardLogin, "translationY", 0f, 100f).apply {
            duration = ANIMATION_DURATION
            interpolator = AccelerateInterpolator()
            start()
        }

        ObjectAnimator.ofFloat(cardLogin, "alpha", 1f, 0f).apply {
            duration = ANIMATION_DURATION - 75
            start()
            addUpdateListener { animator ->
                if (animator.animatedFraction >= 1.0f) {
                    onAnimationEnd()
                }
            }
        }
    }

    private fun setupEditTextListeners() {
        username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                setEditTextActivated(username, false)
            }
        })
        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                setEditTextActivated(password, false)
            }
        })
    }

    private fun setupClickListeners() {
        findViewById<TextView>(R.id.tv_gotoRegister).setOnClickListener {
            animateCardLoginOut {
                navigateTo(RegisterActivity::class.java, finishCurrent = true)
            }
        }

        val showPassImage = findViewById<ImageView>(R.id.iv_showPassword)
        val showPassText = findViewById<TextView>(R.id.tv_showPassword)
        var isPasswordVisible = false

        showPassImage.setOnClickListener {
            val tf = password.typeface

            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showPassImage.setImageResource(R.drawable.icon_hide)
                showPassText.text = "Hide"
            } else {
                password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showPassImage.setImageResource(R.drawable.icon_show)
                showPassText.text = "Show"
            }

            password.typeface = tf
            password.setSelection(password.text.length)
        }

        buttonLogin.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            buttonLogin.isEnabled = false
            if (!validateCredentialsAndShowError()) {
                progressBar.visibility = View.GONE
                buttonLogin.isEnabled = true
                return@setOnClickListener
            }

            // Firebase verification - kept intact
            FirebaseManager.verifyUser(username.text.toString(), password.text.toString(), this) {
                if(it == 1) {
                    toast("Welcome Back ${username.text}")
                    animateCardLoginOut {
                        navigateTo(NavigationActivity::class.java, finishCurrent = true)
                    }
                } else if(it == 2) {
                    toast(ERROR_WRONG_USERNAME)
                    setEditTextActivated(username, true)
                    shakeLoginButton()
                } else if(it == 3) {
                    toast(ERROR_WRONG_PASSWORD)
                    setEditTextActivated(password, true)
                    shakeLoginButton()
                }
                progressBar.visibility = View.GONE
                buttonLogin.isEnabled = true
            }
        }
    }

    private fun navigateTo(activityClass: Class<*>, finishCurrent: Boolean = false) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        overridePendingTransition(0, 0)
        if (finishCurrent) finish()
    }

    private fun validateCredentialsAndShowError(): Boolean {
        var isValid = true

        // Reset error state for all fields
        setEditTextActivated(username, false)
        setEditTextActivated(password, false)

        if (username.isEmpty()) {
            toast(ERROR_EMPTY_USERNAME)
            setEditTextActivated(username, true)
            isValid = false
        }

        if (password.isEmpty()) {
            toast(ERROR_EMPTY_PASSWORD)
            setEditTextActivated(password, true)
            isValid = false
        }

        if (!isValid) {
            shakeLoginButton()
        }

        return isValid
    }

    private fun setEditTextActivated(editText: EditText, activated: Boolean) {
        editText.isActivated = activated
        editText.refreshDrawableState()
    }

    private fun shakeLoginButton() {
        ObjectAnimator.ofFloat(buttonLogin, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f).apply {
            duration = 600
            start()
        }
    }

    override fun onBackPressed() {
        animateCardLoginOut {
            super.onBackPressed()
        }
    }
}