package com.example.photoeditor


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_editor.*
import kotlin.math.cos
import kotlin.math.sin


class EditorActivity : AppCompatActivity() {


    //var states: MutableList<Bitmap> = ArrayList()
    lateinit var oldBitmap: Bitmap
    lateinit var buttons: Array<Button>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        bFilters.isSelected = true
        ivPhoto.setImageURI(intent.getParcelableExtra<Parcelable>("Image") as Uri)
        oldBitmap = (ivPhoto.drawable as BitmapDrawable).bitmap

        buttons = arrayOf(
            bFilters,
            bRotate,
            bZoom,
            bHealing,
            bUnsharpMasking,
            bDraw,
            bFiltration,
            bSegmentation
        )


        // Top Bar
        bBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        bUndo.setOnClickListener {
            //
        }

        bRedo.setOnClickListener {
            //
        }

        bCompare.setOnClickListener {
            //
        }

        bSave.setOnClickListener {
            //
        }
        // Top Bar


        // Bottom Bar
        bFilters.setOnClickListener {
            turnButtons(0, FiltersFragment())
            onNegativeFilter()
        }

        bRotate.setOnClickListener {
            turnButtons(1, RotateFragment())
            onSepiaFilter()
        }

        bZoom.setOnClickListener {
            turnButtons(2, ZoomFragment())
            onGrayFilter()
        }

        bHealing.setOnClickListener {
            turnButtons(3, HealingFragment())
        }

        bUnsharpMasking.setOnClickListener {
            turnButtons(4, UnsharpMaskingFragment())
        }

        bDraw.setOnClickListener {
            turnButtons(5, DrawFragment())
        }

        bFiltration.setOnClickListener {
            turnButtons(6, FiltrationFragment())
        }

        bSegmentation.setOnClickListener {
            turnButtons(7, SegmentationFragment())
            rotateImage(30.0)
        }
        // Bottom Bar
    }




    fun turnButtons(k: Int, currentFragment: Fragment) {
        for (i in buttons.indices) {
            buttons[i].isSelected = i == k
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fPlace, currentFragment)
        transaction.commit()
    }


    fun onNegativeFilter() {
        val newBitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true)

        for (y in 0 until oldBitmap.height) {
            for (x in 0 until oldBitmap.width) {
                val oldPixel = oldBitmap.getPixel(x, y)

                val r = 255 - Color.red(oldPixel)
                val g = 255 - Color.green(oldPixel)
                val b = 255 - Color.blue(oldPixel)

                newBitmap.setPixel(x, y, Color.rgb(r, g ,b))
            }
        }

        ivPhoto.setImageBitmap(newBitmap)
        oldBitmap = newBitmap
        //states.add(oldBitmap)
    }


    fun onSepiaFilter() {
        val newBitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true)

        for (y in 0 until oldBitmap.height) {
            for (x in 0 until oldBitmap.width) {
                val oldPixel = oldBitmap.getPixel(x, y)

                val r = Color.red(oldPixel)
                val g = Color.green(oldPixel)
                val b = Color.blue(oldPixel)

                var red = (r * 0.393 + g * 0.769 + b * 0.189).toInt()
                var green = (r * 0.349 + g * 0.686 + b * 0.168).toInt()
                var blue = (r * 0.272 + g * 0.534 + b * 0.131).toInt()

                red = if (red > 255) 255 else red
                green = if (green > 255) 255 else green
                blue = if (blue > 255) 255 else blue

                newBitmap.setPixel(x, y, Color.rgb(red, green, blue))
            }
        }

        ivPhoto.setImageBitmap(newBitmap)
        oldBitmap = newBitmap
        //states.add(oldBitmap)
    }


    fun onGrayFilter() {
        val newBitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true)

        for (y in 0 until oldBitmap.height) {
            for (x in 0 until oldBitmap.width) {
                val oldPixel = oldBitmap.getPixel(x, y)

                val r = Color.red(oldPixel)
                val g = Color.green(oldPixel)
                val b = Color.blue(oldPixel)
                val grey = (r * 0.2126 + g * 0.7152 + b * 0.0722).toInt()

                newBitmap.setPixel(x, y, Color.rgb(grey, grey, grey))
            }
        }

        ivPhoto.setImageBitmap(newBitmap)
        oldBitmap = newBitmap
        //states.add(oldBitmap)
    }


    fun rotateImage(angle: Double) {
        val newBitmap = Bitmap.createBitmap(oldBitmap.width, oldBitmap.height, Bitmap.Config.ARGB_8888)

        val sinAngle: Double = sin(Math.toRadians(angle))
        val cosAngle: Double = cos(Math.toRadians(angle))
        val xCenter: Double = 0.5 * (oldBitmap.width - 1)
        val yCenter: Double = 0.5 * (oldBitmap.height - 1)


        for (y in 0 until oldBitmap.height) {
            for (x in 0 until oldBitmap.width) {

                val a = x - xCenter
                val b = y - yCenter

                val xNew = (a * cosAngle - b * sinAngle + xCenter).toInt()
                val yNew = (a * sinAngle + b * cosAngle + yCenter).toInt()

                if (0 <= xNew && xNew < oldBitmap.width && 0 <= yNew && yNew < oldBitmap.height)
                    newBitmap.setPixel(x, y, oldBitmap.getPixel(xNew, yNew))
            }
        }

        ivPhoto.setImageBitmap(newBitmap)
    }
}
