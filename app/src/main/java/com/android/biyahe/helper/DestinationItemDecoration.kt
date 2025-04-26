package com.android.biyahe.helper

import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.biyahe.R

class DestinationItemDecoration(
    private val adapter: DestinationAdapter
) : RecyclerView.ItemDecoration() {

    private var headerView: View? = null

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        if (headerView == null) {
            headerView = createHeaderView(parent)
            fixLayoutSize(parent, headerView!!)
        }

        val child = parent.getChildAt(0) ?: return
        val params = child.layoutParams as RecyclerView.LayoutParams

        val offset = 0

        c.save()
        c.translate(0f, offset.toFloat())
        headerView?.draw(c)
        c.restore()
    }

    private fun createHeaderView(parent: RecyclerView): View {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.destination_header, parent, false)
        return view
    }

    private fun fixLayoutSize(parent: ViewGroup, view: View) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        val childWidthSpec = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            view.layoutParams.width
        )
        val childHeightSpec = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            view.layoutParams.height
        )

        view.measure(childWidthSpec, childHeightSpec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }
}
