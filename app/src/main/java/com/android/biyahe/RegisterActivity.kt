package com.android.biyahe

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
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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
    private lateinit var cardLogin: FrameLayout
    private var backgroundId: Int = R.drawable.background_grainy1

    companion object {
        private const val ANIMATION_DURATION = 500L // Animation duration in ms
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        backgroundId = intent.getIntExtra("BACKGROUND_ID", R.drawable.background_grainy1)

        initializeViews()
        setupBackground()
        setupTextWatchers()
        setupClickListeners()
        animateCardLoginIn()
    }

    private fun setupBackground() {
        val layout = findViewById<ConstraintLayout>(R.id.main)
        val backgroundImage = ContextCompat.getDrawable(this, backgroundId)
        layout.background = backgroundImage
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
        uid.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                when {
                    uid.isEmpty() -> {
                        uidErrorText.text = "UID IS REQUIRED"
                        uidErrorText.visibility = View.VISIBLE
                    }
                    uid.isInvalidUID() -> {
                        uidErrorText.text = "UID MUST NOT CONTAIN SPACES"
                        uidErrorText.visibility = View.VISIBLE
                    }
                    else -> {
                        uidErrorText.visibility = View.INVISIBLE
                    }
                }
                updateRegisterButtonState()
            }
        })

        // Username validation
        username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                when {
                    username.isEmpty() -> {
                        usernameErrorText.text = "USERNAME CANNOT BE EMPTY"
                        usernameErrorText.visibility = View.VISIBLE
                    }
                    else -> {
                        usernameErrorText.visibility = View.INVISIBLE
                    }
                }
                updateRegisterButtonState()
            }
        })

        // Password validation
        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val errorMessage = password.getPasswordValidationError()
                if (errorMessage != null) {
                    passwordErrorText.text = errorMessage
                    passwordErrorText.visibility = View.VISIBLE
                } else {
                    passwordErrorText.visibility = View.INVISIBLE
                }

                // Check password confirmation match whenever password changes
                validatePasswordMatch()
                updateRegisterButtonState()
            }
        })

        // Confirm password validation
        confirmPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validatePasswordMatch()
                updateRegisterButtonState()
            }
        })
    }

    private fun validatePasswordMatch() {
        if (!confirmPass.isEmpty() && password.text.toString() != confirmPass.text.toString()) {
            confirmPasswordErrorText.text = "PASSWORDS DO NOT MATCH"
            confirmPasswordErrorText.visibility = View.VISIBLE
        } else if (confirmPass.isEmpty()) {
            confirmPasswordErrorText.text = "CONFIRM PASSWORD IS REQUIRED"
            confirmPasswordErrorText.visibility = View.VISIBLE
        } else {
            confirmPasswordErrorText.visibility = View.INVISIBLE
        }
    }

    private fun updateRegisterButtonState() {
        // Enable button only when all fields are valid
        buttonRegister.isEnabled = uidErrorText.visibility == View.INVISIBLE &&
                passwordErrorText.visibility == View.INVISIBLE &&
                confirmPasswordErrorText.visibility == View.INVISIBLE &&
                !uid.isEmpty() && !username.isEmpty() && !password.isEmpty() && !confirmPass.isEmpty() &&
                password.isValidPassword() // Add check for valid password using our new validator
    }

    private fun setupClickListeners() {
        val gotoLogin = findViewById<TextView>(R.id.tv_gotoLogin)
        gotoLogin.setOnClickListener {
            toast("Going to Login")
            // Animate the card out before navigating
            animateCardLoginOut {
                // Pass the same background to the login activity
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("BACKGROUND_ID", backgroundId)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
        }

        buttonRegister.setOnClickListener {
            saveUserChanges(uid.text.toString(), username.text.toString())
            // Animate the card out before navigating
            animateCardLoginOut {
                // Pass the background ID to the navigation activity too
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra("BACKGROUND_ID", backgroundId)
                startActivity(intent)
                finish()
            }
        }

        val btnFacebook = findViewById<FrameLayout>(R.id.btn_facebook)
        btnFacebook.setOnClickListener {
            toast("FB is CLICKED")
            // Animate the card out before navigating
            animateCardLoginOut {
                // Pass the background ID to the navigation activity
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra("BACKGROUND_ID", backgroundId)
                startActivity(intent)
                finish()
            }
        }

        val btnGoogle = findViewById<FrameLayout>(R.id.btn_google)
        btnGoogle.setOnClickListener {
            toast("Google is CLICKED")
            // Animate the card out before navigating
            animateCardLoginOut {
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra("BACKGROUND_ID", backgroundId)
                startActivity(intent)
                finish()
            }
        }

        val btnOutlook = findViewById<FrameLayout>(R.id.btn_outlook)
        btnOutlook.setOnClickListener {
            toast("Outlook is CLICKED")
            // Animate the card out before navigating
            animateCardLoginOut {
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra("BACKGROUND_ID", backgroundId)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun saveUserChanges(uid: String, username: String) {
        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("UID", uid)
            putString("username", username)
            apply()
        }

        toast("User registered successfully")
        setResult(RESULT_OK)
    }

    override fun onBackPressed() {
        animateCardLoginOut {
            super.onBackPressed()
        }
    }
}