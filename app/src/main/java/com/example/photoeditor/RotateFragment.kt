package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.rotate_fragment.*
import kotlin.math.cos
import kotlin.math.sin


class RotateFragment : Fragment() {

    private var ivPhoto: Bitmap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.rotate_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ivPhoto == null) {
            ivPhoto = (activity!!.ivPhoto.drawable as BitmapDrawable).bitmap
            //EditorActivity.States.states.add(ivPhoto!!)
        }

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
                if (seekAngle.progress != 180) {
                    showConfirmBar()
                } else activity!!.confirmBar!!.visibility = View.INVISIBLE
            }
        })

        activity!!.bConfirm!!.setOnClickListener(){
            ivPhoto = (activity!!.ivPhoto.drawable as BitmapDrawable).bitmap
            activity!!.confirmBar!!.visibility = View.INVISIBLE
            seekAngle.progress = 180
        }

        activity!!.bCancel!!.setOnClickListener(){
            activity!!.ivPhoto!!.setImageBitmap(ivPhoto)
            activity!!.confirmBar!!.visibility = View.INVISIBLE
            seekAngle.progress = 180
        }
    }

    private fun showConfirmBar() {
        if (activity!!.confirmBar!!.visibility == View.INVISIBLE){
            activity!!.confirmBar!!.visibility = View.VISIBLE
        }
    }

    private fun rotateImage(angle: Double) {

        val xCenter: Double = 0.5 * (ivPhoto!!.width - 1)
        val yCenter: Double = 0.5 * (ivPhoto!!.height - 1)
        val sinAngle: Double = sin(Math.toRadians(angle))
        val cosAngle: Double = cos(Math.toRadians(angle))

        val newBitmap = Bitmap.createBitmap(ivPhoto!!.width, ivPhoto!!.height, Bitmap.Config.ARGB_8888)

        for (y in 0 until ivPhoto!!.height) {
            for (x in 0 until ivPhoto!!.width) {

                val a = x - xCenter
                val b = y - yCenter

                val xNew = (a * cosAngle - b * sinAngle + xCenter).toInt()
                val yNew = (a * sinAngle + b * cosAngle + yCenter).toInt()

                if (0 <= xNew && xNew < ivPhoto!!.width && 0 <= yNew && yNew < ivPhoto!!.height)
                    newBitmap.setPixel(x, y, ivPhoto!!.getPixel(xNew, yNew))
            }
        }

        activity!!.ivPhoto!!.setImageBitmap(newBitmap)
    }
}
