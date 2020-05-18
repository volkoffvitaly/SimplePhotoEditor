package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.zoom_fragment.*


class ZoomFragment : Fragment() {

    var ivPhoto: Bitmap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zoom_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ivPhoto == null) {
            ivPhoto = (activity!!.ivPhoto.drawable as BitmapDrawable).bitmap
            //EditorActivity.States.states.add(ivPhoto!!)
        }
        textViewZoom.text = "100% of the original"

        seekZoom.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                val temp = 100 + i
                textViewZoom.text = "$temp% of the original"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                /*fZoom(100 - seekZoom.progress)*/
                resizeBilinear()
            }
        })
    }



    private fun fZoom(percentage: Int){
        val bitmapNew = Bitmap.createBitmap((ivPhoto!!.width * percentage / 100), (ivPhoto!!.height * percentage / 100), Bitmap.Config.ARGB_8888)

        val startX: Int = (ivPhoto!!.width - (ivPhoto!!.width * percentage / 100)) / 2
        val startY: Int = (ivPhoto!!.height - (ivPhoto!!.height * percentage / 100)) / 2

        for (y in 0 until bitmapNew.height){
            for (x in 0 until bitmapNew.width){
                val oldPixel = ivPhoto!!.getPixel(startX + x, startY + y)
                bitmapNew.setPixel(x, y, oldPixel)
            }
        }

        activity!!.ivPhoto!!.setImageBitmap(bitmapNew)
    }

    fun resizeBilinear(){
        val pixels = IntArray(ivPhoto!!.width * ivPhoto!!.height)
        ivPhoto!!.getPixels(pixels, 0, ivPhoto!!.width, 0, 0, ivPhoto!!.width, ivPhoto!!.height)



        val w2 = ivPhoto!!.width * 2 // Здесь я изменил пропорции
        val h2 = ivPhoto!!.height * 8

        val scaled = Bitmap.createBitmap(
            w2, h2,
            Bitmap.Config.ARGB_8888
        )
        val cs = Canvas(scaled)


        val temp = IntArray(w2 * h2)
        var a: Int
        var b: Int
        var c: Int
        var d: Int
        var x: Int
        var y: Int
        var index: Int
        val x_ratio = (ivPhoto!!.width - 1).toFloat() / w2
        val y_ratio = (ivPhoto!!.height - 1).toFloat() / h2
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
                index = y * ivPhoto!!.width + x
                a = pixels[index]
                b = pixels[index + 1]
                c = pixels[index + ivPhoto!!.width]
                d = pixels[index + ivPhoto!!.width + 1]

                // blue element
                // Yb = Ab(1-w)(1-h) + Bb(w)(1-h) + Cb(h)(1-w) + Db(wh)
                blue =
                    (a and 0xff) * (1 - x_diff) * (1 - y_diff) + (b and 0xff) * x_diff * (1 - y_diff) + (c and 0xff) * y_diff * (1 - x_diff) + (d and 0xff) * (x_diff * y_diff)

                // green element
                // Yg = Ag(1-w)(1-h) + Bg(w)(1-h) + Cg(h)(1-w) + Dg(wh)
                green =
                    (a shr 8 and 0xff) * (1 - x_diff) * (1 - y_diff) + (b shr 8 and 0xff) * x_diff * (1 - y_diff) + (c shr 8 and 0xff) * y_diff * (1 - x_diff) + (d shr 8 and 0xff) * (x_diff * y_diff)

                // red element
                // Yr = Ar(1-w)(1-h) + Br(w)(1-h) + Cr(h)(1-w) + Dr(wh)
                red =
                    (a shr 16 and 0xff) * (1 - x_diff) * (1 - y_diff) + (b shr 16 and 0xff) * x_diff * (1 - y_diff) + (c shr 16 and 0xff) * y_diff * (1 - x_diff) + (d shr 16 and 0xff) * (x_diff * y_diff)
                temp[offset++] = -0x1000000 or  // hardcode alpha
                        (red.toInt() shl 16 and 0xff0000) or
                        (green.toInt() shl 8 and 0xff00) or
                        blue.toInt()
            }
        }

        cs.drawBitmap(temp, 0, w2, 0.0F, 0.0F, w2, h2, true, null)
        activity!!.ivPhoto!!.setImageBitmap(scaled)
    }
}
