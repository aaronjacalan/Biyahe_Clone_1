package com.android.biyahe.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.android.biyahe.R
import com.android.biyahe.activities.RouteActivity
import com.android.biyahe.data.Route
import com.android.biyahe.data.RouteDataManager
import com.android.biyahe.database.FirebaseManager
import com.android.biyahe.helper.RouteAdapter
import com.android.biyahe.utils.toast

class BookmarkFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val route_bookmarks = view.findViewById<ListView>(R.id.lv_route_bookmarks)

        setUserBookmarks()
        val routeList = RouteDataManager.bookmarked

        val arrayAdapter = RouteAdapter(
            requireContext(),
            routeList,
            routeList,
            onClick = { it ->
                startActivity(Intent(requireContext(), RouteActivity::class.java).apply {
                    putExtra("route", it)
                })
            })
        route_bookmarks.adapter = arrayAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    // To convert List<String> into List<Route>
    fun setUserBookmarks() {
        val routes = RouteDataManager.routelist
        val user_bookmarks : List<String> = FirebaseManager.current_user.bookmarkList
        for(r in routes) {
            if(user_bookmarks.contains(r.code) && !RouteDataManager.bookmarked.contains(r)) {
                RouteDataManager.bookmarked.add(r)
            }
        }
    }
}