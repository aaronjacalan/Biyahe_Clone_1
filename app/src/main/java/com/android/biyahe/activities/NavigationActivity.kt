package com.android.biyahe.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.biyahe.R
import com.android.biyahe.databinding.ActivityNavigationBinding
import com.android.biyahe.fragments.BookmarkFragment
import com.android.biyahe.fragments.LandingFragment
import com.android.biyahe.fragments.ProfileFragment
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation

class NavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navBar.add(
            CurvedBottomNavigation.Model(1, "", R.drawable.icon_home)
        )
        binding.navBar.add(
            CurvedBottomNavigation.Model(2, "", R.drawable.icon_bookmark)
        )
        binding.navBar.add(
            CurvedBottomNavigation.Model(3, "", R.drawable.icon_profile)
        )

        binding.navBar.setOnClickMenuListener {
            when(it.id) {
                1 -> { switchFragment(LandingFragment()) }
                2 -> { switchFragment(BookmarkFragment()) }
                3 -> { switchFragment(ProfileFragment()) }
            }
        }

        switchFragment(LandingFragment())
        binding.navBar.show(1)

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
}