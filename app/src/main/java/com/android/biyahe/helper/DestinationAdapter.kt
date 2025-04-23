package com.android.biyahe.helper

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.android.biyahe.R
import com.android.biyahe.data.Destination
import com.android.biyahe.data.Route

class DestinationAdapter(
    private val context : Context,
    private var route : Route,
    private var destinations : List<Destination>
    ) : BaseExpandableListAdapter() {
    override fun getGroupCount(): Int = 1

    override fun getChildrenCount(groupPosition: Int): Int = route.destinations.size

    override fun getGroup(groupPosition: Int): Any = route

    override fun getChild(groupPosition: Int, childPosition: Int): Any = route.destinations[childPosition]

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val groupTitle : String = "Destinations"
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_expandable_list_item_1, parent, false)
        view.findViewById<TextView>(android.R.id.text1).text = groupTitle
        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val destination = destinations[childPosition]
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.expandable_list_view_destination, parent, false)

        val level = view.findViewById<ImageView>(R.id.iv_destination_level)
        if(childPosition == 0) {
            level.setImageResource(R.drawable.icon_end_node)
            level.rotation = 90f
        } else if(childPosition == destinations.size-1) {
            level.setImageResource(R.drawable.icon_end_node)
            level.rotation = -90f
        } else {
            level.setImageResource(R.drawable.icon_mid_node)
        }

        val title = view.findViewById<TextView>(R.id.tv_destination)
        title.setText(destination.title)

        val icon = view.findViewById<ImageView>(R.id.iv_destination_icon)
        icon.setImageResource(
            if(destination.type.equals("School")) {
                R.drawable.icon_star
            } else {
                R.drawable.icon_star_not
            }
        )

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}
