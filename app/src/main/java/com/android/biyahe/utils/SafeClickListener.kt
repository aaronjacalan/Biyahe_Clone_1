package com.android.biyahe.utils

import android.os.SystemClock
import android.view.View

abstract class SafeClickListener : View.OnClickListener {
    private var lastClickTime: Long = 0
    private val minClickInterval = 600L

    abstract fun onSafeCLick()

    override fun onClick(v: View?) {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastClickTime > minClickInterval) {
            lastClickTime = currentTime
            onSafeCLick()
        }
    }
}