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



class EditorActivity : AppCompatActivity() {


    var states: MutableList<Bitmap> = ArrayList()
    lateinit var buttons: Array<Button>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        bFilters.isSelected = true
        ivPhoto.setImageURI(intent.getParcelableExtra<Parcelable>("Image") as Uri)


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
        states.add(bitmapOld)
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
        states.add(bitmapOld)
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
        states.add(bitmapOld)
    }
}
