package com.android.biyahe.temp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.android.biyahe.R
import com.android.biyahe.databinding.ActivityNavigationBinding

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