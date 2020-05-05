package com.example.photoeditor


import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_editor.*



class EditorActivity : AppCompatActivity() {

    lateinit var buttons: Array<Button>
    lateinit var fragments: Array<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)


        var ivPhoto = findViewById<ImageView>(R.id.ivPhoto)
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
        for (i in buttons.indices) {
            buttons[i].isSelected = i == k

            if (i == k) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fPlace, fragments[i])
                transaction.commit()
            }
        }
    }
}
