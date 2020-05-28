package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.draw_fragment.*


class DrawFragment  : Fragment() {

    var sortedValues: MutableList<Pair<Float, Float>> = ArrayList()
    private lateinit var canvas: Canvas


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.draw_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var bitmap = (activity!!.ivPhoto.drawable as BitmapDrawable).bitmap
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        canvas = Canvas(bitmap)

        activity!!.ivPhoto.setOnTouchListener { v, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {

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

                sortedValues.add(motionTouchEventX to motionTouchEventY)
                sortedValues.sortBy { it.first }


                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                paint.color = Color.BLACK
                canvas.drawCircle(motionTouchEventX, motionTouchEventY, 20F, paint)
                activity!!.ivPhoto.setImageBitmap(bitmap)
            }

            true
        }

        bLinear.setOnClickListener {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = Color.BLACK



            paint.strokeWidth = 5F
            for (i in 0 until sortedValues.size - 1) {
                canvas.drawCircle(sortedValues[i].first, sortedValues[i].second, 10F, paint)

                canvas.drawLine(
                    sortedValues[i].first,
                    sortedValues[i].second,
                    sortedValues[i + 1].first,
                    sortedValues[i + 1].second,
                    paint
                )
            }
            activity!!.ivPhoto.setImageBitmap(bitmap)
        }
    }
}