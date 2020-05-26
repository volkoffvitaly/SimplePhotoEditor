package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.rotate_fragment.*
import kotlinx.android.synthetic.main.zoom_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class ZoomFragment : Fragment() {

    var ivPhoto: Bitmap? = null

    val alpha = -0x1000000

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zoom_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ivPhoto == null) {
            ivPhoto = (activity!!.ivPhoto.drawable as BitmapDrawable).bitmap
        }
        textViewZoom.text = "100% of image"

        seekZoom.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                val temp = 100 + i
                textViewZoom.text = "$temp% of image"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                if (seekZoom.progress != 0) {
                    doAsync {
                        uiThread {
                            (activity as stateChangesInterface).stateOfConfirmBar(true)
                            (activity as stateChangesInterface).stateOfConfirmBarButtons(false)
                            (activity as stateChangesInterface).stateOfTopBar(false)
                            (activity as stateChangesInterface).stateOfProgressLoading(true)

                            seekZoom.isEnabled = false
                        }

                        val zoomedBitmap = zoom(100 - seekZoom.progress)
                        val resizedBitmap = bilinearResize(zoomedBitmap, 100.0 / (100 - seekZoom.progress))

                        uiThread {
                            activity!!.ivPhoto!!.setImageBitmap(resizedBitmap)

                            (activity as stateChangesInterface).stateOfConfirmBarButtons(true)
                            (activity as stateChangesInterface).stateOfProgressLoading(false)

                            seekZoom.isEnabled = true
                        }
                    }
                } else {
                    activity!!.ivPhoto!!.setImageBitmap(ivPhoto)

                    (activity as stateChangesInterface).stateOfConfirmBar(false)
                    (activity as stateChangesInterface).stateOfTopBar(true)
                }
            }
        })

        activity!!.bConfirm!!.setOnClickListener(){
            ivPhoto = (activity!!.ivPhoto.drawable as BitmapDrawable).bitmap

            (activity as stateChangesInterface).stateOfConfirmBar(false)
            (activity as stateChangesInterface).stateOfTopBar(true)

            seekZoom.progress = 0
        }

        activity!!.bCancel!!.setOnClickListener(){
            (activity as stateChangesInterface).changeIvPhoto(ivPhoto!!)

            (activity as stateChangesInterface).stateOfConfirmBar(false)
            (activity as stateChangesInterface).stateOfTopBar(true)

            seekZoom.progress = 0
        }
    }



    private fun zoom(percentage: Int): Bitmap {
        val newWidth = (ivPhoto!!.width * percentage / 100)
        val newHeight = (ivPhoto!!.height * percentage / 100)

        val oldPixels = IntArray(ivPhoto!!.width * ivPhoto!!.height)
        ivPhoto!!.getPixels(oldPixels, 0, ivPhoto!!.width, 0, 0, ivPhoto!!.width, ivPhoto!!.height)

        var offset = 0
        val newPixels = IntArray(newWidth * newHeight)

        val startX: Int = (ivPhoto!!.width - newWidth) / 2
        val startY: Int = (ivPhoto!!.height - newHeight) / 2

        for (y in 0 until newHeight){
            for (x in 0 until newWidth){
                newPixels[offset++] = oldPixels[ivPhoto!!.width * (startY + y) + (startX + x)]
            }
        }

        return Bitmap.createBitmap(newPixels, newWidth, newHeight, Bitmap.Config.ARGB_8888)
    }


    fun bilinearResize(bitmap: Bitmap, k: Double): Bitmap {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)


        val w2 = (bitmap.width * k).toInt()
        val h2 = (bitmap.height * k).toInt()
        val temp = IntArray(w2 * h2)


        var a: Int
        var b: Int
        var c: Int
        var d: Int
        var x: Int
        var y: Int
        var index: Int
        val x_ratio = (bitmap.width - 1).toFloat() / w2
        val y_ratio = (bitmap.height - 1).toFloat() / h2
        var x_diff: Float
        var y_diff: Float
        var blue: Float
        var red: Float
        var green: Float
        var offset = 0

        for (i in 0 until h2) {
            for (j in 0 until w2) {
                x = (x_ratio * j).toInt()
                y = (y_ratio * i).toInt()
                x_diff = x_ratio * j - x
                y_diff = y_ratio * i - y
                index = y * bitmap.width + x
                a = pixels[index]
                b = pixels[index + 1]
                c = pixels[index + bitmap.width]
                d = pixels[index + bitmap.width + 1]

                blue = (a and 0xff) * (1 - x_diff) * (1 - y_diff) + (b and 0xff) * x_diff * (1 - y_diff) + (c and 0xff) * y_diff * (1 - x_diff) + (d and 0xff) * (x_diff * y_diff)
                green = (a shr 8 and 0xff) * (1 - x_diff) * (1 - y_diff) + (b shr 8 and 0xff) * x_diff * (1 - y_diff) + (c shr 8 and 0xff) * y_diff * (1 - x_diff) + (d shr 8 and 0xff) * (x_diff * y_diff)
                red = (a shr 16 and 0xff) * (1 - x_diff) * (1 - y_diff) + (b shr 16 and 0xff) * x_diff * (1 - y_diff) + (c shr 16 and 0xff) * y_diff * (1 - x_diff) + (d shr 16 and 0xff) * (x_diff * y_diff)

                temp[offset++] = alpha or (red.toInt() shl 16 and 0xff0000) or (green.toInt() shl 8 and 0xff00) or blue.toInt()
            }
        }

        return Bitmap.createBitmap(temp, w2, h2, Bitmap.Config.ARGB_8888)
    }
}
