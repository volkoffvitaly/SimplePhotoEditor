package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.rotate_fragment.*
import kotlin.math.cos
import kotlin.math.sin

class RotateFragment : Fragment() {

    lateinit var ivPhoto: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.rotate_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivPhoto = activity!!.ivPhoto

        seekAngle.progress = 180
        textViewAngles.text = "0°"

        seekAngle.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                val temp = i - 180
                textViewAngles.text = "$temp°"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                rotateImage(seekAngle.progress.toDouble() - 180)
            }
        })
    }

    fun rotateImage(angle: Double) {
        val oldBitmap = (ivPhoto.drawable as BitmapDrawable).bitmap
        val newBitmap = Bitmap.createBitmap(oldBitmap.width, oldBitmap.height, Bitmap.Config.ARGB_8888)

        val sinAngle: Double = sin(Math.toRadians(angle))
        val cosAngle: Double = cos(Math.toRadians(angle))
        val xCenter: Double = 0.5 * (oldBitmap.width - 1)
        val yCenter: Double = 0.5 * (oldBitmap.height - 1)

        for (y in 0 until oldBitmap.height) {
            for (x in 0 until oldBitmap.width) {

                val a = x - xCenter
                val b = y - yCenter

                val xNew = (a * cosAngle - b * sinAngle + xCenter).toInt()
                val yNew = (a * sinAngle + b * cosAngle + yCenter).toInt()

                if (0 <= xNew && xNew < oldBitmap.width && 0 <= yNew && yNew < oldBitmap.height)
                    newBitmap.setPixel(x, y, oldBitmap.getPixel(xNew, yNew))
            }
        }

        ivPhoto.setImageBitmap(newBitmap)
    }
}
