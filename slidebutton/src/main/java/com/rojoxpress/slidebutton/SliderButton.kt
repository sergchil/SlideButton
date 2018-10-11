package com.rojoxpress.slidebutton

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.slider_main.view.*

class SliderButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private val _sliderTitle: TextView
    private val _slider: Slider
    private val _actionsContainer: ViewGroup
    private val _positiveButton: Button
    private val _negativeButton: Button

    private var onPositiveClickListener: (() -> Unit)? = null
    private var onNegativeClickListener: (() -> Unit)? = null

    // called after showing actions container to reset slider
    private val endTransition = with(AutoTransition()) {
        addListener(SimpleTransitionEndListener(this@SliderButton::onTransitionEnd))
    }

    init {
        val parent = LayoutInflater.from(context).inflate(R.layout.slider_main, this, true)
        _slider = parent.__slider
        _sliderTitle = parent.__slider_title

        _actionsContainer = parent.__actions_container

        _positiveButton = parent.__slider_positive_button
        _positiveButton.setOnClickListener { onPositiveClickListener?.invoke() }

        _negativeButton = parent.__slider_negative_button
        _negativeButton.setOnClickListener { onNegativeClickListener?.invoke() }

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SliderButton, 0, 0)

            if (a.hasValue(R.styleable.SliderButton_text)) {
                val buttonText = a.getString(R.styleable.SliderButton_text)
                setSliderTitle(buttonText)
            }

            if (a.hasValue(R.styleable.SliderButton_thumb)) {
                val thumbDrawable: Drawable? = a.getDrawable(R.styleable.SliderButton_thumb)
                _slider.thumb = thumbDrawable
            }

            if (a.hasValue(R.styleable.SliderButton_thumbOffset)) {
                val offset = a.getDimensionPixelSize(R.styleable.SliderButton_thumbOffset, 10.dp)
            }

            if (a.hasValue(R.styleable.SliderButton_sliderBackground)) {
                ViewCompat.setBackground(this, a.getDrawable(R.styleable.SliderButton_sliderBackground))
            } else {
                ViewCompat.setBackground(this, ContextCompat.getDrawable(context, R.drawable.back_slide_button))
            }

            val unitsTextSize = a.getDimensionPixelSize(R.styleable.SliderButton_textSize, 20.dp).toFloat()

            _sliderTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitsTextSize)

            val color = a.getColor(R.styleable.SliderButton_textColor, Color.WHITE)
            _sliderTitle.setTextColor(color)

            a.recycle()
        }
    }

    fun setSliderTitle(@StringRes res: Int) {
        _sliderTitle.setText(res)
    }

    fun setSliderTitle(charSequence: String?) {
        _sliderTitle.text = charSequence ?: ""
    }

    fun setThumb(drawable: Drawable?) {
        _slider.thumb = drawable ?: return
    }

    fun setThumb(@DrawableRes drawableRes: Int) {
        _slider.thumb = ContextCompat.getDrawable(context, drawableRes) ?: return
    }

    fun setThumbOffset(offset: Int) {
        _slider.thumbOffset = offset
    }

    override fun setEnabled(enabled: Boolean) {
        TransitionManager.beginDelayedTransition(_slider.parent as ViewGroup)
        super.setEnabled(enabled)
        _slider.isEnabled = enabled
        _sliderTitle.isEnabled = enabled
        var color = 0
        if (!enabled) {
            color = ContextCompat.getColor(context, R.color.disabled_filter)
            _sliderTitle.invisible()
        } else {
            _sliderTitle.show()
        }
        _slider.thumb?.setColorFilter(color, PorterDuff.Mode.XOR)
    }

    fun resetSlider() {
        TransitionManager.beginDelayedTransition(_slider.parent as ViewGroup)
        _actionsContainer.invisible()
        _slider.show()
        _sliderTitle.show()
    }

    //region LISTENERS
    fun onSliderPositionChange(slideChangeListener: ((position: Float) -> Unit)?) {
        _slider.onSlideChangeListener = {
            val calculatedAlpha = 1 - (it * 2) // change alpha 2x faster
            _sliderTitle.alpha = Math.min(calculatedAlpha, 1f)
            _sliderTitle.translationX = it * 300

            slideChangeListener?.invoke(it)
        }
    }

    fun onSlideComplete(completeListener: (() -> Unit)?) {
        _slider.onSlideCompleteListener = {

            TransitionManager.beginDelayedTransition(_slider.parent as ViewGroup, endTransition) // reset slider to initial position after end
            _slider.invisible()
            _sliderTitle.invisible()
            _actionsContainer.show()

            completeListener?.invoke()
        }
    }

    fun onNegativeClick(block: (() -> Unit)?) {
        this.onNegativeClickListener = block
    }

    fun onPositiveClick(block: (() -> Unit)?) {
        this.onPositiveClickListener = block
    }

    // called after showing actions container to reset slider
    private fun onTransitionEnd() {
        _slider.reset(false)
    }

    //endregion
}

