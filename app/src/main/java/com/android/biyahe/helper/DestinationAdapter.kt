package com.android.biyahe.helper

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.biyahe.R
import com.android.biyahe.data.Destination
import com.android.biyahe.data.Route

class DestinationAdapter(
    private val context: Context,
    private val route: Route
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isExpanded = false
    private var isTo = true;

    companion object {
        private const val VIEW_TYPE_GROUP = 0
        const val VIEW_TYPE_CHILD = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_GROUP else VIEW_TYPE_CHILD
    }

    override fun getItemCount(): Int {
        return if (isExpanded) {
            1 + (if(isTo) route.destinations_to.size else route.destinations_back.size)
        } else {
            1 // Only group visible
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_GROUP) {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.expandable_list_view_destination_group, parent, false)
            GroupViewHolder(view)
        } else {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.expandable_list_view_destination_children, parent, false)
            ChildViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GroupViewHolder) {
            holder.bind()
        } else if (holder is ChildViewHolder) {
            holder.bind(
                if(isTo) route.destinations_to[position - 1] else route.destinations_back[position - 1]
            )
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
        private val level: View = itemView.findViewById(R.id.iv_point)
        private val title: TextView = itemView.findViewById(R.id.tv_destination)
        private val icon: ImageView = itemView.findViewById(R.id.iv_destination_icon)

        fun bind(destination: Destination) {
            val position = bindingAdapterPosition - 1 // child index
            title.text = destination.title

            level.setBackgroundColor(
                ContextCompat.getColor(level.context, if (position % 2 == 0) R.color.colorPrimary else R.color.colorSecondary)
            )

            icon.setImageResource(destination.type)
        }
    }

    public fun updateData() {
        if(isTo) {
            isTo = false;
            notifyDataSetChanged()
        } else {
            isTo = true;
            notifyDataSetChanged()
        }
    }
}