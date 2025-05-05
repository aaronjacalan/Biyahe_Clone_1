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

    private fun ImageView.setSafeOnClickListener(onClick: (ImageView) -> Unit) {
        this.setOnClickListener(object : SafeClickListener() {
            override fun onSafeCLick() {
                onClick(this@setSafeOnClickListener)
            }
        })
    }

}