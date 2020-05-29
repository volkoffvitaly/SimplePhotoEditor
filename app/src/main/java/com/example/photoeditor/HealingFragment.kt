package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.red
import kotlinx.android.synthetic.main.activity_editor.*
import kotlin.math.sqrt

class HealingFragment : Fragment() {

    lateinit var orig: Bitmap

    lateinit var ivPhoto: Bitmap
    lateinit var pixels: IntArray
    var radius : Int = 50

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.healing_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orig = (activity as stateChangesInterface).getIvPhoto()
        ivPhoto = (activity as stateChangesInterface).getIvPhoto()
        ivPhoto = ivPhoto.copy(Bitmap.Config.ARGB_8888, true)

        pixels = IntArray(ivPhoto.width * ivPhoto.height)
        ivPhoto.getPixels(pixels, 0, ivPhoto.width, 0, 0, ivPhoto.width, ivPhoto.height)

        activity!!.ivPhoto.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {

                val widthImageView = activity!!.ivPhoto.width.toFloat()
                val heightImageView = activity!!.ivPhoto.height.toFloat()
                val widthBitmap = (activity!!.ivPhoto.drawable as BitmapDrawable).bitmap.width.toFloat()
                val heightBitmap = (activity!!.ivPhoto.drawable as BitmapDrawable).bitmap.height.toFloat()

                val scaleFactor: Float

                val scaleFactorW = widthBitmap / widthImageView
                val scaleFactorH = heightBitmap / heightImageView

                if (scaleFactorW > scaleFactorH) {
                    scaleFactor = widthImageView / widthBitmap
                }

                else {
                    scaleFactor = heightImageView / heightBitmap
                }


                val motionTouchEventX = (event.x - (widthImageView - scaleFactor * widthBitmap) / 2.0F) / scaleFactor
                val motionTouchEventY = (event.y - (heightImageView - scaleFactor * heightBitmap) / 2.0F) / scaleFactor

                healing(motionTouchEventX.toInt(), motionTouchEventY.toInt(), ivPhoto!!)

                Toast.makeText(activity, "$motionTouchEventX, $motionTouchEventY", Toast.LENGTH_SHORT).show()

                activity!!.ivPhoto.setImageBitmap(ivPhoto)
            }

            true
        }

    }

    private fun healing (x : Int, y : Int, bitmap: Bitmap) {

        //val oldPixel = bitmap.getPixel(x, y)

        var r = 0
        var g = 0
        var b = 0
        var count = 0


        for (yTemp in (y-radius)..(y+radius)) {
            if (yTemp < 0 || yTemp >= bitmap.height) continue

            for (xTemp in (x-radius)..(x+radius)) {
                if (xTemp < 0 || xTemp >= bitmap.width) continue
                if (sqrt((((xTemp - x) * (xTemp - x)) + ((yTemp - y) * (yTemp - y))).toDouble()).toInt() > radius) continue

                val oldPixel = orig.getPixel(xTemp, yTemp)
                r += Color.red(oldPixel)
                g += Color.green(oldPixel)
                b += Color.blue(oldPixel)
                count++
            }
        }

        if (count == 0) return

        r /= count
        g /= count
        b /= count

        for (yTemp in (y-radius)..(y+radius)) {
            if (yTemp < 0 || yTemp >= bitmap.height) continue

            for (xTemp in (x-radius)..(x+radius)) {
                if (xTemp < 0 || xTemp >= bitmap.width) continue
                if (sqrt((((xTemp - x) * (xTemp - x)) + ((yTemp - y) * (yTemp - y))).toDouble()).toInt() > radius) continue

                val oldPixel = orig.getPixel(xTemp, yTemp)
                val red = Color.red(oldPixel) + ((r - Color.red(oldPixel))*0.2).toInt()
                val green = Color.green(oldPixel) + ((r - Color.green(oldPixel))*0.2).toInt()
                val blue = Color.blue(oldPixel) + ((r - Color.blue(oldPixel))*0.2).toInt()

                ivPhoto.setPixel(xTemp, yTemp, Color.rgb(red, green, blue))
            }
        }
    }


}


