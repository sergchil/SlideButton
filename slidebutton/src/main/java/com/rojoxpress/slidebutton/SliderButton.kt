package com.rojoxpress.slidebutton

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.slider_main.view.*

class SliderButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var _texView: TextView
    private var _slider: Slider

    private var offsetThumb: Int = 0

    init {

        val parent = LayoutInflater.from(context).inflate(R.layout.slider_main, this, true)

        _slider = parent.__slider
        _texView = parent.__slider_title

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SliderButton, 0, 0)

            if (a.hasValue(R.styleable.SliderButton_text)) {
                val buttonText = a.getString(R.styleable.SliderButton_text)
                setText(buttonText)
            }

            if (a.hasValue(R.styleable.SliderButton_thumb)) {
                val thumbDrawable: Drawable? = a.getDrawable(R.styleable.SliderButton_thumb)
                _slider.thumb = thumbDrawable
            }

            if (a.hasValue(R.styleable.SliderButton_thumbOffset)) {
                val offset = a.getDimensionPixelSize(R.styleable.SliderButton_thumbOffset, 10.dp)
                offsetThumb += offset
            }

            if (a.hasValue(R.styleable.SliderButton_sliderBackground)) {
                ViewCompat.setBackground(this, a.getDrawable(R.styleable.SliderButton_sliderBackground))
            } else {
                ViewCompat.setBackground(this, ContextCompat.getDrawable(context, R.drawable.back_slide_button))
            }

            val unitsTextSize = a.getDimensionPixelSize(R.styleable.SliderButton_textSize, 20.dp).toFloat()

            _texView.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitsTextSize)

            val color = a.getColor(R.styleable.SliderButton_textColor, Color.WHITE)
            _texView.setTextColor(color)

            a.recycle()
        }

//        setThumbOffset(offsetThumb)

    }

    fun setText(@StringRes res: Int) {
        _texView.setText(res)
    }

    fun setText(charSequence: CharSequence?) {
        _texView.text = charSequence
    }

    fun setThumb(drawable: Drawable) {
        _slider.thumb = drawable
    }

    fun setThumbOffset(offset: Int) {
        _slider.thumbOffset = offset
    }


    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        _slider.isEnabled = enabled
        _texView.isEnabled = enabled
        var color = 0
        if (!enabled) {
            color = ContextCompat.getColor(context, R.color.disabled_filter)
            _texView.visibility = View.GONE
        } else {
            _texView.visibility = View.VISIBLE
        }
        _slider.thumb?.setColorFilter(color, PorterDuff.Mode.XOR)
    }


    fun onSliderPositionChange(slideChangeListener: ((position: Float) -> Unit)?) {
        _slider.slideChangeListener = {
            _texView.alpha = Math.min(1 - (it * 2), 1f)
            _texView.translationX = it * 300


            slideChangeListener?.invoke(it)
        }
    }

    fun onSlideComplete(completeListener: (() -> Unit)?) {
        _slider.onSlideComplete = completeListener
    }

}

// dp to pixels
val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

// float dp to pixels
val Float.dp: Int get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
