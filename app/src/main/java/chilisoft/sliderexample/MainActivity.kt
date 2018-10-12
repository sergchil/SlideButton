package chilisoft.sliderexample

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = progress
        val slideButton = slide_button
        val switchCompat = switch_

        slideButton.onSlideComplete {
            Toast.makeText(this@MainActivity, "UNLOCKED", Toast.LENGTH_SHORT).show()
        }

        slideButton.onSliderPositionChange { position ->
            @SuppressLint("SetTextI18n")
            textView.text = "Progress: $position"
        }

        slideButton.onPositiveClick {
            //            slideButton.toggleActions()
        }

        slideButton.onNegativeClick {
            slideButton.resetSlider()
        }

        switchCompat.setOnCheckedChangeListener { compoundButton, b ->
            slideButton.isEnabled = b
//            slideButton.toggleActions()
        }
    }
}
