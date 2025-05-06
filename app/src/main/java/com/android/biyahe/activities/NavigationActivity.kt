package com.android.biyahe.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.biyahe.R
import com.android.biyahe.databinding.ActivityNavigationBinding
import com.android.biyahe.dialogs.ExitDialog
import com.android.biyahe.dialogs.OfflineMode
import com.android.biyahe.fragments.BookmarkFragment
import com.android.biyahe.fragments.LandingFragment
import com.android.biyahe.fragments.SettingsFragment
import com.android.biyahe.utils.toast

class NavigationActivity : AppCompatActivity() {

    private var onlineMode: Boolean = true
    private lateinit var binding: ActivityNavigationBinding
    private var currentFragmentId: Int = R.id.home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFullScreenView()

        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onlineMode = intent.getBooleanExtra("online_mode", true)
        if (!onlineMode) OfflineMode.show(this)

        currentFragmentId = R.id.home
        switchFragment(LandingFragment())
        binding.bubbleTabBar.setSelectedWithId(R.id.home, false)

        updateTabBarForOnlineMode()

        if (!onlineMode) {
            binding.bubbleTabBar.isEnabled = false
            for (i in 0 until binding.bubbleTabBar.childCount) {
                val tab = binding.bubbleTabBar.getChildAt(i)
                if (tab.id != R.id.home) {
                    tab.isEnabled = false
                    tab.alpha = 0.5f
                }
            }
        } else {
            binding.bubbleTabBar.addBubbleListener { id ->
                when (id) {
                    R.id.home -> {
                        currentFragmentId = R.id.home
                        switchFragment(LandingFragment())
                    }
                    R.id.bookmarks -> {
                        currentFragmentId = R.id.bookmarks
                        switchFragment(BookmarkFragment())
                    }
                    R.id.settings -> {
                        currentFragmentId = R.id.settings
                        switchFragment(SettingsFragment())
                    }
                }
            }
        }
    }

    private fun setFullScreenView() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    private fun updateTabBarForOnlineMode() {
        for (i in 0 until binding.bubbleTabBar.childCount) {
            val tab = binding.bubbleTabBar.getChildAt(i)
            val tabId = tab.id

            if (!onlineMode && tabId != R.id.home) {
                tab.isEnabled = false
                tab.alpha = 0.5f
            } else {
                tab.isEnabled = true
                tab.alpha = 1.0f
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun switchFragment(fragment: Fragment) {
        if (fragment is LandingFragment) {
            val args = Bundle()
            args.putBoolean("online_mode", onlineMode)
            fragment.arguments = args
        }
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_depth_in,
                R.anim.slide_out_down
            )
            .replace(R.id.fl_fragment, fragment)
            .commitAllowingStateLoss()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (currentFragmentId == R.id.home) {
            ExitDialog.show(this)
        } else {
            currentFragmentId = R.id.home
            switchFragment(LandingFragment())
            binding.bubbleTabBar.setSelectedWithId(R.id.home, true)
        }
    }
}