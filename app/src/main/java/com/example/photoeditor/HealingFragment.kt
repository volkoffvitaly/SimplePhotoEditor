package com.example.photoeditor


import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_editor.*
import kotlin.math.sqrt


class HealingFragment : Fragment() {

    private lateinit var ivPhoto: Bitmap
    private var radius : Int = 50
    private var amount: Double = 0.5


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.healing_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivPhoto = (activity as stateChangesInterface).getIvPhoto()
        ivPhoto = ivPhoto.copy(Bitmap.Config.ARGB_8888, true)


        activity!!.ivPhoto.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                val scaleFactor: Float

                val widthImageView = activity!!.ivPhoto.width.toFloat()
                val heightImageView = activity!!.ivPhoto.height.toFloat()
                val widthBitmap = (activity!!.ivPhoto.drawable as BitmapDrawable).bitmap.width.toFloat()
                val heightBitmap = (activity!!.ivPhoto.drawable as BitmapDrawable).bitmap.height.toFloat()

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

                healing(motionTouchEventX.toInt(), motionTouchEventY.toInt(), ivPhoto)
                activity!!.ivPhoto.setImageBitmap(ivPhoto)
            }

            true
        }
    }

    private fun healing (xCenter: Int, yCenter: Int, bitmap: Bitmap) {

        var oldPixel: Int
        var redAverage = 0.0
        var greenAverage = 0.0
        var blueAverage = 0.0
        var hits = 0


        for (y in (yCenter - radius) until (yCenter + radius)) {

            if (y < 0 || y >= bitmap.height) {
                continue
            }

            for (x in (xCenter - radius) until (xCenter + radius)) {

                if (x < 0 || x >= bitmap.width) {
                    continue
                }

                if (sqrt((((x - xCenter) * (x - xCenter)) + ((y - yCenter) * (y - yCenter))).toDouble()).toInt() > radius) {
                    continue
                }

                oldPixel = ivPhoto.getPixel(x, y)

                redAverage += (oldPixel shr 16 and 0xff)
                greenAverage += (oldPixel shr 8 and 0xff)
                blueAverage += (oldPixel and 0xff)

                hits++
            }
        }

        if (hits == 0) {
            return
        }

        redAverage /= hits
        greenAverage /= hits
        blueAverage /= hits

        var coef : Double
        var red: Int
        var green: Int
        var blue: Int

        for (y in (yCenter - radius) until (yCenter + radius)) {

            if (y < 0 || y >= bitmap.height) {
                continue
            }

            for (x in (xCenter - radius) until (xCenter + radius)) {

                if (x < 0 || x >= bitmap.width) {
                    continue
                }

                if (sqrt((((x - xCenter) * (x - xCenter)) + ((y - yCenter) * (y - yCenter))).toDouble()).toInt() >= radius) {
                    continue
                }

                coef = (1.0 - sqrt((((x - xCenter) * (x - xCenter)) + ((y - yCenter) * (y - yCenter))).toDouble()) / radius) * amount

                oldPixel = ivPhoto.getPixel(x, y)
                red = (oldPixel shr 16 and 0xff)
                green = (oldPixel shr 8 and 0xff)
                blue = (oldPixel and 0xff)


                // Усреднение красного канала
                if (redAverage > red) {
                    red += ((redAverage - red) * coef).toInt()
                }
                else if (redAverage < red) {
                    red -= ((red - redAverage) * coef).toInt()
                }
                else {
                    red = red
                }


                // Усреднение зеленого канала
                if (greenAverage > green){
                    green += ((greenAverage - green) * coef).toInt()
                }
                else if (greenAverage < green) {
                    green -= ((green - greenAverage) * coef).toInt()
                }
                else {
                    green = green
                }


                // Усреднение синего канала
                if (blueAverage > blue) {
                    blue += ((blueAverage - blue) * coef).toInt()
                }
                else if (blueAverage < blue) {
                    blue -= ((blue - blueAverage) * coef).toInt()
                }
                else {
                    blue = blue
                }

                ivPhoto.setPixel(x, y, ((-0x1000000) or (red shl 16) or (green shl 8) or blue))
            }
        }
    }
}