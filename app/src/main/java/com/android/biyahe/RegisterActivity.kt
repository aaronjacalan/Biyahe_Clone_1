package com.android.biyahe

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
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
    private lateinit var passwordErrorText: TextView
    private lateinit var confirmPasswordErrorText: TextView
    private lateinit var buttonRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initializeViews()
        setupTextWatchers()
        setupClickListeners()
    }

    private fun initializeViews() {
        uid = findViewById(R.id.et_enterUID)
        username = findViewById(R.id.et_enterUsername)
        password = findViewById(R.id.et_enterPassword)
        confirmPass = findViewById(R.id.et_confirmPassword)

        uidErrorText = findViewById(R.id.tv_uid_error)
        passwordErrorText = findViewById(R.id.tv_password_error)
        confirmPasswordErrorText = findViewById(R.id.tv_confirm_password_error)

        buttonRegister = findViewById(R.id.btn_register)
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
                // Username validation without error text
                updateRegisterButtonState()
            }
        })

        // Password validation
        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Use the new password validator
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
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        buttonRegister.setOnClickListener {
            saveUserChanges(uid.text.toString(), username.text.toString())
            startActivity(Intent(this, LandingActivity::class.java))
            finish()
        }

        val btnFacebook = findViewById<FrameLayout>(R.id.btn_facebook)
        btnFacebook.setOnClickListener {
            toast("FB is CLICKED")
            startActivity(Intent(this, LandingActivity::class.java))
            finish()
        }

        val btnGoogle = findViewById<FrameLayout>(R.id.btn_google)
        btnGoogle.setOnClickListener {
            toast("Google is CLICKED")
        }

        val btnOutlook = findViewById<FrameLayout>(R.id.btn_outlook)
        btnOutlook.setOnClickListener {
            toast("Outlook is CLICKED")
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
}