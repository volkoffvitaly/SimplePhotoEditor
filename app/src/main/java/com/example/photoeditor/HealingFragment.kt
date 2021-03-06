package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.healing_fragment.*
import kotlin.math.sqrt


class HealingFragment : Fragment() {

    private lateinit var ivPhoto: Bitmap
    private lateinit var originalIvPhoto: Bitmap

    //Parameters
    private var radius : Int = 75
    private var strength: Int = 50


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.healing_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        originalIvPhoto = (activity as stateChangesInterface).getIvPhoto()
        ivPhoto = (activity as stateChangesInterface).getIvPhoto()
        ivPhoto = ivPhoto.copy(Bitmap.Config.ARGB_8888, true)

        tHealingRadius.text = "Raduis: ${seekHealingRadius.progress}px"
        tHealingStrength.text = "Strength: ${seekHealingStrength.progress}%"


        seekHealingRadius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                tHealingRadius.text = "Raduis: ${i}px"
                tHealingRadius.isSelected = true
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                tHealingRadius.isSelected = false
                radius = seekHealingRadius.progress
            }
        })

        seekHealingStrength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                tHealingStrength.text = "Strength: ${i}%"
                tHealingStrength.isSelected = true
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                tHealingStrength.isSelected = false
                strength = seekHealingStrength.progress
            }
        })


        activity!!.ivPhoto.setOnTouchListener { v, event ->

            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {

                (activity as stateChangesInterface).stateOfTopBar(false)
                (activity as stateChangesInterface).stateOfConfirmBar(true)

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

                ivPhoto = healing(motionTouchEventX.toInt(), motionTouchEventY.toInt(), ivPhoto)
                activity!!.ivPhoto.setImageBitmap(ivPhoto)
            }

            true
        }

        activity!!.bConfirm!!.setOnClickListener {
            originalIvPhoto = (activity as stateChangesInterface).getIvPhoto()

            (activity as stateChangesInterface).stateOfConfirmBar(false)
            (activity as stateChangesInterface).stateOfTopBar(true)

            (activity as stateChangesInterface).addBitmapToMemoryCache(ivPhoto)
        }

        activity!!.bCancel!!.setOnClickListener {
            (activity as stateChangesInterface).changeIvPhoto(originalIvPhoto)
            ivPhoto = originalIvPhoto

            (activity as stateChangesInterface).stateOfConfirmBar(false)
            (activity as stateChangesInterface).stateOfTopBar(true)
        }
    }



    private fun healing(xCenter: Int, yCenter: Int, bitmap: Bitmap): Bitmap {

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

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

                oldPixel = pixels[bitmap.width * y + x]

                redAverage += (oldPixel shr 16 and 0xff)
                greenAverage += (oldPixel shr 8 and 0xff)
                blueAverage += (oldPixel and 0xff)

                hits++
            }
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

                coef = (1.0 - sqrt((((x - xCenter) * (x - xCenter)) + ((y - yCenter) * (y - yCenter))).toDouble()) / radius) * (strength.toDouble() / 100)

                oldPixel = pixels[bitmap.width * y + x]
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

                // Усреднение зеленого канала
                if (greenAverage > green){
                    green += ((greenAverage - green) * coef).toInt()
                }
                else if (greenAverage < green) {
                    green -= ((green - greenAverage) * coef).toInt()
                }

                // Усреднение синего канала
                if (blueAverage > blue) {
                    blue += ((blueAverage - blue) * coef).toInt()
                }
                else if (blueAverage < blue) {
                    blue -= ((blue - blueAverage) * coef).toInt()
                }

                pixels[bitmap.width * y + x] = (-0x1000000) or (red shl 16) or (green shl 8) or blue
            }
        }

        return Bitmap.createBitmap(pixels, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    }
}