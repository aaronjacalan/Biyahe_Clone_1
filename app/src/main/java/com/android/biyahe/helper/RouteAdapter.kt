package com.android.biyahe.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.android.biyahe.R
import com.android.biyahe.data.Route

class RouteAdapter(private val context : Context,
                   var routeList : List<Route>,
                   val bookmarkList : MutableList<Route>) : BaseAdapter() {
    override fun getCount(): Int = routeList.size

    override fun getItem(position: Int): Any = routeList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_view_route, parent, false)

        val iv_route = view.findViewById<ImageView>(R.id.iv_route)
        val tv_route_code = view.findViewById<TextView>(R.id.tv_route_code)
        val ib_bookmark = view.findViewById<ImageButton>(R.id.ib_bookmark)

        val route = routeList[position]
        iv_route.setImageResource(route.photoResource)
        tv_route_code.setText("${route.jeepney_code} ${route.destination}")

        val isBookmarked = bookmarkList.contains(route)
        ib_bookmark.setImageResource(
            if (isBookmarked) R.drawable.icon_star else R.drawable.icon_star_not
        )

        ib_bookmark.setOnClickListener {
            if (isBookmarked) {
                removeItem(route)
            } else {
                addItem(route)
            }
        }

        return view
    }

    private fun addItem(route : Route) {
        bookmarkList.add(route)
        notifyDataSetChanged()
    }
    private fun removeItem(route : Route) {
        bookmarkList.remove(route)
        notifyDataSetChanged()
    }



}