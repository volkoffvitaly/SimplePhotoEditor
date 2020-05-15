package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.ButtonBarLayout
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.filters_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


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
            tNegative,
            tSepia,
            tGray
        )

        var tempBitmap : Bitmap

        bNegative.setOnClickListener() {
            doAsync {
                uiThread {
                    textSelectedOff()
                    tNegative.isSelected = true
                    showConfirmBar()
                    activity!!.progressLoading!!.visibility = View.VISIBLE

                    enabledButtoms(false)
                }

                tempBitmap = onNegativeFilter(ivPhoto!!)

                uiThread {
                    activity!!.ivPhoto!!.setImageBitmap(tempBitmap)
                    activity!!.progressLoading!!.visibility = View.INVISIBLE

                    enabledButtoms(true)
                }
            }
        }

        bSepia.setOnClickListener() {
            doAsync {
                uiThread {
                    textSelectedOff()
                    tSepia.isSelected = true
                    showConfirmBar()
                    activity!!.progressLoading!!.visibility = View.VISIBLE

                    enabledButtoms(false)
                }

                tempBitmap = onSepiaFilter(ivPhoto!!)

                uiThread {
                    activity!!.ivPhoto!!.setImageBitmap(tempBitmap)
                    activity!!.progressLoading!!.visibility = View.INVISIBLE

                    enabledButtoms(true)
                }
            }
        }

        bGray.setOnClickListener() {
            doAsync {
                uiThread {
                    textSelectedOff()
                    tGray.isSelected = true
                    showConfirmBar()
                    activity!!.progressLoading!!.visibility = View.VISIBLE

                    enabledButtoms(false)
                }

                tempBitmap = onGrayFilter(ivPhoto!!)

                uiThread {
                    activity!!.ivPhoto!!.setImageBitmap(tempBitmap)
                    activity!!.progressLoading!!.visibility = View.INVISIBLE

                    enabledButtoms(true)
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

    private fun enabledButtoms(boolean: Boolean){
        // Блокируем/разблокируем кнопки фильтров
        for (i in 0 until frameLayout.getChildCount()) {
            frameLayout.getChildAt(i).isEnabled = boolean
        }
        // Блокируем/разблокируем кнопки подтверждений
        for (i in 0 until activity!!.confirmBar!!.childCount){
            activity!!.confirmBar!!.getChildAt(i).isEnabled = boolean
        }
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

        if (ivPhoto!!.width <= 150 || ivPhoto!!.height <= 150){
            if (ivPhoto!!.width > ivPhoto!!.height){
                xStart = (ivPhoto!!.width - ivPhoto!!.height) / 2
                sizeOfSide = ivPhoto!!.height
                yStart = 0
            } else {
                yStart = (ivPhoto!!.height - ivPhoto!!.width) / 2
                sizeOfSide = ivPhoto!!.width
                xStart = 0
            }
            step = 1

        } else {
            if (ivPhoto!!.width > ivPhoto!!.height){
                xStart = (ivPhoto!!.width - ivPhoto!!.height) / 2
                step = ivPhoto!!.height / 150
                yStart = 0
            } else {
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

        bNegative.setImageBitmap(onNegativeFilter(prevBitmap))
        bSepia.setImageBitmap(onSepiaFilter(prevBitmap))
        bGray.setImageBitmap(onGrayFilter(prevBitmap))
    }

    private fun showConfirmBar() {
        if (activity!!.confirmBar!!.visibility == View.INVISIBLE){
            activity!!.confirmBar!!.visibility = View.VISIBLE
        }
    }

    private fun onNegativeFilter(bitmap: Bitmap) : Bitmap {

        val bitmapNew = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val oldPixel = bitmap.getPixel(x, y)

                val r = 255 - Color.red(oldPixel)
                val g = 255 - Color.green(oldPixel)
                val b = 255 - Color.blue(oldPixel)

                bitmapNew.setPixel(x, y, Color.rgb(r, g ,b))
            }
        }

        return bitmapNew
    }



    private fun onSepiaFilter(bitmap: Bitmap) : Bitmap {
        
        val bitmapNew = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val oldPixel = bitmap.getPixel(x, y)

                val r = Color.red(oldPixel)
                val g = Color.green(oldPixel)
                val b = Color.blue(oldPixel)

                var red = (r * 0.393 + g * 0.769 + b * 0.189).toInt()
                var green = (r * 0.349 + g * 0.686 + b * 0.168).toInt()
                var blue = (r * 0.272 + g * 0.534 + b * 0.131).toInt()

                red = if (red > 255) 255 else red
                green = if (green > 255) 255 else green
                blue = if (blue > 255) 255 else blue

                bitmapNew.setPixel(x, y, Color.rgb(red, green, blue))
            }
        }

        return bitmapNew
    }



    private fun onGrayFilter(bitmap: Bitmap) : Bitmap {

        val bitmapNew = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val oldPixel = bitmap.getPixel(x, y)

                val r = Color.red(oldPixel)
                val g = Color.green(oldPixel)
                val b = Color.blue(oldPixel)
                val grey = (r * 0.2126 + g * 0.7152 + b * 0.0722).toInt()

                bitmapNew.setPixel(x, y, Color.rgb(grey, grey, grey))
            }
        }

        return bitmapNew
    }
}
