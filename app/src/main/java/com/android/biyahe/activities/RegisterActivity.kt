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
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.android.biyahe.R
import com.android.biyahe.database.FirebaseManager
import com.android.biyahe.utils.isEmpty
import com.android.biyahe.utils.isInvalidUID
import com.android.biyahe.utils.getPasswordValidationError
import com.android.biyahe.utils.isValidPassword
import com.android.biyahe.utils.toast

class RegisterActivity : Activity() {

    private lateinit var uid: EditText
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var confirmPass: EditText
    private lateinit var uidErrorText: TextView
    private lateinit var usernameErrorText: TextView
    private lateinit var passwordErrorText: TextView
    private lateinit var confirmPasswordErrorText: TextView
    private lateinit var buttonRegister: Button
    private lateinit var cardLogin: CardView
    private var backgroundId: Int = R.drawable.background_grainy1

    private val sharedPref by lazy {
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    companion object {
        private const val PREF_NAME = "ProfileData"
        private const val KEY_UID = "UID"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val EXTRA_BACKGROUND_ID = "BACKGROUND_ID"

        private const val ERROR_EMPTY_UID = "UID CANNOT BE EMPTY"
        private const val ERROR_INVALID_UID = "UID MUST NOT CONTAIN SPACES"
        private const val ERROR_EMPTY_USERNAME = "USERNAME CANNOT BE EMPTY"
        private const val ERROR_EMPTY_PASSWORD = "PASSWORD CANNOT BE EMPTY"
        private const val ERROR_PASSWORDS_MISMATCH = "PASSWORDS DO NOT MATCH"
        private const val ERROR_EMPTY_CONFIRM_PASSWORD = "CONFIRM PASSWORD IS REQUIRED"

        private const val ERROR_DISPLAY_DURATION = 1500L // 1.5 seconds
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
        uid = findViewById(R.id.et_enterUID)
        username = findViewById(R.id.et_enterUsername)
        password = findViewById(R.id.et_enterPassword)
        confirmPass = findViewById(R.id.et_confirmPassword)

        uidErrorText = findViewById(R.id.tv_uid_error)
        usernameErrorText = findViewById(R.id.tv_username_error)
        passwordErrorText = findViewById(R.id.tv_password_error)
        confirmPasswordErrorText = findViewById(R.id.tv_confirm_password_error)

        buttonRegister = findViewById(R.id.btn_register)
        cardLogin = findViewById(R.id.card_login)

        // Initially hide error messages
        uidErrorText.visibility = View.INVISIBLE
        usernameErrorText.visibility = View.INVISIBLE
        passwordErrorText.visibility = View.INVISIBLE
        confirmPasswordErrorText.visibility = View.INVISIBLE

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
                if (uid.text.isNotEmpty()) uidErrorText.visibility = View.INVISIBLE
                if (username.text.isNotEmpty()) usernameErrorText.visibility = View.INVISIBLE
                if (password.text.isNotEmpty()) passwordErrorText.visibility = View.INVISIBLE
                if (confirmPass.text.isNotEmpty()) confirmPasswordErrorText.visibility = View.INVISIBLE

                if (s === password.editableText || s === confirmPass.editableText) {
                    validatePasswordMatch()
                }
            }
        }

        uid.addTextChangedListener(textWatcher)
        username.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)
        confirmPass.addTextChangedListener(textWatcher)
    }

    private fun validatePasswordMatch() {
        if (!confirmPass.isEmpty() && password.text.toString() != confirmPass.text.toString()) {
            showError(confirmPasswordErrorText, ERROR_PASSWORDS_MISMATCH)
        }
    }

    private fun setupClickListeners() {
        findViewById<TextView>(R.id.tv_gotoLogin).setOnClickListener {
            animateCardLoginOut {
                navigateTo(LoginActivity::class.java, finishCurrent = true)
            }
        }

        buttonRegister.setOnClickListener {
            if (!validateCredentials()) {
                return@setOnClickListener
            }

            // Successful login
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

        findViewById<CardView>(R.id.btn_facebook).setOnClickListener {
            toast("Facebook login clicked")
            animateCardLoginOut {
                navigateTo(NavigationActivity::class.java, finishCurrent = true)
            }
        }

        findViewById<CardView>(R.id.btn_google).setOnClickListener {
            toast("Google login clicked")
            animateCardLoginOut {
                navigateTo(NavigationActivity::class.java, finishCurrent = true)
            }
        }

        findViewById<CardView>(R.id.btn_outlook).setOnClickListener {
            toast("Outlook login clicked")
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
        finish()
    }

    private fun validateCredentials(): Boolean {
        var isValid = true

        if (uid.isEmpty()) {
            showError(uidErrorText, ERROR_EMPTY_UID)
            isValid = false
        } else if (uid.isInvalidUID()) {
            showError(uidErrorText, ERROR_INVALID_UID)
            isValid = false
        }

        if (username.isEmpty()) {
            showError(usernameErrorText, ERROR_EMPTY_USERNAME)
            isValid = false
        }

        if (password.isEmpty()) {
            showError(passwordErrorText, ERROR_EMPTY_PASSWORD)
            isValid = false
        } else if (!password.isValidPassword()) {
            val errorMessage = password.getPasswordValidationError() ?: ERROR_EMPTY_PASSWORD
            showError(passwordErrorText, errorMessage)
            isValid = false
        }

        if (confirmPass.isEmpty()) {
            showError(confirmPasswordErrorText, ERROR_EMPTY_CONFIRM_PASSWORD)
            isValid = false
        } else if (password.text.toString() != confirmPass.text.toString()) {
            showError(confirmPasswordErrorText, ERROR_PASSWORDS_MISMATCH)
            isValid = false
        }

        if (!isValid) {
            shakeRegisterButton()
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

    private fun shakeRegisterButton() {
        ObjectAnimator.ofFloat(buttonRegister, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f).apply {
            duration = 600
            start()
        }
    }

    private fun saveUserCredentials() {
        val uidText = uid.text.toString()
        val usernameText = username.text.toString()
        val passwordText = password.text.toString()

        with(sharedPref.edit()) {
            putString(KEY_UID, uidText)
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