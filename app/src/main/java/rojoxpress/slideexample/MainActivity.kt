package rojoxpress.slideexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.rojoxpress.slidebutton.SliderButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = progress
        val slideButton = slide_button
        val switchCompat = switch_

        slideButton.setSlideButtonListener(object : SliderButton.OnSlideCompleteListener {
            override fun onSlideComplete() {
                Toast.makeText(this@MainActivity, "UNLOCKED", Toast.LENGTH_SHORT).show()
            }
        })

        slideButton.setOnSlideChangeListener(object : SliderButton.OnSlideChangeListener {
            override fun onSlideChange(position: Float) {
                textView.text = "Progress: $position"
            }
        })

        switchCompat.setOnCheckedChangeListener { compoundButton, b -> slideButton.isEnabled = b }
    }
}
