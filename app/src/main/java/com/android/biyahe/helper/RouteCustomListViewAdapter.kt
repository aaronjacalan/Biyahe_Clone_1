package com.android.biyahe.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.android.biyahe.R
import com.android.biyahe.data.Route

class RouteCustomListViewAdapter(private val context : Context,
                                 var routeList : List<Route>) : BaseAdapter() {
    override fun getCount(): Int = routeList.size

    override fun getItem(position: Int): Any = routeList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.route_item_list_view, parent, false)

        val iv_route = view.findViewById<ImageView>(R.id.iv_route)
        val tv_route_code = view.findViewById<TextView>(R.id.tv_route_code)

        val route = routeList[position]
        iv_route.setImageResource(route.photoResource)
        tv_route_code.setText("${route.jeepney_code} - ${route.destination}")
        return view
    }
}