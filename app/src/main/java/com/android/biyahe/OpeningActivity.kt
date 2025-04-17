package com.android.biyahe

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat

class OpeningActivity : Activity() {

    private var randomBackground: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opening)

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val headerText = findViewById<TextView>(R.id.tv_openingHeader)
        val textLine1 = findViewById<TextView>(R.id.tv_openingText1)
        val textLine2 = findViewById<TextView>(R.id.tv_openingText2)
        val textLine3 = findViewById<TextView>(R.id.tv_openingText3)
        val button = findViewById<Button>(R.id.btn_getStarted)

        setupBackground()

        headerText.alpha = 0f
        headerText.translationY = -50f

        textLine1.alpha = 0f
        textLine1.translationX = -100f

        textLine2.alpha = 0f
        textLine2.translationX = -150f

        textLine3.alpha = 0f
        textLine3.translationX = -200f

        button.alpha = 0f
        button.scaleX = 0.7f
        button.scaleY = 0.7f
        button.translationY = 100f

        startEntranceAnimations(headerText, textLine1, textLine2, textLine3, button)

        button.setOnClickListener {
            startExitAnimations {
                val intent = Intent(this, RegisterActivity::class.java)
                intent.putExtra("BACKGROUND_ID", randomBackground)
                startActivity(intent)
                overridePendingTransition(0, 0)  // Added this line to remove slide animation
                finish()
            }
        }
    }

    private fun setupBackground() {
        val layout = findViewById<ConstraintLayout>(R.id.main)
        val backgrounds = listOf(
            R.drawable.background_grainy1,
            R.drawable.background_grainy2,
            R.drawable.background_grainy3,
            R.drawable.background_grainy4
        )
        randomBackground = backgrounds.random()
        val backgroundImage = ContextCompat.getDrawable(this, randomBackground)
        layout.background = backgroundImage
    }

    private fun startEntranceAnimations(
        headerText: TextView,
        textLine1: TextView,
        textLine2: TextView,
        textLine3: TextView,
        button: Button
    ) {
        val headerFadeIn = ObjectAnimator.ofFloat(headerText, "alpha", 0f, 1f).apply {
            duration = 800
            interpolator = DecelerateInterpolator()
        }

        val headerSlideDown = ObjectAnimator.ofFloat(headerText, "translationY", -50f, 0f).apply {
            duration = 800
            interpolator = OvershootInterpolator(1.2f)
        }

        val headerAnimSet = AnimatorSet().apply {
            playTogether(headerFadeIn, headerSlideDown)
        }

        val line1FadeIn = ObjectAnimator.ofFloat(textLine1, "alpha", 0f, 1f).apply {
            duration = 700
        }

        val line1SlideIn = ObjectAnimator.ofFloat(textLine1, "translationX", -100f, 0f).apply {
            duration = 700
            interpolator = DecelerateInterpolator(1.5f)
        }

        val line1AnimSet = AnimatorSet().apply {
            playTogether(line1FadeIn, line1SlideIn)
            startDelay = 100
        }

        val line2FadeIn = ObjectAnimator.ofFloat(textLine2, "alpha", 0f, 1f).apply {
            duration = 700
        }

        val line2SlideIn = ObjectAnimator.ofFloat(textLine2, "translationX", -150f, 0f).apply {
            duration = 700
            interpolator = DecelerateInterpolator(1.5f)
        }

        val line2AnimSet = AnimatorSet().apply {
            playTogether(line2FadeIn, line2SlideIn)
            startDelay = 200
        }

        val line3FadeIn = ObjectAnimator.ofFloat(textLine3, "alpha", 0f, 1f).apply {
            duration = 700
        }

        val line3SlideIn = ObjectAnimator.ofFloat(textLine3, "translationX", -200f, 0f).apply {
            duration = 700
            interpolator = DecelerateInterpolator(1.5f)
        }

        val line3AnimSet = AnimatorSet().apply {
            playTogether(line3FadeIn, line3SlideIn)
            startDelay = 300
        }

        val buttonFadeIn = ObjectAnimator.ofFloat(button, "alpha", 0f, 1f).apply {
            duration = 800
        }

        val buttonSlideUp = ObjectAnimator.ofFloat(button, "translationY", 100f, 0f).apply {
            duration = 1000
            interpolator = OvershootInterpolator(1.5f)
        }

        val buttonScaleX = ObjectAnimator.ofFloat(button, "scaleX", 0.7f, 1f).apply {
            duration = 1000
            interpolator = AnticipateOvershootInterpolator(1.2f)
        }

        val buttonScaleY = ObjectAnimator.ofFloat(button, "scaleY", 0.7f, 1f).apply {
            duration = 1000
            interpolator = AnticipateOvershootInterpolator(1.2f)
        }

        val buttonAnimSet = AnimatorSet().apply {
            playTogether(buttonFadeIn, buttonSlideUp, buttonScaleX, buttonScaleY)
            startDelay = 300
        }

        val textAnimSet = AnimatorSet().apply {
            play(headerAnimSet).before(line1AnimSet)
            play(line1AnimSet).before(line2AnimSet)
            play(line2AnimSet).before(line3AnimSet)
            play(line3AnimSet).before(buttonAnimSet)
        }

        textAnimSet.start()

        buttonAnimSet.doOnEnd {
            startTextEmphasisAnimation(textLine1, textLine2, textLine3)
        }
    }

    private fun startTextEmphasisAnimation(textLine1: TextView, textLine2: TextView, textLine3: TextView) {
        val lines = listOf(textLine1, textLine2, textLine3)
        val delays = listOf(0L, 400L, 800L)

        lines.forEachIndexed { index, textView ->
            textView.setShadowLayer(5f, 1f, 1f, 0x33000000)

            val scaleUp = AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(textView, "scaleX", 1f, 1.03f),
                    ObjectAnimator.ofFloat(textView, "scaleY", 1f, 1.03f)
                )
                duration = 2000
                startDelay = delays[index]
                interpolator = AccelerateDecelerateInterpolator()
            }

            val scaleDown = AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(textView, "scaleX", 1.03f, 1f),
                    ObjectAnimator.ofFloat(textView, "scaleY", 1.03f, 1f)
                )
                duration = 2000
                interpolator = AccelerateDecelerateInterpolator()
            }

            val sequence = AnimatorSet().apply {
                playSequentially(scaleUp, scaleDown)
            }

            sequence.start()

            if (index == 2) {
                sequence.doOnEnd {
                    startTextEmphasisAnimation(textLine1, textLine2, textLine3)
                }
            }
        }
    }

    private fun startExitAnimations(onAnimationEnd: () -> Unit) {
        val headerText = findViewById<TextView>(R.id.tv_openingHeader)
        val textLine1 = findViewById<TextView>(R.id.tv_openingText1)
        val textLine2 = findViewById<TextView>(R.id.tv_openingText2)
        val textLine3 = findViewById<TextView>(R.id.tv_openingText3)
        val button = findViewById<Button>(R.id.btn_getStarted)

        val buttonClickFeedback = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.9f, 1f),
                ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.9f, 1f)
            )
            duration = 300
            interpolator = AnticipateOvershootInterpolator()
        }

        // Removed headerFadeOut and headerSlideOut animations

        val line1FadeOut = ObjectAnimator.ofFloat(textLine1, "alpha", 1f, 0f).apply {
            duration = 300
            startDelay = 50
        }
        val line1SlideOut = ObjectAnimator.ofFloat(textLine1, "translationX", 0f, 100f).apply {
            duration = 400
            startDelay = 50
        }

        val line2FadeOut = ObjectAnimator.ofFloat(textLine2, "alpha", 1f, 0f).apply {
            duration = 300
            startDelay = 100
        }
        val line2SlideOut = ObjectAnimator.ofFloat(textLine2, "translationX", 0f, 150f).apply {
            duration = 400
            startDelay = 100
        }

        val line3FadeOut = ObjectAnimator.ofFloat(textLine3, "alpha", 1f, 0f).apply {
            duration = 300
            startDelay = 150
        }
        val line3SlideOut = ObjectAnimator.ofFloat(textLine3, "translationX", 0f, 200f).apply {
            duration = 400
            startDelay = 150
        }

        val buttonFadeOut = ObjectAnimator.ofFloat(button, "alpha", 1f, 0f).apply {
            duration = 300
            startDelay = 200
        }
        val buttonSlideOut = ObjectAnimator.ofFloat(button, "translationY", 0f, 100f).apply {
            duration = 400
            startDelay = 200
        }

        val exitAnimSet = AnimatorSet().apply {
            playTogether(
                // Removed headerFadeOut from this list
                line1FadeOut, line1SlideOut,
                line2FadeOut, line2SlideOut,
                line3FadeOut, line3SlideOut,
                buttonFadeOut, buttonSlideOut
            )
        }

        AnimatorSet().apply {
            playSequentially(buttonClickFeedback, exitAnimSet)
            doOnEnd { onAnimationEnd() }
            start()
        }
    }
}