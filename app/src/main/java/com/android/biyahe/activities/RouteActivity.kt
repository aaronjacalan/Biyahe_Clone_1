package com.android.biyahe.activities

import android.app.Activity
import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.biyahe.R
import com.android.biyahe.data.Route
import com.android.biyahe.databinding.ActivityRouteBinding
import com.android.biyahe.helper.DestinationAdapter
import com.android.biyahe.helper.DestinationItemDecoration

class RouteActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRouteBinding
    private var isTo : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val route = intent.getParcelableExtra<Route>("route")!!

        binding.tvRouteCode.setText(route.code)
//        binding.pvRouteDisplay.setImageResource(route.route_resource)
        binding.btnBack.setOnClickListener {
            finish()
        }

        val adapter = DestinationAdapter(
                this,
                route
            )
        binding.lvRouteDestinations.setAdapter(adapter)
        binding.lvRouteDestinations.layoutManager = LinearLayoutManager(this)
        binding.lvRouteDestinations.addItemDecoration(DestinationItemDecoration(adapter))

        binding.btnChangeRoute.setOnClickListener{
            adapter.updateData()
        }

    }
}