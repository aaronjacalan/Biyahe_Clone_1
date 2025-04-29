package com.android.biyahe.fragments

import android.content.Intent
import android.os.Bundle
import android.util.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import com.android.biyahe.R
import com.android.biyahe.activities.RouteActivity
import com.android.biyahe.activities.SettingsActivity
import com.android.biyahe.data.RouteDataManager
import com.android.biyahe.database.FirebaseManager
import com.android.biyahe.helper.RouteAdapter

class LandingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_landing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val routes = view.findViewById<ListView>(R.id.lv_routes)
        val routeList = RouteDataManager.routelist
        // To check if already bookmarked
        setUserBookmarks()
        val bookmarked = RouteDataManager.bookmarked

        val arrayAdapter = RouteAdapter(
            requireContext(),
            routeList,
            bookmarked,
            onClick = { it ->
                startActivity(Intent(requireContext(), RouteActivity::class.java).apply {
                    putExtra("route", it)
                })
            })
        routes.adapter = arrayAdapter

        // Search Functionality
        val search_bar = view.findViewById<SearchView>(R.id.landing_search_bar)
        search_bar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(query: String?): Boolean {
                val filteredList = routeList.filter { route ->
                    val code_match = route.code.contains(query ?: "", ignoreCase = true)
                    val destinationTo_match = route.destinations_to.any { destination ->
                        destination.title.lowercase().contains(query ?: "", ignoreCase = true)
                    }
                    val destinationBack_match = route.destinations_back.any { destination ->
                        destination.title.lowercase().contains(query ?: "", ignoreCase = true)
                    }
                    code_match|| destinationTo_match|| destinationBack_match
                }
                arrayAdapter.updateList(filteredList)
                return true
            }
        })
    }

    fun setUserBookmarks() {
        val routes = RouteDataManager.routelist
        val user_bookmarks : List<String> = FirebaseManager.current_user.bookmarkList
        for(r in routes) {
            if(user_bookmarks.contains((r.code))) {
                RouteDataManager.bookmarked.add(r)
            }
        }
    }

}