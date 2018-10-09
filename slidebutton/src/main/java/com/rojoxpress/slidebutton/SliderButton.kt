package com.rojoxpress.slidebutton

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.AppCompatSeekBar
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView

open class SliderButton : FrameLayout {

    private lateinit var texView: TextView
    private lateinit var slideBar: SlideBar
    private var listener: SlideButtonListener? = null
    private var slideChangeListener: OnSlideChangeListener? = null
    private var offsetThumb: Int = 0

    constructor(context: Context) : super(context) {
        init(null)

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)

    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }


    fun dpToPixels(dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    fun init(set: AttributeSet?) {

        offsetThumb = dpToPixels(16)
        texView = TextView(context)
        slideBar = SlideBar(context)

        val childParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        childParams.gravity = Gravity.CENTER

        slideBar.layoutParams = childParams
        texView.layoutParams = childParams
        slideBar.progressDrawable = ContextCompat.getDrawable(context, R.drawable.back_slide_layer)


        texView.gravity = Gravity.CENTER

        if (set != null) {
            val a = context.obtainStyledAttributes(set, R.styleable.SliderButton, 0, 0)

            if (a.hasValue(R.styleable.SliderButton_text)) {
                val buttonText = a.getString(R.styleable.SliderButton_text)
                setText(buttonText)
            }

            if (a.hasValue(R.styleable.SliderButton_thumb)) {
                val thumbDrawable: Drawable?
                thumbDrawable = a.getDrawable(R.styleable.SliderButton_thumb)
                slideBar.thumb = thumbDrawable
            } else {
                slideBar.thumb = ContextCompat.getDrawable(context, R.drawable.thumb_def)
            }

            if (a.hasValue(R.styleable.SliderButton_thumbOffset)) {
                val offset = a.getDimensionPixelSize(R.styleable.SliderButton_thumbOffset, dpToPixels(10))
                offsetThumb += offset
            }

            if (a.hasValue(R.styleable.SliderButton_sliderBackground)) {
                ViewCompat.setBackground(this, a.getDrawable(R.styleable.SliderButton_sliderBackground))
            } else {
                ViewCompat.setBackground(this, ContextCompat.getDrawable(context, R.drawable.back_slide_button))
            }

            val unitsTextSize = a.getDimensionPixelSize(R.styleable.SliderButton_textSize, dpToPixels(20)).toFloat()

            texView.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitsTextSize)

            val color = a.getColor(R.styleable.SliderButton_textColor, Color.WHITE)
            texView.setTextColor(color)

            a.recycle()
        }

        setThumbOffset(offsetThumb)

        /*post(new Runnable() {
            @Override
            public void run() {
                Drawable drawable = slideBar.thumb;
                if(drawable != null){
                    Drawable n = new ScaleDrawable(drawable,Gravity.CENTER,getHeight(),getHeight());
                    slideBar.setThumb(n);
                }
            }
        });*/

        this.addView(texView)
        this.addView(slideBar)
    }


    fun setText(@StringRes res: Int) {
        texView.setText(res)
    }

    fun setText(charSequence: CharSequence?) {
        texView.text = charSequence
    }

    fun setThumb(drawable: Drawable) {
        slideBar.thumb = drawable
    }

    fun setThumbOffset(offset: Int) {
        slideBar.thumbOffset = offset
    }

    fun setOnSlideChangeListener(slideChangeListener: OnSlideChangeListener) {
        this.slideChangeListener = slideChangeListener
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        slideBar.isEnabled = enabled
        texView.isEnabled = enabled
        var color = 0
        if (!enabled) {
            color = ContextCompat.getColor(context, R.color.disabled_filter)
            texView.visibility = View.GONE
        } else {
            texView.visibility = View.VISIBLE
        }
        slideBar.thumb!!.setColorFilter(color, PorterDuff.Mode.XOR)

    }


    protected inner class SlideBar : AppCompatSeekBar {

        private var thumb: Drawable? = null

        private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                onSlideChange(i.toFloat() / max)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        }

        constructor(context: Context) : super(context) {
            init()
        }


        constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
            init()
        }

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
            init()
        }

        override fun setThumb(thumb: Drawable?) {
            super.setThumb(thumb)
            this.thumb = thumb
        }

        override fun getThumb(): Drawable? {
            return thumb
        }

        fun init() {
            max = 100
            setOnSeekBarChangeListener(seekBarChangeListener)
        }


        override fun onTouchEvent(event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (thumb!!.bounds.contains(event.x.toInt(), event.y.toInt())) {
                    super.onTouchEvent(event)
                } else
                    return false
            } else if (event.action == MotionEvent.ACTION_UP) {
                if (progress > 90)
                    onSlide()

                progress = 0
            } else
                super.onTouchEvent(event)

            return true
        }

        private fun onSlide() {
            if (listener != null) {
                listener!!.onSlide()
            }
        }

        private fun onSlideChange(position: Float) {
            if (slideChangeListener != null) {
                slideChangeListener!!.onSlideChange(position)
            }
        }

    }

    fun setSlideButtonListener(listener: SlideButtonListener) {
        this.listener = listener
    }

    interface SlideButtonListener {
        fun onSlide()
    }

    interface OnSlideChangeListener {
        fun onSlideChange(position: Float)
    }

}
