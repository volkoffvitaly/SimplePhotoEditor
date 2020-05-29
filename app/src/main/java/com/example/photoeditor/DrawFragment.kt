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

    // Возможные наборы:
    // strWidth = 20.0F, radius = 40.0F - big
    // strWidth = 10.0F, radius = 20.0F - big
    private lateinit var canvas: Canvas
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var strWidth = 50.0F // 1..20
    private var radius = 100.0F // 1..100
    private var sortedDots: MutableList<Pair<Float, Float>> = ArrayList()



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.draw_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var bitmap = (activity!!.ivPhoto.drawable as BitmapDrawable).bitmap
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)


        canvas = Canvas(bitmap)
        paint.color = Color.BLACK // установка цвета в коде должно быть здесь
        //paint.color = Color.WHITE   Смену можно реализовать по кнопкам. (дефолт будет черный)
        //paint.color = Color.RED
        //paint.color = Color.GREEN
        //paint.color = Color.BLUE
        //paint.color = Color.YELLOW

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

                canvas.drawCircle(motionTouchEventX, motionTouchEventY, radius, paint)
                activity!!.ivPhoto.setImageBitmap(bitmap)
            }

            true
        }

        bLinear.setOnClickListener {
            //
            // Здесь нужно ставить битмап, с которым мы пришли в этот фрагмент!!!!!
            //
            paint.strokeWidth = strWidth


            for (i in 0 until sortedDots.size - 1) {
                canvas.drawCircle(sortedDots[i].first, sortedDots[i].second, radius, paint)

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
    }
}