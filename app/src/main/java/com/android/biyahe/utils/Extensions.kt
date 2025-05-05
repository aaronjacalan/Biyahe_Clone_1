package com.android.biyahe.utils

import android.app.Activity
import android.widget.EditText
import android.widget.Toast

fun Activity.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun EditText.isEmpty(): Boolean = this.text.toString().isEmpty()

fun EditText.isInvalidUID(): Boolean {
    val uid = this.text.toString()
    return uid.isEmpty() || uid.contains(" ")
}

fun EditText.isValidPassword(): Boolean {
    val password = this.text.toString()
    return password.length >= 8 &&
            password.contains("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]+".toRegex()) &&
            !password.contains(" ")
}

fun EditText.getPasswordValidationError(): String? {
    val password = this.text.toString()
    return when {
        password.isEmpty() -> "PASSWORD IS REQUIRED"
        password.length < 8 -> "PASSWORD MUST BE AT LEAST 8 CHARACTERS"
        !password.contains("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]+".toRegex()) -> "PASSWORD MUST CONTAIN AT LEAST ONE SPECIAL CHARACTER"
        password.contains(" ") -> "PASSWORD MUST NOT CONTAIN SPACES"
        else -> null
    }
}