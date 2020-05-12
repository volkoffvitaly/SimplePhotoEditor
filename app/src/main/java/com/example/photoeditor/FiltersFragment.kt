package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.filters_fragment.*


class FiltersFragment : Fragment() {

    var ivPhoto: Bitmap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.filters_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ivPhoto == null)
            ivPhoto = (activity!!.ivPhoto.drawable as BitmapDrawable).bitmap

        bNegative.setOnClickListener() {
            onNegativeFilter()
            showConfirmBar()
        }

        bSepia.setOnClickListener() {
            onSepiaFilter()
            showConfirmBar()
        }

        bGray.setOnClickListener() {
            onGrayFilter()
            showConfirmBar()
        }

        activity!!.bConfirm!!.setOnClickListener(){
            ivPhoto = (activity!!.ivPhoto.drawable as BitmapDrawable).bitmap
            activity!!.confirmBar!!.visibility = View.INVISIBLE
        }

        activity!!.bCancel!!.setOnClickListener(){
            activity!!.ivPhoto!!.setImageBitmap(ivPhoto)
            activity!!.confirmBar!!.visibility = View.INVISIBLE
        }
    }

    private fun showConfirmBar() {
        activity!!.confirmBar!!.visibility = View.VISIBLE
    }

    private fun onNegativeFilter() {

        val bitmapNew = ivPhoto!!.copy(Bitmap.Config.ARGB_8888, true)

        for (y in 0 until ivPhoto!!.height) {
            for (x in 0 until ivPhoto!!.width) {
                val oldPixel = ivPhoto!!.getPixel(x, y)

                val r = 255 - Color.red(oldPixel)
                val g = 255 - Color.green(oldPixel)
                val b = 255 - Color.blue(oldPixel)

                bitmapNew.setPixel(x, y, Color.rgb(r, g ,b))
            }
        }

        activity!!.ivPhoto!!.setImageBitmap(bitmapNew)
    }



    private fun onSepiaFilter() {
        
        val bitmapNew = ivPhoto!!.copy(Bitmap.Config.ARGB_8888, true)

        for (y in 0 until ivPhoto!!.height) {
            for (x in 0 until ivPhoto!!.width) {
                val oldPixel = ivPhoto!!.getPixel(x, y)

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

        activity!!.ivPhoto!!.setImageBitmap(bitmapNew)
    }



    private fun onGrayFilter() {

        val bitmapNew = ivPhoto!!.copy(Bitmap.Config.ARGB_8888, true)

        for (y in 0 until ivPhoto!!.height) {
            for (x in 0 until ivPhoto!!.width) {
                val oldPixel = ivPhoto!!.getPixel(x, y)

                val r = Color.red(oldPixel)
                val g = Color.green(oldPixel)
                val b = Color.blue(oldPixel)
                val grey = (r * 0.2126 + g * 0.7152 + b * 0.0722).toInt()

                bitmapNew.setPixel(x, y, Color.rgb(grey, grey, grey))
            }
        }

        activity!!.ivPhoto!!.setImageBitmap(bitmapNew)
    }
}
