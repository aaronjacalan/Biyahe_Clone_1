package com.android.biyahe.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.android.biyahe.R
import com.android.biyahe.data.Route
import com.android.biyahe.data.RouteDataManager
import com.android.biyahe.helper.RouteAdapter
import com.android.biyahe.utils.toast

/**
 * A simple [Fragment] subclass.
 * Use the [BookmarkFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BookmarkFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val route_bookmarks = view.findViewById<ListView>(R.id.lv_route_bookmarks)
        val routeList = RouteDataManager.bookmarked

        val arrayAdapter = RouteAdapter(requireContext(), routeList)
        arrayAdapter.routeList = routeList
        route_bookmarks.adapter = arrayAdapter

        // Temporary Operation on delete-ing
//        route_bookmarks.setOnItemLongClickListener { _, _, position, _ ->
//
//            val alertBuilder = AlertDialog.Builder(this)
//            alertBuilder.setTitle("Remove Route")
//            alertBuilder.setMessage("Do you wish to remove Route: ${routeList[position].jeepney_code} from bookmarks?")
//
//            alertBuilder.setPositiveButton("Yes") { dialog, _ ->
//                val temp : Route = routeList[position]
//                RouteDataManager.bookmarked.remove(routeList[position])
//                toast("${temp.jeepney_code} is removed from bookmarks!")
//                arrayAdapter.notifyDataSetChanged()
//            }
//            alertBuilder.setNegativeButton("No") { dialog, _ ->
//                dialog.dismiss()
//            }
//
//            val alertDialog = alertBuilder.create()
//            alertDialog.show()
//
//            true
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }
}