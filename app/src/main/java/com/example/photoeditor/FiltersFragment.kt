package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.filters_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.math.max
import kotlin.math.min


class FiltersFragment : Fragment() {

    var ivPhoto: Bitmap? = null

    lateinit var names: Array<TextView>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.filters_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ivPhoto == null)
            ivPhoto = (activity!!.ivPhoto.drawable as BitmapDrawable).bitmap

        setPreview()

        names = arrayOf(
            tContrast,
            tBright,
            tNegative,
            tSepia,
            tGray
        )

        var tempBitmap : Bitmap

        llBright.setOnClickListener() {
            doAsync {
                uiThread {
                    textSelectedOff()
                    showConfirmBar()
                    activity!!.progressLoading!!.visibility = View.VISIBLE

                    enabledButtons(false)
                    tBright.isSelected = true
                }

                tempBitmap = onBrightFilter(ivPhoto!!)

                uiThread {
                    activity!!.ivPhoto!!.setImageBitmap(tempBitmap)
                    activity!!.progressLoading!!.visibility = View.INVISIBLE

                    enabledButtons(true)
                }
            }
        }

        llContrast.setOnClickListener() {
            doAsync {
                uiThread {
                    textSelectedOff()
                    showConfirmBar()
                    activity!!.progressLoading!!.visibility = View.VISIBLE

                    enabledButtons(false)
                    tContrast.isSelected = true
                }

                tempBitmap = onContrastFilter(ivPhoto!!)

                uiThread {
                    activity!!.ivPhoto!!.setImageBitmap(tempBitmap)
                    activity!!.progressLoading!!.visibility = View.INVISIBLE

                    enabledButtons(true)
                }
            }
        }

        llNegative.setOnClickListener() {
            doAsync {
                uiThread {
                    textSelectedOff()
                    showConfirmBar()
                    activity!!.progressLoading!!.visibility = View.VISIBLE

                    enabledButtons(false)
                    tNegative.isSelected = true
                }

                tempBitmap = onNegativeFilter(ivPhoto!!)

                uiThread {
                    activity!!.ivPhoto!!.setImageBitmap(tempBitmap)
                    activity!!.progressLoading!!.visibility = View.INVISIBLE

                    enabledButtons(true)
                }
            }
        }

        llSepia.setOnClickListener() {
            doAsync {
                uiThread {
                    textSelectedOff()
                    showConfirmBar()
                    activity!!.progressLoading!!.visibility = View.VISIBLE

                    enabledButtons(false)
                    tSepia.isSelected = true
                }

                tempBitmap = onSepiaFilter(ivPhoto!!)

                uiThread {
                    activity!!.ivPhoto!!.setImageBitmap(tempBitmap)
                    activity!!.progressLoading!!.visibility = View.INVISIBLE

                    enabledButtons(true)
                }
            }
        }

        llGray.setOnClickListener() {
            doAsync {
                uiThread {
                    textSelectedOff()
                    showConfirmBar()
                    activity!!.progressLoading!!.visibility = View.VISIBLE

                    enabledButtons(false)
                    tGray.isSelected = true
                }

                tempBitmap = onGrayFilter(ivPhoto!!)

                uiThread {
                    activity!!.ivPhoto!!.setImageBitmap(tempBitmap)
                    activity!!.progressLoading!!.visibility = View.INVISIBLE

                    enabledButtons(true)
                }
            }
        }

        activity!!.bConfirm!!.setOnClickListener(){
            ivPhoto = (activity!!.ivPhoto.drawable as BitmapDrawable).bitmap
            activity!!.confirmBar!!.visibility = View.INVISIBLE
            setPreview()
            textSelectedOff()
        }

        activity!!.bCancel!!.setOnClickListener(){
            activity!!.ivPhoto!!.setImageBitmap(ivPhoto)
            activity!!.confirmBar!!.visibility = View.INVISIBLE
            textSelectedOff()
        }
    }

    private fun enabledButtons(boolean: Boolean){
        // Блокируем/разблокируем фильтров
        for (i in 0 until llSelectFilters.childCount) {
            llSelectFilters.getChildAt(i).isEnabled = boolean
        }

        // Блокируем/разблокируем кнопки подтверждений
        for (i in 0 until activity!!.confirmBar!!.childCount){
            activity!!.confirmBar!!.getChildAt(i).isEnabled = boolean
        }

        // Смена цвета надписей
        for (i in names.indices) names[i].isEnabled = boolean
    }

    private fun textSelectedOff(){
        for (i in names.indices) names[i].isSelected = false
    }

    private fun setPreview(){
        val yStart : Int
        val xStart : Int
        val step : Int
        val prevBitmap : Bitmap
        val sizeOfSide : Int


        if (ivPhoto!!.width <= 150 || ivPhoto!!.height <= 150) {
            if (ivPhoto!!.width > ivPhoto!!.height) {
                xStart = (ivPhoto!!.width - ivPhoto!!.height) / 2
                sizeOfSide = ivPhoto!!.height
                yStart = 0
            }

            else {
                yStart = (ivPhoto!!.height - ivPhoto!!.width) / 2
                sizeOfSide = ivPhoto!!.width
                xStart = 0
            }

            step = 1
        }

        else {
            if (ivPhoto!!.width > ivPhoto!!.height) {
                xStart = (ivPhoto!!.width - ivPhoto!!.height) / 2
                step = ivPhoto!!.height / 150
                yStart = 0
            }

            else {
                yStart = (ivPhoto!!.height - ivPhoto!!.width) / 2
                step = ivPhoto!!.width / 150
                xStart = 0
            }

            sizeOfSide = 150
        }


        prevBitmap = Bitmap.createBitmap(sizeOfSide, sizeOfSide, Bitmap.Config.ARGB_8888)

        for (y in 0 until prevBitmap.height){
            for (x in 0 until prevBitmap.width){
                prevBitmap.setPixel(x, y, ivPhoto!!.getPixel(x * step + xStart, y * step + yStart))
            }
        }

        bContrast.setImageBitmap(onContrastFilter(prevBitmap))
        bBright.setImageBitmap(onBrightFilter(prevBitmap))
        bNegative.setImageBitmap(onNegativeFilter(prevBitmap))
        bSepia.setImageBitmap(onSepiaFilter(prevBitmap))
        bGray.setImageBitmap(onGrayFilter(prevBitmap))
    }

    private fun showConfirmBar() {
        if (activity!!.confirmBar!!.visibility == View.INVISIBLE){
            activity!!.confirmBar!!.visibility = View.VISIBLE
        }
    }


    // FILTERS //
    private fun onNegativeFilter(bitmap: Bitmap) : Bitmap {

        val bitmapNew = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var oldPixel : Int
        var r : Int
        var g : Int
        var b : Int

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                oldPixel = pixels[bitmap.width * y + x]

                r = 255 - Color.red(oldPixel)
                g = 255 - Color.green(oldPixel)
                b = 255 - Color.blue(oldPixel)

                bitmapNew.setPixel(x, y, Color.rgb(r, g ,b))
            }
        }

        return bitmapNew
    }


    private fun onSepiaFilter(bitmap: Bitmap) : Bitmap {

        val bitmapNew = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var oldPixel : Int
        var r : Int
        var g : Int
        var b : Int

        var red : Int
        var green : Int
        var blue : Int

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                oldPixel = pixels[bitmap.width * y + x]

                r = Color.red(oldPixel)
                g = Color.green(oldPixel)
                b = Color.blue(oldPixel)

                red = (r * 0.393 + g * 0.769 + b * 0.189).toInt()
                green = (r * 0.349 + g * 0.686 + b * 0.168).toInt()
                blue = (r * 0.272 + g * 0.534 + b * 0.131).toInt()

                red = if (red > 255) 255 else red
                green = if (green > 255) 255 else green
                blue = if (blue > 255) 255 else blue

                bitmapNew.setPixel(x, y, Color.rgb(red, green, blue))
            }
        }

        return bitmapNew
    }


    private fun onGrayFilter(bitmap: Bitmap) : Bitmap {

        val bitmapNew = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var oldPixel : Int
        var r : Int
        var g : Int
        var b : Int

        var grey : Int

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                oldPixel = pixels[bitmap.width * y + x]

                r = Color.red(oldPixel)
                g = Color.green(oldPixel)
                b = Color.blue(oldPixel)
                grey = (r * 0.2126 + g * 0.7152 + b * 0.0722).toInt()

                bitmapNew.setPixel(x, y, Color.rgb(grey, grey, grey))
            }
        }

        return bitmapNew
    }


    private fun onBrightFilter(bitmap: Bitmap) : Bitmap {

        val bitmapNew = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var oldPixel : Int
        var r : Int
        var g : Int
        var b : Int

        val k : Double = 1.5

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                oldPixel = pixels[bitmap.width * y + x]

                r = min(255, (Color.red(oldPixel) * k).toInt())
                g = min(255, (Color.green(oldPixel) * k).toInt())
                b = min(255, (Color.blue(oldPixel) * k).toInt())

                bitmapNew.setPixel(x, y, Color.rgb(r, g ,b))
            }
        }

        return bitmapNew
    }


    private fun onContrastFilter(bitmap: Bitmap) : Bitmap {

        val bitmapNew = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var oldPixel : Int
        var r : Int
        var g : Int
        var b : Int

        val k = 1.5
        var avg = 0.0

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                oldPixel = pixels[bitmap.width * y + x]
                avg += Color.red(oldPixel) * 0.299 + Color.green(oldPixel) * 0.587 + Color.blue(oldPixel) * 0.114
            }
        }

        avg /= bitmap.width * bitmap.height

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                oldPixel = pixels[bitmap.width * y + x]

                r = min(255, max(0, (avg + k * (Color.red(oldPixel) - avg)).toInt()))
                g = min(255, max(0, (avg + k * (Color.green(oldPixel) - avg)).toInt()))
                b = min(255, max(0, (avg + k * (Color.blue(oldPixel) - avg)).toInt()))

                bitmapNew.setPixel(x, y, Color.rgb(r, g ,b))
            }
        }

        return bitmapNew
    }
    // FILTERS //
}
