package com.android.biyahe.activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.android.biyahe.R
import com.android.biyahe.database.FirebaseManager
import com.android.biyahe.dialogs.NoInternetDialog

class OpeningActivity : Activity() {

    private lateinit var lottieView: LottieAnimationView
    private var connectivityReceiver: BroadcastReceiver? = null

    companion object {
        private const val PREF_NAME = "ProfileData"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opening)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        lottieView = findViewById(R.id.lottieView)
        lottieView.apply {
            repeatCount = LottieDrawable.INFINITE
            repeatMode = LottieDrawable.RESTART
            playAnimation()
        }

        val gotoLoginBtn = findViewById<Button>(R.id.btn_loginToApp)
        val gotoRegisterBtn = findViewById<Button>(R.id.btn_signupToApp)

        checkConnectivityOrShowDialog()
        registerConnectivityReceiver()

        gotoLoginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        gotoRegisterBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
    }

    private fun loadUserIfPossible() {
        if (!isOnline(this)) return
        val sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val username = sharedPref.getString(KEY_USERNAME, null)
        val password = sharedPref.getString(KEY_PASSWORD, null)
        if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
            firebaseLoad(username, password)
        }
    }

    private fun firebaseLoad(username: String, password: String) {
        if (!isOnline(this)) {
            NoInternetDialog.show(
                context = this,
                onGuest = {
                    val intent = Intent(this, NavigationActivity::class.java)
                    intent.putExtra("online_mode", false)
                    startActivity(intent)
                    finish()
                },
                onTryAgain = {
                    checkConnectivityOrShowDialog()
                }
            )
            return
        }
        FirebaseManager.verifyUser(username, password, this) { result ->
            if (!isOnline(this)) {
                NoInternetDialog.show(
                    context = this,
                    onGuest = {
                        val intent = Intent(this, NavigationActivity::class.java)
                        intent.putExtra("online_mode", false)
                        startActivity(intent)
                        finish()
                    },
                    onTryAgain = {
                        checkConnectivityOrShowDialog()
                    }
                )
                return@verifyUser
            }
            if (result == 1) {
                try {
                    NoInternetDialog.dismiss()
                } catch (_: Exception) {}
                startActivity(Intent(this, NavigationActivity::class.java))
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterConnectivityReceiver()
    }

    private fun registerConnectivityReceiver() {
        if (connectivityReceiver == null) {
            connectivityReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val connected = isOnline(context)
                    if (connected) {
                        loadUserIfPossible()
                        try {
                            NoInternetDialog.dismiss()
                        } catch (_: Exception) {}
                    } else {
                        NoInternetDialog.show(
                            context = this@OpeningActivity,
                            onGuest = {
                                val intent = Intent(this@OpeningActivity, NavigationActivity::class.java)
                                intent.putExtra("online_mode", false)
                                startActivity(intent)
                                finish()
                            },
                            onTryAgain = {
                                checkConnectivityOrShowDialog()
                            }
                        )
                    }
                }
            }
            val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            registerReceiver(connectivityReceiver, filter)
        }
    }

    private fun unregisterConnectivityReceiver() {
        connectivityReceiver?.let {
            unregisterReceiver(it)
            connectivityReceiver = null
        }
    }

    private fun checkConnectivityOrShowDialog() {
        if (!isOnline(this)) {
            NoInternetDialog.show(
                context = this,
                onGuest = {
                    val intent = Intent(this, NavigationActivity::class.java)
                    intent.putExtra("online_mode", false)
                    startActivity(intent)
                    finish()
                },
                onTryAgain = {
                    checkConnectivityOrShowDialog()
                }
            )
        } else {
            try {
                NoInternetDialog.dismiss()
            } catch (_: Exception) {}
            loadUserIfPossible()
        }
    }

    private fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo: NetworkInfo? = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}