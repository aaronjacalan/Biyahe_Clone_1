package com.android.biyahe

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import com.android.biyahe.data.RouteDataManager
import com.android.biyahe.helper.RouteCustomListViewAdapter
import com.android.biyahe.utils.toast

class LandingActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val routes = findViewById<ListView>(R.id.lv_routes)
        val routeList = RouteDataManager.routelist
        // To check if already bookmarked
        val bookmarked = RouteDataManager.bookmarked

        val arrayAdapter = RouteCustomListViewAdapter(this, routeList)
        arrayAdapter.routeList = routeList
        routes.adapter = arrayAdapter

        // Temporary Operation on adding route to bookmarks
        routes.setOnItemLongClickListener { _, _, position, _ ->
            if(bookmarked.contains(routeList[position])) {
                toast("${routeList[position].jeepney_code} is already bookmarked!")
            } else {
                val alertBuilder = AlertDialog.Builder(this)
                alertBuilder.setTitle("Add Route")
                alertBuilder.setMessage("Do you wish to bookmark Route: ${routeList[position].jeepney_code}?")

                alertBuilder.setPositiveButton("Yes") { dialog, _ ->
                    RouteDataManager.bookmarked.add(routeList[position])
                    toast("${routeList[position].jeepney_code} is bookmarked!")
                }
                alertBuilder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }

                val alertDialog = alertBuilder.create()
                alertDialog.show()

            }
            true
        }

        val btn_settings = findViewById<ImageView>(R.id.btn_settings)
        btn_settings.setOnClickListener {
            Log.e("Landing Activity", "Proceeding to Settings")
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        val btn_home = findViewById<ImageButton>(R.id.btn_home)
        btn_home.setOnClickListener {
            toast("You are already at Home")
        }

        val btn_profile = findViewById<ImageButton>(R.id.btn_profile)
        btn_profile.setOnClickListener {
            Log.e("Landing Activity", "Proceeding to Profile")
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        val btn_bookmarks = findViewById<ImageButton>(R.id.btn_bookmark)
        btn_bookmarks.setOnClickListener {
            Log.e("Landing Activity", "Proceeding to Bookmarks")
            startActivity(Intent(this, BookmarkActivity::class.java))
        }

    }
}
