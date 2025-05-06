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
import androidx.cardview.widget.CardView
import com.android.biyahe.R
import com.android.biyahe.database.FirebaseManager
import com.android.biyahe.dialogs.TermsOfService
import com.android.biyahe.utils.isEmpty
import com.android.biyahe.utils.getPasswordValidationError
import com.android.biyahe.utils.toast

class RegisterActivity : Activity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var confirmPass: EditText
    private lateinit var buttonRegister: Button
    private lateinit var cardLogin: CardView
    private lateinit var progressBar: ProgressBar

    private val sharedPref by lazy {
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    companion object {
        const val PREF_NAME = "ProfileData"
        const val KEY_USERNAME = "username"
        const val KEY_PASSWORD = "password"
        private const val KEY_UID = "uid"

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

        initializeViews()
        setupEditTextListeners()
        setupClickListeners()
        animateCardLoginIn()
    }

    private fun initializeViews() {
        username = findViewById(R.id.et_enterUsername)
        password = findViewById(R.id.et_enterPassword)
        confirmPass = findViewById(R.id.et_confirmPassword)

        buttonRegister = findViewById(R.id.btn_register)
        cardLogin = findViewById(R.id.card_login)
        progressBar = findViewById(R.id.pb_register)

        cardLogin.translationY = 100f
        cardLogin.alpha = 0f
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
        confirmPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                setEditTextActivated(confirmPass, false)
            }
        })
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

    private fun setupClickListeners() {
        findViewById<TextView>(R.id.tv_gotoLogin).setOnClickListener {
            animateCardLoginOut {
                navigateTo(LoginActivity::class.java, finishCurrent = true)
            }
        }

        val openTermsOfService = findViewById<TextView>(R.id.tv_termsOfService)
        openTermsOfService.setOnClickListener {
            TermsOfService.show(this)
        }

        val showPassImage = findViewById<ImageView>(R.id.iv_showPassword)
        val showPassText = findViewById<TextView>(R.id.tv_showPassword)
        var isPasswordVisible = false

        showPassImage.setOnClickListener {
            val tf1 = password.typeface
            val tf2 = confirmPass.typeface

            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                confirmPass.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showPassImage.setImageResource(R.drawable.icon_hide)
                showPassText.text = "Hide"
            } else {
                password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                confirmPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showPassImage.setImageResource(R.drawable.icon_show)
                showPassText.text = "Show"
            }

            password.typeface = tf1
            confirmPass.typeface = tf2

            password.setSelection(password.text.length)
            confirmPass.setSelection(confirmPass.text.length)
        }

        buttonRegister.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            buttonRegister.isEnabled = false
            if (!validateCredentialsAndShowError()) {
                shakeRegisterButton()
                progressBar.visibility = View.INVISIBLE
                buttonRegister.isEnabled = true
                return@setOnClickListener
            }
            val inputUsername = username.text.toString()
            val inputPassword = password.text.toString()
            FirebaseManager.addUser(
                inputUsername,
                inputPassword,
                mutableListOf(),
                "",
                this
            ) { success ->
                if (success) {
                    saveUserCredentials(inputUsername)
                    toast("Registration Successful!")
                    animateCardLoginOut {
                        navigateTo(NavigationActivity::class.java, finishCurrent = true)
                    }
                }
                progressBar.visibility = View.INVISIBLE
                buttonRegister.isEnabled = true
            }
        }

    }

    private fun navigateTo(activityClass: Class<*>, finishCurrent: Boolean = false) {
        startActivity(Intent(this, activityClass))
        if (activityClass != NavigationActivity::class.java) overridePendingTransition(0, 0)
        if (finishCurrent) finish()
    }

    private fun validateCredentialsAndShowError(): Boolean {
        var valid = true

        setEditTextActivated(username, false)
        setEditTextActivated(password, false)
        setEditTextActivated(confirmPass, false)

        if (username.isEmpty()) {
            toast(ERROR_EMPTY_USERNAME)
            setEditTextActivated(username, true)
            valid = false
        }

        if (password.isEmpty()) {
            toast(ERROR_EMPTY_PASSWORD)
            setEditTextActivated(password, true)
            valid = false
        }

        password.getPasswordValidationError()?.let {
            toast(it)
            setEditTextActivated(password, true)
            valid = false
        }

        if (confirmPass.isEmpty()) {
            toast(ERROR_EMPTY_CONFIRM_PASSWORD)
            setEditTextActivated(confirmPass, true)
            valid = false
        }

        if (password.text.toString() != confirmPass.text.toString()) {
            toast(ERROR_PASSWORDS_MISMATCH)
            setEditTextActivated(password, true)
            setEditTextActivated(confirmPass, true)
            valid = false
        }

        return valid
    }

    private fun setEditTextActivated(editText: EditText, activated: Boolean) {
        editText.isActivated = activated
        editText.refreshDrawableState()
    }

    private fun shakeRegisterButton() {
        ObjectAnimator.ofFloat(buttonRegister, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f).apply {
            duration = 600
            start()
        }
    }

    private fun saveUserCredentials(uid: String) {
        val usernameText = username.text.toString()
        val passwordText = password.text.toString()
        with(sharedPref.edit()) {
            putString(KEY_USERNAME, usernameText)
            putString(KEY_PASSWORD, passwordText)
            putString(KEY_UID, uid)
            apply()
        }
    }

    override fun onBackPressed() {
        animateCardLoginOut {
            super.onBackPressed()
        }
    }
}