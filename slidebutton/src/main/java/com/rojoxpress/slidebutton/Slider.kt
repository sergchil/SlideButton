package com.rojoxpress.slidebutton

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.widget.AppCompatSeekBar
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.SeekBar

class Slider @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatSeekBar(context, attrs, defStyleAttr) {

    private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            slideChangeListener?.invoke(i.toFloat() / max)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {

        }
    }
    var onSlideComplete: (() -> Unit)? = null
    var slideChangeListener: ((Float) -> Unit)? = null

    init {
        setOnSeekBarChangeListener(seekBarChangeListener)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_DOWN) {
            val draggableView = thumb ?: return false

            if (!draggableView.bounds.contains(event.x.toInt(), event.y.toInt())) {
                return false
            }
        }

        if (event.action == MotionEvent.ACTION_UP) {
            if (progress > 75) {
                slideToComplete()
            } else {
                reset()
            }

            return true
        }


        super.onTouchEvent(event)

        return true
    }

    private fun slideToComplete() {
        val valueAnimator = ValueAnimator.ofInt(progress, max)
        valueAnimator.duration = 200
        valueAnimator.addUpdateListener {
            val animatedValue = it.animatedValue as Int
            progress = animatedValue
        }

        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                onSlideComplete?.invoke()
            }
        })
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.start()
    }

    private fun reset() {
        val valueAnimator = ValueAnimator.ofInt(progress, 0)
        valueAnimator.duration = 800
        valueAnimator.addUpdateListener {
            val animatedValue = it.animatedValue as Int
            progress = animatedValue
        }
        valueAnimator.interpolator = BounceInterpolator()

        valueAnimator.start()
    }
}