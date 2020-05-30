package com.example.photoeditor


import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.draw_fragment.*


class DrawFragment  : Fragment() {

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var radius = 50.0F // 1..100
    private var sortedDots: MutableList<Pair<Float, Float>> = ArrayList()

    lateinit var indicators : Array<View>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.draw_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var originalIvPhoto = (activity as stateChangesInterface).getIvPhoto()
        originalIvPhoto = originalIvPhoto.copy(Bitmap.Config.ARGB_8888, true)
        var bitmap = originalIvPhoto.copy(Bitmap.Config.ARGB_8888, true)

        tThickness.text = "Thickness: ${seekThickness.progress}%"

        paint.color = Color.BLACK

        indicators = arrayOf(
            vBlackColor,
            vWhiteColor,
            vRedColor,
            vGreenColor,
            vBlueColor,
            vYellowColor
        )

        vBlackColor.visibility = View.VISIBLE


        bBlackColor.setOnClickListener() {
            paint.color = Color.BLACK
            changeIndicator(0)
        }

        bWhiteColor.setOnClickListener() {
            paint.color = Color.WHITE
            changeIndicator(1)
        }

        bRedColor.setOnClickListener() {
            paint.color = Color.RED
            changeIndicator(2)
        }

        bGreenColor.setOnClickListener() {
            paint.color = Color.GREEN
            changeIndicator(3)
        }

        bBlueColor.setOnClickListener() {
            paint.color = Color.BLUE
            changeIndicator(4)
        }

        bYellowColor.setOnClickListener() {
            paint.color = Color.YELLOW
            changeIndicator(5)
        }

        seekThickness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                tThickness.text = "Thickness: ${i}%"
                tThickness.isSelected = true
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                tThickness.isSelected = false
                radius = seekThickness.progress * 1.0f
            }
        })

        activity!!.ivPhoto.setOnTouchListener { v, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN || event.actionMasked == MotionEvent.ACTION_MOVE) {

                (activity as stateChangesInterface).stateOfTopBar(false)
                (activity as stateChangesInterface).stateOfConfirmBar(true)

                var canvas = Canvas(bitmap)

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
                (activity as stateChangesInterface).changeIvPhoto(bitmap)
            }

            true
        }

        activity!!.bConfirm!!.setOnClickListener {
            originalIvPhoto = (activity as stateChangesInterface).getIvPhoto()
            originalIvPhoto = originalIvPhoto.copy(Bitmap.Config.ARGB_8888, true)

            (activity as stateChangesInterface).stateOfConfirmBar(false)
            (activity as stateChangesInterface).stateOfTopBar(true)

            (activity as stateChangesInterface).addBitmapToMemoryCache(originalIvPhoto)
        }
        // Confirmation of changes


        // Revert changes
        activity!!.bCancel!!.setOnClickListener {
            (activity as stateChangesInterface).changeIvPhoto(originalIvPhoto)
            bitmap = originalIvPhoto.copy(Bitmap.Config.ARGB_8888, true)

            (activity as stateChangesInterface).stateOfConfirmBar(false)
            (activity as stateChangesInterface).stateOfTopBar(true)
        }
    }

    private fun changeIndicator (k : Int) {
        for (i in indicators.indices) {
            if (k == i) {
                indicators[i].visibility = View.VISIBLE
            } else {
                indicators[i].visibility = View.INVISIBLE
            }
        }
    }
}