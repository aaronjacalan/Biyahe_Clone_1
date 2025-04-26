package com.android.biyahe.helper

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.biyahe.R
import com.android.biyahe.data.Destination
import com.android.biyahe.data.Route

class DestinationAdapter(
    private val context: Context,
    private val route: Route
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isExpanded = false

    companion object {
        private const val VIEW_TYPE_GROUP = 0
        const val VIEW_TYPE_CHILD = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_GROUP else VIEW_TYPE_CHILD
    }

    override fun getItemCount(): Int {
        return if (isExpanded) {
            1 + route.destinations.size
        } else {
            1 // Only group visible
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_GROUP) {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.destination_header, parent, false)
            GroupViewHolder(view)
        } else {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.expandable_list_view_destination, parent, false)
            ChildViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GroupViewHolder) {
            holder.bind()
        } else if (holder is ChildViewHolder) {
            holder.bind(route.destinations[position - 1]) // subtract 1 because position 0 = group
        }
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tv_title)

        fun bind() {
            title.text = "Destinations"
            itemView.setOnClickListener {
                isExpanded = !isExpanded
                notifyDataSetChanged()
            }
        }
    }

    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val level: ImageView = itemView.findViewById(R.id.iv_destination_level)
        private val title: TextView = itemView.findViewById(R.id.tv_destination)
        private val icon: ImageView = itemView.findViewById(R.id.iv_destination_icon)

        fun bind(destination: Destination) {
            val position = bindingAdapterPosition - 1 // child index

            if (position == 0) {
                level.setImageResource(R.drawable.icon_end_node)
                level.rotation = 90f
            } else if (position == route.destinations.size - 1) {
                level.setImageResource(R.drawable.icon_end_node)
                level.rotation = -90f
            } else {
                level.setImageResource(R.drawable.icon_mid_node)
                level.rotation = 0f
            }

            title.text = destination.title

            icon.setImageResource(
                if (destination.type == "School") {
                    R.drawable.icon_star
                } else {
                    R.drawable.icon_star_not
                }
            )
        }
    }
}