package com.android.biyahe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.biyahe.databinding.ActivityNavigationBinding
import com.android.biyahe.fragments.BookmarkFragment
import com.android.biyahe.fragments.LandingFragment
import com.android.biyahe.fragments.ProfileFragment

class NavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        switchFragment(LandingFragment())

        binding.navBar.setOnItemSelectedListener {item ->
            when(item.itemId) {
                R.id.ic_home -> {
                    switchFragment(LandingFragment())
                }
                R.id.ic_bookmark -> {
                    switchFragment(BookmarkFragment())
                }
                R.id.ic_route -> {

                }
                R.id.ic_profile -> {
                    switchFragment(ProfileFragment())
                }
            }
            true
        }

    }

    private fun switchFragment(fragment : Fragment) {
        supportFragmentManager.beginTransaction()?.apply {
            replace(R.id.fl_fragment, fragment)
            commit()
        }
    }
}