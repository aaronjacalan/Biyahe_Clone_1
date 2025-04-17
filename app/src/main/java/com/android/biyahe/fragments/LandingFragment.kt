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
import com.android.biyahe.R
import com.android.biyahe.SettingsActivity
import com.android.biyahe.data.RouteDataManager
import com.android.biyahe.databinding.FragmentLandingBinding
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
        val bookmarked = RouteDataManager.bookmarked

        val arrayAdapter = RouteAdapter(requireContext(), routeList)
        arrayAdapter.routeList = routeList
        routes.adapter = arrayAdapter

        // Temporary Operation on adding route to bookmarks
//        routes.setOnItemLongClickListener { _, _, position, _ ->
//            if(bookmarked.contains(routeList[position])) {
//                toast("${routeList[position].jeepney_code} is already bookmarked!")
//            } else {
//                val alertBuilder = AlertDialog.Builder(requireContext())
//                alertBuilder.setTitle("Add Route")
//                alertBuilder.setMessage("Do you wish to bookmark Route: ${routeList[position].jeepney_code}?")
//
//                alertBuilder.setPositiveButton("Yes") { dialog, _ ->
//                    RouteDataManager.bookmarked.add(routeList[position])
//                    toast("${routeList[position].jeepney_code} is bookmarked!")
//                }
//                alertBuilder.setNegativeButton("No") { dialog, _ ->
//                    dialog.dismiss()
//                }
//
//                val alertDialog = alertBuilder.create()
//                alertDialog.show()
//
//            }
//            true
//        }

        val btn_settings = view.findViewById<ImageView>(R.id.btn_settings)
        btn_settings.setOnClickListener {
            Log.e("Landing Activity", "Proceeding to Settings")
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }
    }

}