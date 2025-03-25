package com.android.biyahe

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ListView
import com.android.biyahe.data.Route
import com.android.biyahe.helper.RouteCustomListViewAdapter
import com.android.biyahe.data.RouteDataManager
import com.android.biyahe.utils.toast

class BookmarkActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

        val route_bookmarks = findViewById<ListView>(R.id.lv_route_bookmarks)
        val routeList = RouteDataManager.bookmarked

        val arrayAdapter = RouteCustomListViewAdapter(this, routeList)
        arrayAdapter.routeList = routeList
        route_bookmarks.adapter = arrayAdapter

        // Temporary Operation on delete-ing
        route_bookmarks.setOnItemLongClickListener { _, _, position, _ ->

            val alertBuilder = AlertDialog.Builder(this)
            alertBuilder.setTitle("Remove Route")
            alertBuilder.setMessage("Do you wish to remove Route: ${routeList[position].jeepney_code} from bookmarks?")

            alertBuilder.setPositiveButton("Yes") { dialog, _ ->
                val temp : Route = routeList[position]
                RouteDataManager.bookmarked.remove(routeList[position])
                toast("${temp.jeepney_code} is removed from bookmarks!")
                arrayAdapter.notifyDataSetChanged()
            }
            alertBuilder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = alertBuilder.create()
            alertDialog.show()

            true
        }

        val btn_home = findViewById<ImageButton>(R.id.btn_home)
        btn_home.setOnClickListener {
            Log.e("Landing Activity", "Proceeding to Home")
            startActivity(Intent(this, LandingActivity::class.java))
        }

        val btn_profile = findViewById<ImageButton>(R.id.btn_profile)
        btn_profile.setOnClickListener {
            Log.e("Landing Activity", "Proceeding to Profile")
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        val btn_bookmarks = findViewById<ImageButton>(R.id.btn_bookmark)
        btn_bookmarks.setOnClickListener {
            toast("You are already at Bookmarks!")
        }

    }
}