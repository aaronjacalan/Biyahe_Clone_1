package com.android.biyahe.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import com.android.biyahe.R
import com.android.biyahe.activities.RouteActivity
import com.android.biyahe.data.RouteDataManager
import com.android.biyahe.database.FirebaseManager
import com.android.biyahe.helper.RouteAdapter

class LandingFragment : Fragment() {

    private lateinit var welcomeText: TextView
    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_landing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val routes = view.findViewById<ListView>(R.id.lv_routes)
        val routeList = RouteDataManager.routelist
        val bookmarked = RouteDataManager.bookmarked

        val onlineMode = arguments?.getBoolean("online_mode", true) == true
        welcomeText = view.findViewById(R.id.tv_title)
        val username = try {
            FirebaseManager.current_user?.username ?: ""
        } catch (e: Exception) {
            ""
        }

        if (onlineMode && username.isNotBlank()) welcomeText.text = "Welcome, $username!"
        else welcomeText.text = "Welcome, Guest!"

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

        val searchBar = view.findViewById<SearchView>(R.id.landing_search_bar)
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(query: String?): Boolean {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                searchRunnable = Runnable {
                    val filteredList = routeList.filter { route ->
                        val codeMatch = route.code.contains(query ?: "", ignoreCase = true)
                        val destinationToMatch = route.destinations_to.any { destination ->
                            destination.title.lowercase().contains(query ?: "", ignoreCase = true)
                        }
                        val destinationBackMatch = route.destinations_back.any { destination ->
                            destination.title.lowercase().contains(query ?: "", ignoreCase = true)
                        }
                        codeMatch || destinationToMatch || destinationBackMatch
                    }
                    arrayAdapter.updateList(filteredList)
                }
                searchHandler.postDelayed(searchRunnable!!, 500)
                return true
            }
        })
    }

    override fun onDestroyView() {
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
        super.onDestroyView()
    }

}