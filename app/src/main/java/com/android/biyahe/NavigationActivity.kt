package com.android.biyahe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
            CurvedBottomNavigation.Model(2, "", R.drawable.icon_route)
        )
        binding.navBar.add(
            CurvedBottomNavigation.Model(3, "", R.drawable.icon_bookmark)
        )
        binding.navBar.add(
            CurvedBottomNavigation.Model(4, "", R.drawable.icon_profile)
        )

        binding.navBar.setOnClickMenuListener {
            when(it.id) {
                1 -> { switchFragment(LandingFragment()) }
                2 -> {  }
                3 -> { switchFragment(BookmarkFragment()) }
                4 -> { switchFragment(ProfileFragment()) }
            }
        }

        switchFragment(LandingFragment())
        binding.navBar.show(1)

    }

    private fun switchFragment(fragment : Fragment) {
        supportFragmentManager.beginTransaction()?.apply {
            replace(R.id.fl_fragment, fragment)
            commit()
        }
    }

}