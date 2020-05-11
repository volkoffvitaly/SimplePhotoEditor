package com.example.photoeditor


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_editor.*

class EditorActivity : AppCompatActivity() {


    //var states: MutableList<Bitmap> = ArrayList()
    lateinit var buttons: Array<Button>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        bFilters.isSelected = true
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fPlace, FiltersFragment())
        transaction.commit()

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
        }

        bRotate.setOnClickListener {
            turnButtons(1, RotateFragment())
        }

        bZoom.setOnClickListener {
            turnButtons(2, ZoomFragment())
            fZoom(50)
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

    fun fZoom(procent: Int){
        val bitmapOld = (ivPhoto.drawable as BitmapDrawable).bitmap
        val bitmapNew = Bitmap.createBitmap((bitmapOld.width * procent / 100), (bitmapOld.height * procent / 100), Bitmap.Config.ARGB_8888)

        val startX: Int = (bitmapOld.width - (bitmapOld.width * procent / 100)) / 2
        val startY: Int = (bitmapOld.height - (bitmapOld.height * procent / 100)) / 2

        for (y in 0 until bitmapNew.height){
            for (x in 0 until bitmapNew.width){
                val oldPixel = bitmapOld.getPixel(startX + x, startY + y)
                bitmapNew.setPixel(x, y, oldPixel)
            }
        }
        ivPhoto.setImageBitmap(bitmapNew)
    }
}
