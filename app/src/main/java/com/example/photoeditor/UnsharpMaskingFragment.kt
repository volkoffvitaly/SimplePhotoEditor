package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.unsharp_masking_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.math.abs


class UnsharpMaskingFragment : Fragment() {

    private var ivPhoto: Bitmap? = null
    private var blurredPhoto: Bitmap? = null


    //Parameters for Unsharp Masking
    private var radius = 1
    private var amount = 0.1f //float
    private var threshold = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.unsharp_masking_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ivPhoto == null) {
            ivPhoto = (activity!!.ivPhoto.drawable as BitmapDrawable).bitmap
            //EditorActivity.States.states.add(ivPhoto!!)
        }


        tRadius.text = "Radius: 1"
        tAmount.text = "Amount: 0.1"
        tThreshold.text = "Threshold: 0"


        // seek bar of RADIUS
        seekRadius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                tRadius.text = "Radius: ${seekRadius.progress}"
                tRadius.isSelected = true
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                radius = seekRadius.progress
                tRadius.isSelected = false
            }
        })
        // seek bar of RADIUS


        // seek bar of AMOUNT
        seekAmount.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                tAmount.text = "Amount: ${(seekAmount.progress.toFloat() / 10)}"
                tAmount.isSelected = true
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                amount = (seekAmount.progress.toFloat() / 10)
                tAmount.isSelected = false
            }
        })
        // seek bar of AMOUNT


        // seek bar of THRESHOLD
        seekThreshold.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                tThreshold.text = "Threshold: ${seekThreshold.progress}"
                tThreshold.isSelected = true
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                threshold = seekThreshold.progress
                tThreshold.isSelected = false
            }
        })
        // seek bar of THRESHOLD


        bApply.setOnClickListener() {
            doAsync {
                uiThread {
                    seekThreshold.isEnabled = false
                    seekAmount.isEnabled = false
                    seekRadius.isEnabled = false
                    (activity as stateChangesInterface).stateOfProgressLoading(true)
                    (activity as stateChangesInterface).stateOfConfirmBar(true)
                    (activity as stateChangesInterface).stateOfConfirmBarButtons(false)
                    (activity as stateChangesInterface).stateOfTopBar(false)
                }
                val tempBitmap = unsharpMasking(amount, threshold)
                uiThread {
                    (activity as stateChangesInterface).changeIvPhoto(tempBitmap)

                    (activity as stateChangesInterface).stateOfProgressLoading(false)
                    (activity as stateChangesInterface).stateOfConfirmBarButtons(true)
                }
            }
        }

        // Confirmation of changes
        activity!!.bConfirm!!.setOnClickListener() {
            ivPhoto = (activity!!.ivPhoto.drawable as BitmapDrawable).bitmap

            (activity as stateChangesInterface).stateOfConfirmBar(false)
            (activity as stateChangesInterface).stateOfTopBar(true)

            seekAmount.progress = seekAmount.min
            seekRadius.progress = seekRadius.min
            seekThreshold.progress = seekThreshold.min

            tAmount.isSelected = false
            tRadius.isSelected = false
            tThreshold.isSelected = false

            bApply.isEnabled = true
            seekThreshold.isEnabled = true
            seekAmount.isEnabled = true
            seekRadius.isEnabled = true
        }
        // Confirmation of changes


        // Revert changes
        activity!!.bCancel!!.setOnClickListener() {
            (activity as stateChangesInterface).changeIvPhoto(ivPhoto!!)

            (activity as stateChangesInterface).stateOfConfirmBar(false)
            (activity as stateChangesInterface).stateOfTopBar(true)

            bApply.isEnabled = true
            seekThreshold.isEnabled = true
            seekAmount.isEnabled = true
            seekRadius.isEnabled = true
        }
        // Revert changes
    }



    private fun boxBlur(bitmap: Bitmap, range: Int): Bitmap? {
        assert(range and 1 == 0) { "Range must be odd." }

        val width = bitmap.width
        val height = bitmap.height

        val blurred = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(blurred)

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        boxBlurHorizontal(pixels, width, height, range / 2)
        boxBlurVertical(pixels, width, height, range / 2)

        canvas.drawBitmap(pixels, 0, width, 0.0f, 0.0f, width, height, true, null)


        return blurred
    }


    private fun boxBlurHorizontal(pixels: IntArray, width: Int, height: Int, halfRange: Int) {
        val newColors = IntArray(width)

        var index = 0
        var hits: Int
        var red: Long
        var green: Long
        var blue: Long

        var oldPixel: Int
        var oldColor: Int

        var newPixel: Int
        var newColor: Int


        for (y in 0 until height) {
            hits = 0
            red = 0
            green = 0
            blue = 0

            for (x in -halfRange until width) {
                oldPixel = x - halfRange - 1

                if (oldPixel >= 0) {
                    oldColor = pixels[index + oldPixel]

                    if (oldColor != 0) {
                        red -= Color.red(oldColor)
                        green -= Color.green(oldColor)
                        blue -= Color.blue(oldColor)
                    }

                    hits--
                }

                newPixel = x + halfRange

                if (newPixel < width) {
                    newColor = pixels[index + newPixel]

                    if (newColor != 0) {
                        red += Color.red(newColor)
                        green += Color.green(newColor)
                        blue += Color.blue(newColor)
                    }

                    hits++
                }

                if (x >= 0) {
                    newColors[x] = Color.rgb(
                        (red / hits).toInt(),
                        (green / hits).toInt(),
                        (blue / hits).toInt()
                    )
                }
            }

            for (x in 0 until width) {
                pixels[index + x] = newColors[x]
            }

            index += width
        }
    }


    private fun boxBlurVertical(pixels: IntArray, width: Int, height: Int, halfRange: Int) {
        val newColors = IntArray(height)
        val oldPixelOffset = -(halfRange + 1) * width
        val newPixelOffset = halfRange * width

        var index: Int
        var hits: Int
        var red: Long
        var green: Long
        var blue: Long

        var oldPixel: Int
        var oldColor: Int

        var newPixel: Int
        var newColor: Int


        for (x in 0 until width) {
            index = -halfRange * width + x
            hits = 0
            red = 0
            green = 0
            blue = 0

            for (y in -halfRange until height) {
                oldPixel = y - halfRange - 1

                if (oldPixel >= 0) {
                    oldColor = pixels[index + oldPixelOffset]

                    if (oldColor != 0) {
                        red -= Color.red(oldColor)
                        green -= Color.green(oldColor)
                        blue -= Color.blue(oldColor)
                    }

                    hits--
                }

                newPixel = y + halfRange

                if (newPixel < height) {
                    newColor = pixels[index + newPixelOffset]

                    if (newColor != 0) {
                        red += Color.red(newColor)
                        green += Color.green(newColor)
                        blue += Color.blue(newColor)
                    }

                    hits++
                }

                if (y >= 0) {
                    newColors[y] = Color.rgb(
                        (red / hits).toInt(),
                        (green / hits).toInt(),
                        (blue / hits).toInt()
                    )
                }

                index += width
            }

            for (y in 0 until height) {
                pixels[y * width + x] = newColors[y]
            }
        }
    }


    private fun unsharpMasking (amount: Float, threshold: Int) : Bitmap {

        blurredPhoto = boxBlur(ivPhoto!!, radius)

        val newBitmap =
            Bitmap.createBitmap(ivPhoto!!.width, ivPhoto!!.height, Bitmap.Config.ARGB_8888)

        var red: Int
        var green: Int
        var blue: Int
        var blurredRed: Int
        var blurredGreen: Int
        var blurredBlue: Int
        var unsharpPixel: Int
        val alpha = -0x1000000

        var originalPixel: Int
        var blurredPixel: Int

        for (j in 0 until newBitmap.height) {
            for (i in 0 until newBitmap.width) {

                // Get Pixels
                originalPixel = ivPhoto!!.getPixel(i, j)
                blurredPixel = blurredPhoto!!.getPixel(i, j)

                // Get original colors
                red = originalPixel shr 16 and 0xff
                green = originalPixel shr 8 and 0xff
                blue = originalPixel and 0xff

                // Get blurred colors
                blurredRed = blurredPixel shr 16 and 0xff
                blurredGreen = blurredPixel shr 8 and 0xff
                blurredBlue = blurredPixel and 0xff

                if (abs(red - blurredRed) >= threshold) {
                    red = (amount * (red - blurredRed) + red).toInt()
                    red = when {
                        red > 255 -> 255
                        red < 0 -> 0
                        else -> red
                    }
                }

                if (abs(green - blurredGreen) >= threshold) {
                    green = (amount * (green - blurredGreen) + green).toInt()
                    green = when {
                        green > 255 -> 255
                        green < 0 -> 0
                        else -> green
                    }
                }

                if (abs(blue - blurredBlue) >= threshold) {
                    blue = (amount * (blue - blurredBlue) + blue).toInt()
                    blue = when {
                        blue > 255 -> 255
                        blue < 0 -> 0
                        else -> blue
                    }
                }

                unsharpPixel = alpha or (red shl 16) or (green shl 8) or blue
                newBitmap.setPixel(i, j, unsharpPixel)
            }
        }

        return newBitmap
    }
}