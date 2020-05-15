package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
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
                doAsync {
                    uiThread {
                        if (seekAngle.progress != 180) {
                            showConfirmBar()
                        }
                        else activity!!.confirmBar!!.visibility = View.INVISIBLE
                        activity!!.progressLoading!!.visibility = View.VISIBLE
                    }

                    val tempBitmap = rotateImage(seekAngle.progress.toDouble() - 180)

                    uiThread {
                        activity!!.ivPhoto!!.setImageBitmap(tempBitmap)
                        activity!!.progressLoading!!.visibility = View.INVISIBLE
                    }
                }
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
        if (activity!!.confirmBar!!.visibility == View.INVISIBLE)
            activity!!.confirmBar!!.visibility = View.VISIBLE
    }


    private fun rotateImage(degrees: Double): Bitmap {
        val radians = (degrees  * Math.PI) / 180.0
        val rotatedMatrix = Matrix()

        val rotateArr: FloatArray = floatArrayOf(
            cos(radians).toFloat(),
            -sin(radians).toFloat(),
            ivPhoto!!.width.toFloat() / 2,
            sin(radians).toFloat(),
            cos(radians).toFloat(),
            ivPhoto!!.height.toFloat() / 2,
            0.0f, 0.0f, 1.0f
        )

        rotatedMatrix.setValues(rotateArr)


        return Bitmap.createBitmap(
            ivPhoto!!,
            0, 0,
            ivPhoto!!.width, ivPhoto!!.height,
            rotatedMatrix, false
        )
    }
}
