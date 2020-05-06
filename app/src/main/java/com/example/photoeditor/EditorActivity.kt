package com.example.photoeditor


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_editor.*



class EditorActivity : AppCompatActivity() {

    var states: MutableList<Bitmap> = ArrayList()
    lateinit var buttons: Array<Button>
    lateinit var fragments: Array<Fragment>
    lateinit var ivPhoto: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)


        ivPhoto = findViewById(R.id.ivPhoto)
        ivPhoto.setImageURI(intent.getParcelableExtra<Parcelable>("Image") as Uri)

        bFilters.isSelected = true

        buttons = arrayOf(
            findViewById(R.id.bFilters),
            findViewById(R.id.bRotate),
            findViewById(R.id.bZoom),
            findViewById(R.id.bHealing),
            findViewById(R.id.bUnsharpMasking),
            findViewById(R.id.bDraw),
            findViewById(R.id.bFiltration),
            findViewById(R.id.bSegmentation)
        )

        fragments = arrayOf(
            f_Filters(),
            f_Rotate(),
            f_Zoom(),
            f_Healing(),
            f_UnsharpMasking(),
            f_Draw(),
            f_Filtration(),
            f_Segmentation()
        )


        bBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        bUndo.setOnClickListener {

        }

        bRedo.setOnClickListener {

        }

        
        bFilters.setOnClickListener {
            turnButtons(0)
        }

        bRotate.setOnClickListener {
            turnButtons(1)
        }

        bZoom.setOnClickListener {
            turnButtons(2)
        }

        bHealing.setOnClickListener {
            turnButtons(3)
        }

        bUnsharpMasking.setOnClickListener {
            turnButtons(4)
        }

        bDraw.setOnClickListener {
            turnButtons(5)
        }

        bFiltration.setOnClickListener {
            turnButtons(6)
        }

        bSegmentation.setOnClickListener {
            turnButtons(7)
        }
    }

    fun turnButtons(k: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fPlace, fragments[k])
        transaction.commit()
        for (i in buttons.indices) {
            buttons[i].isSelected = i == k
        }
    }





    fun onNegativeFilter () {
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
