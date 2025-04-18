package com.android.biyahe.activities

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.android.biyahe.R
import com.android.biyahe.utils.isEmpty
import com.android.biyahe.utils.toast

class LoginActivity : Activity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var usernameErrorText: TextView
    private lateinit var passwordErrorText: TextView
    private lateinit var buttonLogin: Button
    private lateinit var cardLogin: FrameLayout
    private var backgroundId: Int = R.drawable.background_grainy1

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
        private const val EXTRA_BACKGROUND_ID = "BACKGROUND_ID"

        private const val ERROR_EMPTY_USERNAME = "USERNAME CANNOT BE EMPTY"
        private const val ERROR_EMPTY_PASSWORD = "PASSWORD CANNOT BE EMPTY"
        private const val ERROR_WRONG_USERNAME = "USERNAME DOES NOT EXIST"
        private const val ERROR_WRONG_PASSWORD = "INCORRECT PASSWORD"

        private const val ERROR_DISPLAY_DURATION = 1500L // 1.5 seconds
        private const val ANIMATION_DURATION = 500L // Animation duration in ms
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        backgroundId = intent.getIntExtra(EXTRA_BACKGROUND_ID, R.drawable.background_grainy1)

        initializeViews()
        setupBackground()
        setupTextWatchers()
        setupClickListeners()
        animateCardLoginIn()
    }

    private fun setupBackground() {
        findViewById<ConstraintLayout>(R.id.main).apply {
            background = ContextCompat.getDrawable(this@LoginActivity, backgroundId)
        }
    }

    private fun initializeViews() {
        username = findViewById(R.id.et_enterUsername)
        password = findViewById(R.id.et_enterPassword)
        usernameErrorText = findViewById(R.id.tv_username_error)
        passwordErrorText = findViewById(R.id.tv_password_error)
        buttonLogin = findViewById(R.id.btn_loginToApp)
        cardLogin = findViewById(R.id.card_login)

        // Initially hide error messages
        usernameErrorText.visibility = View.INVISIBLE
        passwordErrorText.visibility = View.INVISIBLE

        // Initially set the card login to be slightly below and invisible for animation
        cardLogin.translationY = 100f
        cardLogin.alpha = 0f
    }

    private fun animateCardLoginIn() {
        // Animate translation (float up)
        ObjectAnimator.ofFloat(cardLogin, "translationY", 100f, 0f).apply {
            duration = ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
            start()
        }

        // Animate fade in
        ObjectAnimator.ofFloat(cardLogin, "alpha", 0f, 1f).apply {
            duration = ANIMATION_DURATION - 100
            start()
        }
    }

    private fun animateCardLoginOut(onAnimationEnd: () -> Unit) {
        // Animate translation (float down)
        ObjectAnimator.ofFloat(cardLogin, "translationY", 0f, 100f).apply {
            duration = ANIMATION_DURATION
            interpolator = AccelerateInterpolator()
            start()
        }

        // Animate fade out
        ObjectAnimator.ofFloat(cardLogin, "alpha", 1f, 0f).apply {
            duration = ANIMATION_DURATION - 100
            start()

            // When animation ends, execute the callback
            addUpdateListener { animator ->
                if (animator.animatedFraction >= 1.0f) {
                    onAnimationEnd()
                }
            }
        }
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Clear error messages when user starts typing
                if (username.text.isNotEmpty()) usernameErrorText.visibility = View.INVISIBLE
                if (password.text.isNotEmpty()) passwordErrorText.visibility = View.INVISIBLE
            }
        }

        username.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)
    }

    private fun setupClickListeners() {
        findViewById<TextView>(R.id.tv_gotoRegister).setOnClickListener {
            animateCardLoginOut {
                navigateTo(RegisterActivity::class.java, finishCurrent = true)
            }
        }

        buttonLogin.setOnClickListener {
            if (!validateCredentials()) {
                return@setOnClickListener
            }

            if (authenticateUser()) {
                toast("Welcome Back ${savedUsername}")
                animateCardLoginOut {
                    navigateTo(NavigationActivity::class.java, finishCurrent = true)
                }
            } else {
                if (savedUsername.isEmpty() || savedPassword.isEmpty()) {
                    toast("No saved credentials found. Please register first.")
                    return@setOnClickListener
                }

                if (username.text.toString() != savedUsername) {
                    showError(usernameErrorText, ERROR_WRONG_USERNAME)
                } else {
                    showError(passwordErrorText, ERROR_WRONG_PASSWORD)
                }

                shakeLoginButton()
            }
        }
    }

    private fun navigateTo(activityClass: Class<*>, finishCurrent: Boolean = false) {
        val intent = Intent(this, activityClass)
        intent.putExtra(EXTRA_BACKGROUND_ID, backgroundId)
        startActivity(intent)
        overridePendingTransition(0, 0)
        if (finishCurrent) finish()
    }

    private fun validateCredentials(): Boolean {
        var isValid = true

        if (username.isEmpty()) {
            showError(usernameErrorText, ERROR_EMPTY_USERNAME)
            isValid = false
        }

        if (password.isEmpty()) {
            showError(passwordErrorText, ERROR_EMPTY_PASSWORD)
            isValid = false
        }

        if (!isValid) {
            shakeLoginButton()
        }

        return isValid
    }

    private fun showError(errorTextView: TextView, errorMessage: String) {
        errorTextView.text = errorMessage
        errorTextView.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            errorTextView.visibility = View.INVISIBLE
        }, ERROR_DISPLAY_DURATION)
    }

    private fun shakeLoginButton() {
        ObjectAnimator.ofFloat(buttonLogin, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f).apply {
            duration = 600
            start()
        }
    }

    private fun authenticateUser(): Boolean {
        val enteredUsername = username.text.toString()
        val enteredPassword = password.text.toString()

        if (savedUsername.isEmpty() || savedPassword.isEmpty()) {
            return false
        }

        return enteredUsername == savedUsername && enteredPassword == savedPassword
    }

    override fun onBackPressed() {
        animateCardLoginOut {
            super.onBackPressed()
        }
    }
}