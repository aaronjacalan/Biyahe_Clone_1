package com.android.biyahe.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.biyahe.R

class ParallaxScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    private var headerLayout: ConstraintLayout? = null
    private var contentLayout: ConstraintLayout? = null
    private val parallaxFactor = 0.6f

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0) {
            val mainContent = getChildAt(0)
            if (mainContent is ViewGroup) {
                findHeaderAndContent(mainContent)
            }
        }
    }

    private fun findHeaderAndContent(viewGroup: ViewGroup) {
        headerLayout = viewGroup.findViewById(R.id.headerLayout)
        contentLayout = viewGroup.findViewById(R.id.contentLayout)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        applyParallaxEffect(t)
    }

    private fun applyParallaxEffect(scrollY: Int) {
        headerLayout?.let {
            it.translationY = scrollY * parallaxFactor

            val scrollProgress = (scrollY / 500f).coerceAtMost(1f)

            applyEffectToView(it.findViewById(R.id.CircleImageIcon), scrollProgress)
            applyEffectToView(it.findViewById(R.id.editProfile_goBack), scrollProgress)
            applyEffectToView(it.findViewById(R.id.editProfile_saveChanges), scrollProgress)

            applyEffectToView(it.findViewById(R.id.viewProfile_editProfile), scrollProgress)
            applyEffectToView(it.findViewById(R.id.viewProfile_goBack), scrollProgress)

            applyEffectToView(it.findViewById(R.id.text_username), scrollProgress)
            applyEffectToView(it.findViewById(R.id.UIDTextView), scrollProgress)

            applyEffectToView(it.findViewById(R.id.txtAboutHeader), scrollProgress)
            applyEffectToView(it.findViewById(R.id.developerPageGoBack), scrollProgress)

            applyEffectToView(it.findViewById(R.id.tv_bookmarksHeader), scrollProgress)
        }
    }

    private fun applyEffectToView(view: View?, progress: Float) {
        view?.apply {
            val scale = 1f - (0.3f * progress)
            scaleX = scale
            scaleY = scale
            alpha = 1f - (0.5f * progress)
        }
    }
}
