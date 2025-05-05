package com.android.biyahe.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import com.android.biyahe.R
import com.android.biyahe.dialogs.OpenLinkActivity
import com.android.biyahe.utils.SafeClickListener
import com.google.android.material.snackbar.Snackbar

class DeveloperActivity : Activity() {

    companion object {
        private const val GALORIO_GITHUB_URL = "https://github.com/Shizune-23"
        private const val JACALAN_GITHUB_URL = "https://github.com/aaronjacalan"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)

        setupFullscreenMode()

        setupBackButton()
        setupGitHubLinks()
    }

    private fun setupFullscreenMode() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    private fun setupBackButton() {
        findViewById<ImageView>(R.id.developerPageGoBack).setSafeOnClickListener {
            Log.d("Developer Activity", "Navigating back to SettingActivity")
            finish()
        }
    }

    private fun setupGitHubLinks() {
        findViewById<ImageView>(R.id.developer1_github).setSafeOnClickListener {
            showOpenLinkDialog(GALORIO_GITHUB_URL)
        }

        findViewById<ImageView>(R.id.developer2_github).setSafeOnClickListener {
            showOpenLinkDialog(JACALAN_GITHUB_URL)
        }
    }

    private fun showOpenLinkDialog(url: String) {
        OpenLinkActivity.show(this, url)
    }

    // You can keep the original openGitHubProfile for fallback/error handling if needed.
    // It is now unused since OpenLinkActivity.show handles the navigation.
    /*
    private fun openGitHubProfile(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
            Log.d("Developer Activity", "Opening GitHub profile: $url")
        } catch (e: Exception) {
            Log.e("Developer Activity", "Failed to open URL: $url", e)
            Snackbar.make(
                findViewById(android.R.id.content),
                "Error Opening URL",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
    */

    private fun ImageView.setSafeOnClickListener(onClick: (ImageView) -> Unit) {
        this.setOnClickListener(object : SafeClickListener() {
            override fun onSafeCLick() {
                onClick(this@setSafeOnClickListener)
            }
        })
    }

}