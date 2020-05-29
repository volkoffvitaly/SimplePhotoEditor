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

    private fun healing (x: Int, y: Int, bitmap: Bitmap) {

        var redAverage = 0.0
        var greenAverage = 0.0
        var blueAverage = 0.0
        var hits = 0.0


        for (yTemp in (y - radius) until (y + radius)) {
            if (yTemp < 0 || yTemp >= bitmap.height)
                continue

            for (xTemp in (x - radius) until (x + radius)) {

                if (xTemp < 0 || xTemp >= bitmap.width) {
                    continue
                }
                if (sqrt((((xTemp - x) * (xTemp - x)) + ((yTemp - y) * (yTemp - y))).toDouble()).toInt() > radius) {
                    continue
                }

                val oldPixel = ivPhoto.getPixel(xTemp, yTemp)

                redAverage += (oldPixel shr 16 and 0xff)
                greenAverage += (oldPixel shr 8 and 0xff)
                blueAverage += (oldPixel and 0xff)

                hits++
            }
        }

        if (hits == 0.0)
            return

        redAverage /= hits
        greenAverage /= hits
        blueAverage /= hits

        var coef : Double

        for (yTemp in (y - radius) until (y + radius)) {

            if (yTemp < 0 || yTemp >= bitmap.height)
                continue

            for (xTemp in (x - radius)..(x + radius)) {

                if (xTemp < 0 || xTemp >= bitmap.width)
                    continue
                if (sqrt((((xTemp - x) * (xTemp - x)) + ((yTemp - y) * (yTemp - y))).toDouble()).toInt() >= radius)
                    continue

                coef = 1.0 - sqrt((((xTemp - x) * (xTemp - x)) + ((yTemp - y) * (yTemp - y))).toDouble()) / radius
                coef *= 0.5

                val oldPixel = ivPhoto.getPixel(xTemp, yTemp)
                val red: Int
                val green: Int
                val blue: Int


                if (redAverage > Color.red(oldPixel)) {
                    red = Color.red(oldPixel) + ((redAverage - Color.red(oldPixel)) * coef).toInt()
                }
                else if (redAverage < Color.red(oldPixel)) {
                    red = Color.red(oldPixel) - ((Color.red(oldPixel) - redAverage) * coef).toInt()
                }
                else {
                    red = Color.red(oldPixel)
                }


                if (greenAverage > Color.green(oldPixel)){
                    green = Color.green(oldPixel) + ((greenAverage - Color.green(oldPixel)) * coef).toInt()
                }
                else if (greenAverage < Color.green(oldPixel)) {
                    green = Color.green(oldPixel) - ((Color.green(oldPixel) - greenAverage) * coef).toInt()
                }
                else {
                    green = Color.green(oldPixel)
                }


                if (blueAverage > Color.blue(oldPixel)) {
                    blue = Color.blue(oldPixel) + ((blueAverage - Color.blue(oldPixel)) * coef).toInt()
                }
                else if (blueAverage < Color.blue(oldPixel)) {
                    blue = Color.blue(oldPixel) - ((Color.blue(oldPixel) - blueAverage) * coef).toInt()
                }
                else {
                    blue = Color.blue(oldPixel)
                }

                ivPhoto.setPixel(xTemp, yTemp, Color.rgb(red, green, blue))
            }
        }
    }


}