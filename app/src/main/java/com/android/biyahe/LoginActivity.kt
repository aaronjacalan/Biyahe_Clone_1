package com.android.biyahe

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.android.biyahe.databinding.ActivityLoginBinding
import com.android.biyahe.temp.NavigationActivity
import com.android.biyahe.utils.isEmpty
import com.android.biyahe.utils.toast

class LoginActivity : Activity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var password : String
    private lateinit var username : String
    private lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        setContentView(binding.root)

        binding.etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                toggleUsernameError("")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                togglePasswordError("")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnLogin.setOnClickListener {
            if(areInputsValid()) {
                Log.e("LoginActivity", "Login Button is clicked")
                toast("Welcome Back, $username")
                startActivity(Intent(this, NavigationActivity::class.java))
            }
        }

        // To be decided if functionalities are to be added
//        val btnGoogle = findViewById<ImageButton>(R.id.login_google)
//        btnGoogle.setOnClickListener {
//
//        }
//
//        val btnOutlook = findViewById<ImageButton>(R.id.login_outlook)
//        btnOutlook.setOnClickListener {
//
//        }
//
//        val btnFacebook= findViewById<ImageButton>(R.id.login_facebook)
//        btnFacebook.setOnClickListener {
//
//        }

        val gotoRegister = findViewById<TextView>(R.id.tv_gotoRegister)
        gotoRegister.setOnClickListener {
            toast("Going to Register")
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun initialize() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        username = sharedPref.getString("username", "").toString()

        binding.etUsername.setText(username)
    }

    private fun toggleUsernameError(message : String) {
        if(message.equals("")) {
            binding.tvUsernameError.visibility = View.INVISIBLE
        } else {
            binding.tvUsernameError.text = message
            binding.tvUsernameError.visibility = View.VISIBLE
        }
    }

    private fun togglePasswordError(message : String) {
        if(message.equals("")) {
            binding.tvPasswordError.visibility = View.INVISIBLE
        } else {
            binding.tvPasswordError.text = message
            binding.tvPasswordError.visibility = View.VISIBLE
        }
    }

    private fun areInputsValid() : Boolean {
        var isValid = true

        val currentUsername = binding.etUsername.text.toString()
        val currentPassword = binding.etPassword.text.toString()
        if(currentUsername.isEmpty()) {
            toggleUsernameError("Username cannot be empty!")
            isValid = false
        }
        if(currentPassword.isEmpty()) {
            togglePasswordError("Password cannot be empty!")
            isValid = false
        }
        if(!currentUsername.equals(username)) {
            toggleUsernameError("Username does not exist!")
            isValid = false
        }
        // To add Password Validation

        return isValid
    }

}