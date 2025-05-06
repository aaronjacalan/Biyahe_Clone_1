package com.android.biyahe.helper

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.android.biyahe.R
import com.android.biyahe.data.Route
import com.android.biyahe.database.FirebaseManager // <-- import this

class RouteAdapter(
    private val context: Context,
    var routeList: List<Route>,
    private val bookmarkList: MutableList<Route>,
    private val onClick: (Route) -> Unit
) : BaseAdapter() {
    override fun getCount(): Int = routeList.size

    override fun getItem(position: Int): Any = routeList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_view_route, parent, false)

        val ivRoute = view.findViewById<ImageView>(R.id.iv_route)
        val tvRouteCode = view.findViewById<TextView>(R.id.tv_route_code)
        val vBookmark = view.findViewById<View>(R.id.v_bookmark)
        val ibBookmark = view.findViewById<ImageButton>(R.id.ib_bookmark)
        val tvFrom = view.findViewById<TextView>(R.id.tv_from)
        val tvTo = view.findViewById<TextView>(R.id.tv_to)

        val route = routeList[position]
        ivRoute.setImageResource(route.profile)
        tvRouteCode.text = route.code

        if (route.destinations_to.isNotEmpty()) {
            tvFrom.text = route.destinations_to.first().title
            tvTo.text = route.destinations_to.last().title
        } else {
            tvFrom.text = "ORIGIN"
            tvTo.text = "DESTINATION"
        }

        val isBookmarked = bookmarkList.contains(route)
        ibBookmark.setImageResource(
            if (isBookmarked) R.drawable.icon_star else R.drawable.icon_star_not
        )

        val bookmarkListener = View.OnClickListener {
            if (isBookmarked) {
                animateBookmark(ibBookmark)
                bookmarkList.remove(route)
            } else {
                animateBookmark(ibBookmark)
                bookmarkList.add(route)
            }
            FirebaseManager.updateBookmark(bookmarkList)
            FirebaseManager.remoteSaveUserInstance()
            notifyDataSetChanged()
        }
        vBookmark.setOnClickListener(bookmarkListener)
        ibBookmark.setOnClickListener(bookmarkListener)

        view.setOnClickListener {
            onClick(route)
        }

        return view
    }

    private fun animateBookmark(view: View) {
        // Scale up and down animation
        val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 3f)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 3f)
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 3f, 1f)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 3f, 1f)

        // Rotation animation for "magical" effect
        val rotate = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f)

        // Alpha fade-in and fade-out
        val fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.5f)
        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0.5f, 1f)

        scaleUpX.duration = 150
        scaleUpY.duration = 150
        scaleDownX.duration = 150
        scaleDownY.duration = 150
        rotate.duration = 300
        fadeOut.duration = 150
        fadeIn.duration = 150

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleUpX, scaleUpY, rotate, fadeOut)
        animatorSet.play(scaleDownX).with(scaleDownY).with(fadeIn).after(scaleUpX)
        animatorSet.interpolator = DecelerateInterpolator()

        animatorSet.start()
    }

    fun updateList(filteredList: List<Route>) {
        routeList = filteredList
        notifyDataSetChanged()
    }
}