package com.chilisoft.superslider

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.support.v7.widget.AppCompatSeekBar
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator


internal class Slider @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatSeekBar(context, attrs, defStyleAttr) {
    var COMPLETE_TRESHOLD: Int = 75
    var onSlideCompleteListener: (() -> Unit)? = null
    var onSlideChangeListener: ((Float) -> Unit)? = null

    init {
        setOnSeekBarChangeListener(SimpleSeekBarChangeListener(this::onSeekbarChange))
    }

    fun onSeekbarChange(progress: Int): Unit {
        onSlideChangeListener?.invoke(progress.toFloat() / max)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_DOWN) {
            val draggableView = thumb ?: return false

            if (!draggableView.bounds.contains(event.x.toInt(), event.y.toInt())) {
                return false
            }
        }

        if (event.action == MotionEvent.ACTION_UP) {
            if (progress > COMPLETE_TRESHOLD) {
                slideToComplete()
            } else {
                reset()
            }

            return true
        }


        super.onTouchEvent(event)

        return true
    }

    internal fun slideToComplete() {
        val valueAnimator = ValueAnimator.ofInt(progress, max)
        valueAnimator.duration = 200
        valueAnimator.addUpdateListener {
            val animatedValue = it.animatedValue as Int
            progress = animatedValue
        }

        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                onSlideCompleteListener?.invoke()
            }
        })
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.start()
    }

    fun reset(withAnim: Boolean = true) {
        if (withAnim) {
            val valueAnimator = ValueAnimator.ofInt(progress, 0)
            valueAnimator.duration = 800
            valueAnimator.addUpdateListener {
                val animatedValue = it.animatedValue as Int
                progress = animatedValue
            }
            valueAnimator.interpolator = BounceInterpolator()

            valueAnimator.start()
        } else {
            progress = 0
        }
    }
}

