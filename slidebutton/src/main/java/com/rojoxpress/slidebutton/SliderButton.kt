package com.rojoxpress.slidebutton

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.*
import android.support.v4.content.ContextCompat
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

    private var onPositiveClickListener: (() -> Unit)? = null
    private var onNegativeClickListener: (() -> Unit)? = null
    private val endTransition = with(AutoTransition()) {
        // called after showing actions container to reset slider
        addListener(SimpleTransitionEndListener(this@SliderButton::onTransitionEnd))
    }

    private val _sliderTitle: TextView
    private val _slider: Slider
    private val _actionsContainer: ViewGroup
    private val _positiveButton: Button
    private val _negativeButton: Button

    //region SLIDER DEFAULTS
    private val _defaultSliderText = ""
    private val _defaultSliderTextColor = Color.WHITE
    private val _defaultSliderTextSize = 20.sp.toFloat()
    private val _defaultSliderHeight = 50.dp.toFloat()
    private val _defaultSliderBg = ContextCompat.getDrawable(context, R.drawable.default_slider_bg)
    private val _defaultSliderThumb = ContextCompat.getDrawable(context, R.drawable.default_slider_thumb)
    private val _defaultSliderProgress = ContextCompat.getDrawable(context, R.drawable.default_slider_progress) // not customizable
    private val _defaultSliderThumbOffset = 0f

    //endregion

    //region POSITIVE BUTTON DEFAULTS
    private val _defaultPositiveButtonText = ""
    private val _defaultPositiveButtonTextColor = Color.WHITE
    private val _defaultPositiveButtonTextSize = 20.sp.toFloat()
    private val _defaultPositiveButtonBg = ColorDrawable(ContextCompat.getColor(context, R.color.positive_button_bg_color))
    private val _defaultPositiveButtonBgColor = ContextCompat.getColor(context, R.color.positive_button_bg_color)
    //endregion

    //region NEGATIVE BUTTON DEFAULTS
    private val _defaultNegativeButtonText = ""
    private val _defaultNegativeButtonTextColor = Color.WHITE
    private val _defaultNegativeButtonTextSize = 20.sp.toFloat()
    private val _defaultNegativeButtonBg = ColorDrawable(ContextCompat.getColor(context, R.color.negative_button_bg_color))
    private val _defaultNegativeButtonBgColor = ContextCompat.getColor(context, R.color.negative_button_bg_color)
    //endregion

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
            val a = context.obtainStyledAttributes(attrs, R.styleable.SliderButton, defStyleAttr, 0)

            setSliderText(a.getString(R.styleable.SliderButton_sliderText))
            setSliderTextSize(a.getDimension(R.styleable.SliderButton_sliderTextSize, _defaultSliderTextSize))
            setSliderTextColor(a.getColor(R.styleable.SliderButton_sliderTextColor, _defaultSliderTextColor))
            setSliderHeight(a.getDimension(R.styleable.SliderButton_sliderHeight, _defaultSliderHeight).toInt())
            setSliderBackgroundDrawable(a.getDrawable(R.styleable.SliderButton_sliderBackground))
            setSliderThumbDrawable(a.getDrawable(R.styleable.SliderButton_sliderThumbDrawable))
            _slider.progressDrawable = _defaultSliderProgress // not customizable
            setSliderThumbOffset(a.getDimension(R.styleable.SliderButton_sliderThumbOffset, _defaultSliderThumbOffset).toInt()) // should be called after setting drawable

            setPositiveButtonText(a.getString(R.styleable.SliderButton_positiveButtonText))
            setPositiveButtonTextSize(a.getDimension(R.styleable.SliderButton_positiveButtonTextSize, _defaultPositiveButtonTextSize))
            setPositiveButtonTextColor(a.getColor(R.styleable.SliderButton_positiveButtonTextColor, _defaultPositiveButtonTextColor))
            setPositiveButtonBackgroundDrawable(a.getDrawable(R.styleable.SliderButton_positiveButtonBackground))

            setNegativeButtonText(a.getString(R.styleable.SliderButton_negativeButtonText))
            setNegativeButtonTextSize(a.getDimension(R.styleable.SliderButton_negativeButtonTextSize, _defaultNegativeButtonTextSize))
            setNegativeButtonTextColor(a.getColor(R.styleable.SliderButton_negativeButtonTextColor, _defaultNegativeButtonTextColor))
            setNegativeButtonBackgroundDrawable(a.getDrawable(R.styleable.SliderButton_negativeButtonBackground))

            setCompleteThreshold(a.getInteger(R.styleable.SliderButton_completionThreshold, _slider.COMPLETE_TRESHOLD))


            a.recycle()
        }
    }

    //region SLIDER
    fun setSliderText(@StringRes res: Int) {
        _sliderTitle.setText(res)
    }

    fun setSliderText(charSequence: CharSequence?) {
        _sliderTitle.text = charSequence ?: _defaultSliderText
    }

    fun setSliderTextSize(size: Float) {
        _sliderTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }

    fun setSliderTextColorRes(@ColorRes resId: Int) {
        _sliderTitle.setTextColor(ContextCompat.getColor(context, resId))
    }

    fun setSliderTextColor(@ColorInt color: Int) {
        _sliderTitle.setTextColor(color)
    }

    fun setSliderThumbDrawable(drawable: Drawable?) {
        _slider.thumb = drawable ?: _defaultSliderThumb
    }

    fun setSliderThumbDrawableRes(@DrawableRes drawableRes: Int) {
        _slider.thumb = ContextCompat.getDrawable(context, drawableRes) ?: return
    }

    fun setSliderThumbOffset(offset: Int) {
        _slider.thumbOffset = offset
    }

    fun setSliderBackgroundDrawable(drawable: Drawable?) {
        _slider.background = drawable ?: _defaultSliderBg
    }

    fun setSliderBackgroundColor(color: Int) {
        _slider.setBackgroundColor(color)
    }

    fun setSliderHeight(height: Int) {
        _slider.setHeight(height)
        _actionsContainer.setHeight(height)
    }
    //endregion

    //region POSITIVE BUTTON
    fun setPositiveButtonText(@StringRes res: Int) {
        _positiveButton.setText(res)
    }

    fun setPositiveButtonText(charSequence: String?) {
        _positiveButton.text = charSequence ?: _defaultPositiveButtonText
    }

    // pixel
    fun setPositiveButtonTextSize(size: Float) {
        _positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }

    fun setPositiveButtonTextColor(@ColorInt color: Int) {
        _positiveButton.setTextColor(color)
    }

    fun setPositiveButtonBackgroundDrawable(drawable: Drawable?) {
        _positiveButton.background = drawable ?: _defaultPositiveButtonBg
    }

    fun setPositiveButtonBackgroundColor(@ColorInt color: Int) {
        _positiveButton.setBackgroundColor(color)
    }

    //endregion

    //region NEGATIVE BUTTON
    fun setNegativeButtonText(@StringRes res: Int) {
        _negativeButton.setText(res)
    }

    fun setNegativeButtonText(charSequence: String?) {
        _negativeButton.text = charSequence ?: _defaultNegativeButtonText
    }

    // pixel
    fun setNegativeButtonTextSize(size: Float) {
        _negativeButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }

    fun setNegativeButtonTextColor(@ColorInt color: Int) {
        _negativeButton.setTextColor(color)
    }

    fun setNegativeButtonBackgroundDrawable(drawable: Drawable?) {
        _negativeButton.background = drawable ?: _defaultNegativeButtonBg
    }

    fun setNegativeButtonBackgroundColor(@ColorInt color: Int) {
        _negativeButton.setBackgroundColor(color)
    }


    //endregion

    fun setCompleteThreshold(threshold: Int) {
        _slider.COMPLETE_TRESHOLD = threshold
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

