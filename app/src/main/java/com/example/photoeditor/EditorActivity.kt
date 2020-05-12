package com.example.photoeditor

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_editor.*


class EditorActivity : AppCompatActivity() {

    //object States {
    //    var states: MutableList<Bitmap> = ArrayList()
    //}

    lateinit var buttons: Array<Button>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        bFilters.isSelected = true
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fPlace, FiltersFragment())
        transaction.commit()

        confirmBar.visibility = View.INVISIBLE

        ivPhoto.setImageURI(intent.getParcelableExtra<Parcelable>("Image") as Uri)
        //States.states.add((ivPhoto.drawable as BitmapDrawable).bitmap)

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
            //if (1 < States.states.size) {
            //    States.states.removeAt(States.states.size - 1)
            //    ivPhoto.setImageBitmap(States.states[States.states.size - 1])
            //}
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

    private fun turnButtons(k: Int, currentFragment: Fragment) {
        for (i in buttons.indices) {
            buttons[i].isSelected = i == k
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fPlace, currentFragment)
        transaction.commit()
    }
}
