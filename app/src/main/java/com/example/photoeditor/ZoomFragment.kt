package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.zoom_fragment.*


class ZoomFragment : Fragment() {

    var ivPhoto: Bitmap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zoom_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ivPhoto == null)
            ivPhoto = (activity!!.ivPhoto.drawable as BitmapDrawable).bitmap

        textViewZoom.text = "100%"
        seekZoom.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                val temp = 100 - i
                textViewZoom.text = "$temp%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                fZoom(100 - seekZoom.progress)
            }
        })
    }

    fun fZoom(procent: Int){
        val bitmapNew = Bitmap.createBitmap((ivPhoto!!.width * procent / 100), (ivPhoto!!.height * procent / 100), Bitmap.Config.ARGB_8888)

        val startX: Int = (ivPhoto!!.width - (ivPhoto!!.width * procent / 100)) / 2
        val startY: Int = (ivPhoto!!.height - (ivPhoto!!.height * procent / 100)) / 2

        for (y in 0 until bitmapNew.height){
            for (x in 0 until bitmapNew.width){
                val oldPixel = ivPhoto!!.getPixel(startX + x, startY + y)
                bitmapNew.setPixel(x, y, oldPixel)
            }
        }
        activity!!.ivPhoto!!.setImageBitmap(bitmapNew)
    }
}
