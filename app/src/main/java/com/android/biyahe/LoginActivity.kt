package com.android.biyahe

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.android.biyahe.utils.isEmpty
import com.android.biyahe.utils.toast

class LoginActivity : Activity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var usernameErrorText: TextView
    private lateinit var passwordErrorText: TextView
    private lateinit var buttonLogin: Button
    private lateinit var sharedPref: SharedPreferences
    private lateinit var savedUsername: String
    private var backgroundId: Int = R.drawable.background_grainy1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        backgroundId = intent.getIntExtra("BACKGROUND_ID", R.drawable.background_grainy1)

        initializeViews()
        setupBackground()
        setupTextWatchers()
        setupClickListeners()
    }

    private fun setupBackground() {
        val layout = findViewById<ConstraintLayout>(R.id.main)
        val backgroundImage = ContextCompat.getDrawable(this, backgroundId)
        layout.background = backgroundImage
    }

    private fun initializeViews() {
        username = findViewById(R.id.et_enterUsername)
        password = findViewById(R.id.et_enterPassword)
        usernameErrorText = findViewById(R.id.tv_username_error)
        passwordErrorText = findViewById(R.id.tv_password_error)
        buttonLogin = findViewById(R.id.btn_login)

        sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        savedUsername = sharedPref.getString("username", "").toString()
    }

    private fun setupTextWatchers() {
        username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                when {
                    username.isEmpty() -> {
                        usernameErrorText.text = "USERNAME CANNOT BE EMPTY"
                        usernameErrorText.visibility = View.VISIBLE
                    }
                    !username.text.toString().equals(savedUsername) && !savedUsername.isEmpty() -> {
                        usernameErrorText.text = "USERNAME DOES NOT EXIST"
                        usernameErrorText.visibility = View.VISIBLE
                    }
                    else -> {
                        usernameErrorText.visibility = View.INVISIBLE
                    }
                }
                updateLoginButtonState()
            }
        })

        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                when {
                    password.isEmpty() -> {
                        passwordErrorText.text = "PASSWORD CANNOT BE EMPTY"
                        passwordErrorText.visibility = View.VISIBLE
                    }
                    else -> {
                        passwordErrorText.visibility = View.INVISIBLE
                    }
                }
                updateLoginButtonState()
            }
        })
    }

    private fun updateLoginButtonState() {
        buttonLogin.isEnabled = usernameErrorText.visibility == View.INVISIBLE &&
                passwordErrorText.visibility == View.INVISIBLE &&
                !username.isEmpty() && !password.isEmpty()
    }

    private fun setupClickListeners() {
        val gotoRegister = findViewById<TextView>(R.id.tv_gotoRegister)
        gotoRegister.setOnClickListener {
            toast("Going to Register")
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("BACKGROUND_ID", backgroundId)
            startActivity(intent)
            finish()
        }

        buttonLogin.setOnClickListener {
            if (areInputsValid()) {
                // Pass the background ID to the navigation activity
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra("BACKGROUND_ID", backgroundId)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun areInputsValid(): Boolean {
        return usernameErrorText.visibility == View.INVISIBLE &&
                passwordErrorText.visibility == View.INVISIBLE &&
                !username.isEmpty() && !password.isEmpty()
    }
}