package com.chilisoft.superslider

import android.content.res.Resources
import android.transition.Transition
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar

/**
 * Created with ❤ by Sergey Chilingaryan
 */


// dp to pixels
val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
val Float.dp: Int get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

// sp to pixels
val Float.sp: Int get() = (this * Resources.getSystem().displayMetrics.scaledDensity + 0.5f).toInt()
val Int.sp: Int get() = (this * Resources.getSystem().displayMetrics.scaledDensity + 0.5f).toInt()


fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.isHidden() = this.visibility == View.GONE
fun View.isVisible() = this.visibility == View.VISIBLE
fun View.isInvisible() = this.visibility == View.INVISIBLE
fun View.showIf(condition: Boolean) = if (condition) show() else hide()


internal class SimpleSeekBarChangeListener(val onChange: (progress: Int) -> Unit) : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        onChange(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
}


internal class SimpleTransitionEndListener(val onEnd: () -> Unit) : Transition.TransitionListener {
    override fun onTransitionEnd(transition: Transition?) { onEnd() }

    override fun onTransitionResume(transition: Transition?) {}

    override fun onTransitionPause(transition: Transition?) {}

    override fun onTransitionCancel(transition: Transition?) {}

    override fun onTransitionStart(transition: Transition?) {}
}

internal class SimpleTransitionStartListener(val onStart: () -> Unit) : Transition.TransitionListener {
    override fun onTransitionEnd(transition: Transition?) {}

    override fun onTransitionResume(transition: Transition?) {}

    override fun onTransitionPause(transition: Transition?) {}

    override fun onTransitionCancel(transition: Transition?) {}

    override fun onTransitionStart(transition: Transition?) { onStart() }
}

fun View.setHeight(height: Int) {
    if (layoutParams is ViewGroup.LayoutParams) {
        val params = layoutParams
        params.height = height
        requestLayout()
    }
}