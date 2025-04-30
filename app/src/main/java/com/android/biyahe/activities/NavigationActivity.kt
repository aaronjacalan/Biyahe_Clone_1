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
import com.android.biyahe.fragments.ProfileFragment
import com.android.biyahe.fragments.SettingsFragment
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation

class NavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNavigationBinding
    private var currentFragmentId = 1 // Track the current fragment ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFullScreenView()

        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navBar.add(
            CurvedBottomNavigation.Model(1, "", R.drawable.icon_home)
        )
        binding.navBar.add(
            CurvedBottomNavigation.Model(2, "", R.drawable.icon_bookmark)
        )
        binding.navBar.add(
            CurvedBottomNavigation.Model(3, "", R.drawable.icon_settings)
        )

        binding.navBar.setOnClickMenuListener {
            when(it.id) {
                1 -> {
                    currentFragmentId = 1
                    switchFragment(LandingFragment())
                    updateStatusBarColor(1) // Update status bar for Fragment 1
                }
                2 -> {
                    currentFragmentId = 2
                    switchFragment(BookmarkFragment())
                    updateStatusBarColor(2) // Update status bar for Fragment 2
                }
                3 -> {
                    currentFragmentId = 3
                    switchFragment(SettingsFragment())
                    updateStatusBarColor(3) // Update status bar for Fragment 3
                }
            }
        }

        currentFragmentId = 1
        switchFragment(LandingFragment())
        updateStatusBarColor(1)
        binding.navBar.show(1)
    }

    private fun setFullScreenView() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    private fun updateStatusBarColor(fragmentId: Int) {
        when (fragmentId) {
            1 -> {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            else -> {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun switchFragment(fragment : Fragment) {
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
        if (currentFragmentId == 1) {
            ExitDialog.show(this)
        } else {
            currentFragmentId = 1
            switchFragment(LandingFragment())
            updateStatusBarColor(1)
            binding.navBar.show(1)
        }
    }

}