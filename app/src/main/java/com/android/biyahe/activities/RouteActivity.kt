package com.android.biyahe.activities

import android.app.Activity
import android.os.Bundle
import android.widget.ExpandableListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.biyahe.R
import com.android.biyahe.data.Route
import com.android.biyahe.databinding.ActivityRouteBinding
import com.android.biyahe.helper.DestinationAdapter

class RouteActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRouteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val route = intent.getParcelableExtra<Route>("route")!!

        binding.tvRouteCode.setText(route.jeepney_code)
        binding.btnBack.setOnClickListener {
            finish()
        }

        val adapter = DestinationAdapter(
                this,
                route,
                route.destinations,
            )
        binding.lvRouteDestinations.setAdapter(adapter)

    }
}