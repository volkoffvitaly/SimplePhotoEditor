package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.rotate_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.math.cos
import kotlin.math.sin

class RotateFragment : Fragment() {

    private lateinit var ivPhoto: Bitmap
    private var currentValue = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.rotate_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivPhoto = (activity as stateChangesInterface).getIvPhoto()

        seekAngle.progress = 45
        textViewAngles.text = "0°"


        bRightRotate.setOnClickListener {
            currentValue += 90
            currentValue %= 360
            makeChanges(currentValue)
        }

        bLeftRotate.setOnClickListener {
            currentValue -= 90
            currentValue %= 360
            makeChanges(currentValue)
        }

        seekAngle.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                val temp = i - 45
                textViewAngles.text = "$temp°"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                currentValue -= seekAngle.progress - 45
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                currentValue += seekAngle.progress - 45
                currentValue %= 360
                makeChanges(currentValue)
            }
        })

        activity!!.bConfirm!!.setOnClickListener{
            ivPhoto = (activity as stateChangesInterface).getIvPhoto()

            (activity as stateChangesInterface).stateOfConfirmBar(false)
            (activity as stateChangesInterface).stateOfTopBar(true)

            seekAngle.progress = 45
            currentValue = 0
        }

        activity!!.bCancel!!.setOnClickListener{
            (activity as stateChangesInterface).changeIvPhoto(ivPhoto)

            (activity as stateChangesInterface).stateOfConfirmBar(false)
            (activity as stateChangesInterface).stateOfTopBar(true)

            seekAngle.progress = 45
            currentValue = 0
        }
    }

    private fun makeChanges(value: Int) {
        if (value != 0) {
            doAsync {
                uiThread {
                    (activity as stateChangesInterface).stateOfConfirmBar(true)
                    (activity as stateChangesInterface).stateOfConfirmBarButtons(false)
                    (activity as stateChangesInterface).stateOfTopBar(false)
                    (activity as stateChangesInterface).stateOfProgressLoading(true)

                    seekAngle.isEnabled = false
                }

                val tempBitmap = rotateImage(value.toDouble())

                uiThread {
                    (activity as stateChangesInterface).changeIvPhoto(tempBitmap)

                    (activity as stateChangesInterface).stateOfConfirmBarButtons(true)
                    (activity as stateChangesInterface).stateOfProgressLoading(false)

                    seekAngle.isEnabled = true
                    bLeftRotate.isEnabled = true
                    bRightRotate.isEnabled = true
                }
            }
        }

        else {
            activity!!.ivPhoto!!.setImageBitmap(ivPhoto)

            (activity as stateChangesInterface).stateOfConfirmBar(false)
            (activity as stateChangesInterface).stateOfTopBar(true)
        }
    }

    private fun rotateImage(degrees: Double): Bitmap {

        val radians = (degrees * Math.PI) / 180.0
        val rotatedMatrix = Matrix()

        val rotateArr: FloatArray = floatArrayOf(
            // First stroke //
            cos(radians).toFloat(),
            -sin(radians).toFloat(),
            ivPhoto.width.toFloat() / 2,
            // Second stroke //
            sin(radians).toFloat(),
            cos(radians).toFloat(),
            ivPhoto.height.toFloat() / 2,
            // Third stroke //
            0.0f,
            0.0f,
            1.0f
        )

        rotatedMatrix.setValues(rotateArr)


        return Bitmap.createBitmap(
            ivPhoto,
            0, 0,
            ivPhoto.width, ivPhoto.height,
            rotatedMatrix, false
        )
    }


      // Deprecated //
   /* private fun rotateImage(angle: Double) {

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
    }*/
       // Deprecated //
}
