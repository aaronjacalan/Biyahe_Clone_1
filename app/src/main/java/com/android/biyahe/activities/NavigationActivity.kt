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
import com.android.biyahe.fragments.BookmarkFragment
import com.android.biyahe.fragments.LandingFragment
import com.android.biyahe.fragments.SettingsFragment
import com.android.biyahe.utils.toast

class NavigationActivity : AppCompatActivity() {

    private var onlineMode: Boolean = true
    private lateinit var binding: ActivityNavigationBinding
    private var currentFragmentId: Int = R.id.home // Use menu item IDs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFullScreenView()

        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onlineMode = intent.getBooleanExtra("online_mode", true)

        currentFragmentId = R.id.home
        switchFragment(LandingFragment())
        updateStatusBarColor(R.id.home)
        binding.bubbleTabBar.setSelectedWithId(R.id.home, false)

        updateTabBarForOnlineMode()

        // Listener for BubbleTabBar
        binding.bubbleTabBar.addBubbleListener { id ->
            if (!onlineMode && id != R.id.home) {
                toast("Offline mode: Only landing page is accessible.")
                binding.bubbleTabBar.setSelectedWithId(R.id.home, true)
                return@addBubbleListener
            }
            when (id) {
                R.id.home -> {
                    currentFragmentId = R.id.home
                    switchFragment(LandingFragment())
                    updateStatusBarColor(R.id.home)
                }
                R.id.bookmarks -> {
                    currentFragmentId = R.id.bookmarks
                    switchFragment(BookmarkFragment())
                    updateStatusBarColor(R.id.bookmarks)
                }
                R.id.settings -> {
                    currentFragmentId = R.id.settings
                    switchFragment(SettingsFragment())
                    updateStatusBarColor(R.id.settings)
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

    private fun updateStatusBarColor(fragmentId: Int) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun updateTabBarForOnlineMode() {
        for (i in 0 until binding.bubbleTabBar.childCount) {
            val tab = binding.bubbleTabBar.getChildAt(i)
            val tabId = tab.id

            if (!onlineMode && tabId != R.id.home) {
                tab.isEnabled = false
                tab.alpha = 0.5f // Dim the tab to indicate it's disabled
            } else {
                tab.isEnabled = true
                tab.alpha = 1.0f
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_depth_in,
                R.anim.slide_out_down
            )
            .replace(R.id.fl_fragment, fragment)
            .commit()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (currentFragmentId == R.id.home) {
            ExitDialog.show(this)
        } else {
            currentFragmentId = R.id.home
            switchFragment(LandingFragment())
            updateStatusBarColor(R.id.home)
            binding.bubbleTabBar.setSelectedWithId(R.id.home, true)
        }
    }

}