package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.draw_fragment.*


class DrawFragment  : Fragment() {


    var sortedDots: MutableList<Pair<Float, Float>> = ArrayList()
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

                sortedDots.add(motionTouchEventX to motionTouchEventY)
                sortedDots.sortBy { it.first }


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


            for (i in 0 until sortedDots.size - 1) {
                canvas.drawCircle(sortedDots[i].first, sortedDots[i].second, 10F, paint)

                canvas.drawLine(
                    sortedDots[i].first,
                    sortedDots[i].second,
                    sortedDots[i + 1].first,
                    sortedDots[i + 1].second,
                    paint
                )
            }
            activity!!.ivPhoto.setImageBitmap(bitmap)
        }

        bCurved.setOnClickListener {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = Color.BLACK
            paint.strokeWidth = 5F


            val left = sortedDots[0].first + 0.005F
            val right = sortedDots[sortedDots.size - 1].first - 0.005F

            var i = left

            while (i < right) {
                val y: Float = lagrange(i)
                canvas.drawCircle(i, y, 5F, paint)

                i += 0.0001F
            }

            activity!!.ivPhoto.setImageBitmap(bitmap)
        }
    }

    private fun lagrange(Xi: Float): Float {
        var result = 0.0F

        for (i in 0 until sortedDots.size) {
            var P = 1.0F

            for (j in 0 until sortedDots.size)
                if (i != j) {
                    P *= (Xi - sortedDots[j].first) / (sortedDots[i].first - sortedDots[j].second)
                }

            result += (P * sortedDots[i].second)

        }

        return result
    }


    /*public float lagrange(V mas[], int n, double _x){

        float result = 0.0f;

        for(int i = 0; i < n; i++){
            double P = 1.0f;
            for(int j = 0; j < n; j++)
                if(i!= j)
                    P *= (_x- mas[j].X)/(mas[i].X-mas[j].X);

                result += P*mas[i].Y;
        }
        return result;
    }*/
}