package com.android.biyahe.activities

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.android.biyahe.R
import com.android.biyahe.database.FirebaseManager
import com.android.biyahe.utils.isEmpty
import com.android.biyahe.utils.getPasswordValidationError
import com.android.biyahe.utils.isValidPassword
import com.android.biyahe.utils.toast

class RegisterActivity : Activity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var confirmPass: EditText
    private lateinit var buttonRegister: Button
    private lateinit var cardLogin: CardView
    private var backgroundId: Int = R.drawable.background_grainy1

    private val sharedPref by lazy {
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    companion object {
        private const val PREF_NAME = "ProfileData"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val EXTRA_BACKGROUND_ID = "BACKGROUND_ID"

        private const val ERROR_EMPTY_USERNAME = "USERNAME CANNOT BE EMPTY"
        private const val ERROR_EMPTY_PASSWORD = "PASSWORD CANNOT BE EMPTY"
        private const val ERROR_PASSWORDS_MISMATCH = "PASSWORDS DO NOT MATCH"
        private const val ERROR_EMPTY_CONFIRM_PASSWORD = "CONFIRM PASSWORD IS REQUIRED"

        private const val ANIMATION_DURATION = 500L // Animation duration in ms
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        backgroundId = intent.getIntExtra(EXTRA_BACKGROUND_ID, R.drawable.background_grainy1)

        initializeViews()
        setupBackground()
        setupTextWatchers()
        setupClickListeners()
        animateCardLoginIn()
    }

    private fun setupBackground() {
        findViewById<ConstraintLayout>(R.id.main).apply {
            background = ContextCompat.getDrawable(this@RegisterActivity, backgroundId)
        }
    }

    private fun initializeViews() {
        username = findViewById(R.id.et_enterUsername)
        password = findViewById(R.id.et_enterPassword)
        confirmPass = findViewById(R.id.et_confirmPassword)

        buttonRegister = findViewById(R.id.btn_register)
        cardLogin = findViewById(R.id.card_login)

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
                // Since error text views have been removed, we just need to validate password match
                // when either password field is changed
                if (s === password.editableText || s === confirmPass.editableText) {
                    validatePasswordMatch()
                }
            }
        }

        username.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)
        confirmPass.addTextChangedListener(textWatcher)
    }

    private fun validatePasswordMatch() {
        // No need to show error since error views have been removed
        // We'll rely on the validateCredentials method for final validation
    }

    private fun setupClickListeners() {
        findViewById<TextView>(R.id.tv_gotoLogin).setOnClickListener {
            animateCardLoginOut {
                navigateTo(LoginActivity::class.java, finishCurrent = true)
            }
        }

        buttonRegister.setOnClickListener {
            if (!validateCredentials()) {
                shakeRegisterButton()
                return@setOnClickListener
            }

            // Successful login - keeping Firebase code as is
            FirebaseManager.addUser(
                username.text.toString(),
                password.text.toString(),
                mutableListOf(),
                this
            ) {
                if(it) {
                    // saveUserCredentials()
                    toast("Registration Successful!")
                    animateCardLoginOut {
                        navigateTo(NavigationActivity::class.java, finishCurrent = true)
                    }
                }
            }
        }

        findViewById<ImageButton>(R.id.btn_google).setOnClickListener {
            toast("Google login clicked")
            animateCardLoginOut {
                navigateTo(NavigationActivity::class.java, finishCurrent = true)
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
            toast(ERROR_EMPTY_USERNAME)
            isValid = false
        }

        if (password.isEmpty()) {
            toast(ERROR_EMPTY_PASSWORD)
            isValid = false
        } else if (!password.isValidPassword()) {
            val errorMessage = password.getPasswordValidationError() ?: ERROR_EMPTY_PASSWORD
            toast(errorMessage)
            isValid = false
        }

        if (confirmPass.isEmpty()) {
            toast(ERROR_EMPTY_CONFIRM_PASSWORD)
            isValid = false
        } else if (password.text.toString() != confirmPass.text.toString()) {
            toast(ERROR_PASSWORDS_MISMATCH)
            isValid = false
        }

        return isValid
    }

    private fun shakeRegisterButton() {
        ObjectAnimator.ofFloat(buttonRegister, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f).apply {
            duration = 600
            start()
        }
    }

    private fun saveUserCredentials() {
        val usernameText = username.text.toString()
        val passwordText = password.text.toString()

        with(sharedPref.edit()) {
            putString(KEY_USERNAME, usernameText)
            putString(KEY_PASSWORD, passwordText)
            apply()
        }
    }

    override fun onBackPressed() {
        animateCardLoginOut {
            super.onBackPressed()
        }
    }
}