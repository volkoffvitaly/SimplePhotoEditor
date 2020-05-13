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
import kotlin.math.abs


class UnsharpMaskingFragment : Fragment() {

    private var ivPhoto: Bitmap? = null
    private var blurredPhoto: Bitmap? = null

    //Parameters (Seek'и нужны на эти 3 параметра)
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

        tThreshold.text = "Threshold: 0"
        tAmount.text = "Amount: 0.1"
        tRadius.text = "Radius: 1"

        seekRadius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                tRadius.text = "Radius: ${seekRadius.progress}"
                tRadius.isSelected = true
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                radius = seekRadius.progress
                tRadius.isSelected = false
            }
        })

        seekThreshold.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                tThreshold.text = "Threshold: ${seekThreshold.progress}"
                tThreshold.isSelected = true
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                threshold = seekThreshold.progress
                tThreshold.isSelected = false
            }
        })

        seekAmount.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                tAmount.text = "Amount: ${(seekAmount.progress.toFloat() / 10)}"
                tAmount.isSelected = true
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                amount = (seekAmount.progress.toFloat() / 10)
                tAmount.isSelected = false
            }
        })

        bAply.setOnClickListener(){
            unsharp()
            showConfirmBar()
        }

        activity!!.bConfirm!!.setOnClickListener(){
            ivPhoto = (activity!!.ivPhoto.drawable as BitmapDrawable).bitmap
            activity!!.confirmBar!!.visibility = View.INVISIBLE
            seekAmount.progress = seekAmount.min
            seekRadius.progress = seekRadius.min
            seekThreshold.progress = seekThreshold.min

            tAmount.isSelected = false
            tRadius.isSelected = false
            tThreshold.isSelected = false
        }

        activity!!.bCancel!!.setOnClickListener(){
            activity!!.ivPhoto!!.setImageBitmap(ivPhoto)
            activity!!.confirmBar!!.visibility = View.INVISIBLE
        }
    }

    private fun unsharp(){
        blurredPhoto = boxBlur(ivPhoto!!, radius)

        val originalPixels =  Array(ivPhoto!!.width, {IntArray(ivPhoto!!.height)})
        val blurredPixels = Array(blurredPhoto!!.width, {IntArray(blurredPhoto!!.height)})

        for (j in 0 until ivPhoto!!.height) {
            for (i in 0 until ivPhoto!!.width) {
                originalPixels[i][j] = ivPhoto!!.getPixel(i, j)
                blurredPixels[i][j] = blurredPhoto!!.getPixel(i, j)
            }
        }

        unsharpMask(originalPixels, blurredPixels, amount, threshold)
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


    private fun boxBlurHorizontal(pixels: IntArray, w: Int, h: Int, halfRange: Int) {
        var index = 0
        val newColors = IntArray(w)
        for (y in 0 until h) {
            var hits = 0
            var r: Long = 0
            var g: Long = 0
            var b: Long = 0
            for (x in -halfRange until w) {
                val oldPixel = x - halfRange - 1
                if (oldPixel >= 0) {
                    val color = pixels[index + oldPixel]
                    if (color != 0) {
                        r -= Color.red(color)
                        g -= Color.green(color)
                        b -= Color.blue(color)
                    }
                    hits--
                }
                val newPixel = x + halfRange
                if (newPixel < w) {
                    val color = pixels[index + newPixel]
                    if (color != 0) {
                        r += Color.red(color)
                        g += Color.green(color)
                        b += Color.blue(color)
                    }
                    hits++
                }
                if (x >= 0) {
                    newColors[x] = Color.rgb((r / hits).toInt(),
                        (g / hits).toInt(), (b / hits).toInt()
                    )
                }
            }
            for (x in 0 until w) {
                pixels[index + x] = newColors[x]
            }
            index += w
        }
    }


    private fun boxBlurVertical(pixels: IntArray, w: Int, h: Int, halfRange: Int) {
        val newColors = IntArray(h)
        val oldPixelOffset = -(halfRange + 1) * w
        val newPixelOffset = halfRange * w
        for (x in 0 until w) {
            var hits = 0
            var r: Long = 0
            var g: Long = 0
            var b: Long = 0
            var index = -halfRange * w + x
            for (y in -halfRange until h) {
                val oldPixel = y - halfRange - 1
                if (oldPixel >= 0) {
                    val color = pixels[index + oldPixelOffset]
                    if (color != 0) {
                        r -= Color.red(color)
                        g -= Color.green(color)
                        b -= Color.blue(color)
                    }
                    hits--
                }
                val newPixel = y + halfRange
                if (newPixel < h) {
                    val color = pixels[index + newPixelOffset]
                    if (color != 0) {
                        r += Color.red(color)
                        g += Color.green(color)
                        b += Color.blue(color)
                    }
                    hits++
                }
                if (y >= 0) {
                    newColors[y] = Color.rgb((r / hits).toInt(),
                        (g / hits).toInt(), (b / hits).toInt()
                    )
                }
                index += w
            }
            for (y in 0 until h) {
                pixels[y * w + x] = newColors[y]
            }
        }
    }


    private fun unsharpMask(
        origPixels: Array<IntArray>,
        blurredPixels: Array<IntArray>,
        amount: Float,
        threshold: Int
    ) {

        val newBitmap = Bitmap.createBitmap(ivPhoto!!.width, ivPhoto!!.height, Bitmap.Config.ARGB_8888)

        var orgRed = 0
        var orgGreen = 0
        var orgBlue = 0
        var blurredRed = 0
        var blurredGreen = 0
        var blurredBlue = 0
        var usmPixel = 0
        val alpha = -0x1000000
        for (j in 0 until newBitmap.height) {
            for (i in 0 until newBitmap.width) {
                val origPixel = origPixels[i][j]
                val blurredPixel = blurredPixels[i][j]

                orgRed = origPixel shr 16 and 0xff
                orgGreen = origPixel shr 8 and 0xff
                orgBlue = origPixel and 0xff
                blurredRed = blurredPixel shr 16 and 0xff
                blurredGreen = blurredPixel shr 8 and 0xff
                blurredBlue = blurredPixel and 0xff

                if (abs(orgRed - blurredRed) >= threshold) {
                    orgRed = (amount * (orgRed - blurredRed) + orgRed).toInt()
                    orgRed = if (orgRed > 255) 255 else if (orgRed < 0) 0 else orgRed
                }
                if (abs(orgGreen - blurredGreen) >= threshold) {
                    orgGreen = (amount * (orgGreen - blurredGreen) + orgGreen).toInt()
                    orgGreen = if (orgGreen > 255) 255 else if (orgGreen < 0) 0 else orgGreen
                }
                if (abs(orgBlue - blurredBlue) >= threshold) {
                    orgBlue = (amount * (orgBlue - blurredBlue) + orgBlue).toInt()
                    orgBlue = if (orgBlue > 255) 255 else if (orgBlue < 0) 0 else orgBlue
                }
                usmPixel = alpha or (orgRed shl 16) or (orgGreen shl 8) or orgBlue
                newBitmap.setPixel(i, j, usmPixel)
            }
        }

        activity!!.ivPhoto!!.setImageBitmap(newBitmap)
    }

    private fun showConfirmBar() {
        if (activity!!.confirmBar!!.visibility == View.INVISIBLE){
            activity!!.confirmBar!!.visibility = View.VISIBLE
        }
    }
}