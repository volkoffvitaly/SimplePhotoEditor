package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.filters_fragment.*


class FiltersFragment : Fragment() {

    lateinit var ivPhoto: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.filters_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivPhoto = activity!!.ivPhoto

        bNegative.setOnClickListener(){
            onNegativeFilter()
        }

        bSepia.setOnClickListener(){
            onSepiaFilter()
        }

        bGray.setOnClickListener(){
            onGrayFilter()
        }
    }

    fun onNegativeFilter() {
        val bitmapOld = (ivPhoto.drawable as BitmapDrawable).bitmap
        val bitmapNew = bitmapOld.copy(Bitmap.Config.ARGB_8888, true)

        for (y in 0 until bitmapOld.height) {
            for (x in 0 until bitmapOld.width) {
                val oldPixel = bitmapOld.getPixel(x, y)

                val r = 255 - Color.red(oldPixel)
                val g = 255 - Color.green(oldPixel)
                val b = 255 - Color.blue(oldPixel)

                bitmapNew.setPixel(x, y, Color.rgb(r, g ,b))
            }
        }
        ivPhoto.setImageBitmap(bitmapNew)
    }

    fun onSepiaFilter() {
        val bitmapOld = (ivPhoto.drawable as BitmapDrawable).bitmap
        val bitmapNew = bitmapOld.copy(Bitmap.Config.ARGB_8888, true)

        for (y in 0 until bitmapOld.height) {
            for (x in 0 until bitmapOld.width) {
                val oldPixel = bitmapOld.getPixel(x, y)

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

        ivPhoto.setImageBitmap(bitmapNew)
    }

    fun onGrayFilter() {
        val bitmapOld = (ivPhoto.drawable as BitmapDrawable).bitmap
        val bitmapNew = bitmapOld.copy(Bitmap.Config.ARGB_8888, true)

        for (y in 0 until bitmapOld.height) {
            for (x in 0 until bitmapOld.width) {
                val oldPixel = bitmapOld.getPixel(x, y)

                val r = Color.red(oldPixel)
                val g = Color.green(oldPixel)
                val b = Color.blue(oldPixel)
                val grey = (r * 0.2126 + g * 0.7152 + b * 0.0722).toInt()

                bitmapNew.setPixel(x, y, Color.rgb(grey, grey, grey))
            }
        }

        ivPhoto.setImageBitmap(bitmapNew)
    }
}
