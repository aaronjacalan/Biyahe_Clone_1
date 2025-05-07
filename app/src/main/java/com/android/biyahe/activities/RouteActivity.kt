package com.android.biyahe.activities

import android.animation.Animator
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.android.biyahe.R
import com.android.biyahe.data.Route
import com.android.biyahe.databinding.ActivityRouteBinding
import com.android.biyahe.helper.DestinationAdapter
import com.android.biyahe.helper.DestinationItemDecoration

class RouteActivity : Activity() {

    private lateinit var binding: ActivityRouteBinding
    private lateinit var lottieView: LottieAnimationView
    private var isTo: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFullScreenView()

        binding = ActivityRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lottieView = findViewById(R.id.lottieView)
        setLottieDirection(isTo)

        val route = intent.getParcelableExtra<Route>("route") ?: return

        binding.tvRouteCode.text = "${route.code} - Forwards"
        binding.pvRouteDisplay.setImageResource(route.routeTo_resource)
        binding.btnBack.setOnClickListener {
            finish()
        }

        val adapter = DestinationAdapter(this, route)
        binding.lvRouteDestinations.adapter = adapter
        binding.lvRouteDestinations.layoutManager = LinearLayoutManager(this)
        binding.lvRouteDestinations.addItemDecoration(DestinationItemDecoration(adapter))

        binding.lottieView.setOnClickListener {
            lottieView.removeAllAnimatorListeners()
            setLottieDirection(isTo)
            lottieView.playAnimation()

            lottieView.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    isTo = !isTo
                    if (isTo) {
                        binding.pvRouteDisplay.setImageResource(route.routeTo_resource)
                        binding.tvRouteCode.text = "${route.code} - Forwards"
                    } else {
                        binding.pvRouteDisplay.setImageResource(route.routeBack_resource)
                        binding.tvRouteCode.text = "${route.code} - Backwards"
                    }
                    adapter.updateData()
                    setLottieDirection(isTo)
                    lottieView.removeAllAnimatorListeners()
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
    }

    private fun setLottieDirection(toDirection: Boolean) {
        lottieView.repeatCount = 0
        lottieView.repeatMode = LottieDrawable.RESTART
        lottieView.speed = if (toDirection) 2.5f else -2.5f
        lottieView.progress = if (toDirection) 0f else 1f
        lottieView.pauseAnimation()
    }

    private fun setFullScreenView() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

}