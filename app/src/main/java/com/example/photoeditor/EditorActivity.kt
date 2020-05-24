package com.example.photoeditor


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_editor.*
import org.jetbrains.anko.toast
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

interface stateChangesInterface {
    fun stateOfTopBar(boolean: Boolean)
    fun stateOfConfirmBar(boolean: Boolean)
    fun stateOfConfirmBarButtons(boolean: Boolean)
    fun stateOfProgressLoading(boolean: Boolean)
    fun changeIvPhoto(bitmap: Bitmap)
}


class EditorActivity : AppCompatActivity(), stateChangesInterface {

    //object States {
    //    var states: MutableList<Bitmap> = ArrayList()
    //}

    //lateinit var originalPhoto: Bitmap
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
        //originalPhoto = (ivPhoto.drawable as BitmapDrawable).bitmap
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
            val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Exit the edit window?")
                    .setMessage("Сurrent result will be lost")
                    .setCancelable(true)

            builder.setPositiveButton(
                "ok"
            ) { dialog, which ->
                finish() // Закрываем активиити
            }
            builder.setNegativeButton(
                "cancel"
            ) { dialog, which ->
                dialog.dismiss() // Отпускает диалоговое окно
            }

            builder.create().show()
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
            //ivPhoto.setImageBitmap(originalPhoto)
        }

        bSave.setOnClickListener {
            val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Save image?")
                .setCancelable(true)

            builder.setPositiveButton(
                "ok"
            ) { dialog, which ->
                saveImageToGallery((ivPhoto.drawable as BitmapDrawable).bitmap)
            }
            builder.setNegativeButton(
                "cancel"
            ) { dialog, which ->
                dialog.dismiss() // Отпускает диалоговое окно
            }

            builder.create().show()
        }
        // Top Bar


        // Bottom Bar
        bFilters.setOnClickListener {
            change(0, FiltersFragment())
        }

        bRotate.setOnClickListener {
            change(1, RotateFragment())
        }

        bZoom.setOnClickListener {
            change(2, ZoomFragment())
        }

        bHealing.setOnClickListener {
            change(3, HealingFragment())
        }

        bUnsharpMasking.setOnClickListener {
            change(4, UnsharpMaskingFragment())
        }

        bDraw.setOnClickListener {
            change(5, DrawFragment())
        }

        bFiltration.setOnClickListener {
            change(6, FiltrationFragment())
        }

        bSegmentation.setOnClickListener {
            change(7, SegmentationFragment())
        }
        // Bottom Bar
    }

    override fun onBackPressed(){
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Exit the edit window?")
            .setMessage("Сurrent result will be lost")
            .setCancelable(true)

        builder.setPositiveButton(
            "ok"
        ) { dialog, which ->
            finish() // Закрываем активиити
        }
        builder.setNegativeButton(
            "cancel"
        ) { dialog, which ->
            dialog.dismiss() // Отпускает диалоговое окно
        }

        builder.create().show()
    }

    private fun change(k: Int, currentFragment: Fragment) {
        // Changing buttons
        for (i in buttons.indices) {
            buttons[i].isSelected = i == k
        }

        // Changing fragments
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fPlace, currentFragment)
        transaction.commit()
    }


    private fun saveImageToGallery(bitmap:Bitmap) {
        // Classic title of the file with timestamp
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "IMG_$timeStamp"

        try {
            MediaStore.Images.Media.insertImage(contentResolver, bitmap, imageFileName, "Image of $title")
            toast("Successful")
        } catch (e: IOException) {
            toast("Error...")
        }
    }

    override fun stateOfTopBar(boolean: Boolean) {

        // кнопки верхнего бара
        bBack.isEnabled = boolean
        bUndo.isEnabled = boolean
        bRedo.isEnabled = boolean
        bCompare.isEnabled = boolean
        bSave.isEnabled = boolean

    }

    override fun stateOfConfirmBarButtons(boolean: Boolean) {

        // кнопки confirmBar'а
        for (i in 0 until confirmBar.childCount) {
            confirmBar.getChildAt(i).isEnabled = boolean
        }
    }

    override fun stateOfConfirmBar(boolean: Boolean) {

        // показ confirmBar'а
        if (boolean){
            confirmBar.visibility = View.VISIBLE
        } else {
            confirmBar.visibility = View.INVISIBLE
        }
    }

    override fun stateOfProgressLoading(boolean: Boolean) {

        // состояние анимации загрузки
        if (boolean){
            progressLoading.visibility = View.VISIBLE
        } else {
            progressLoading.visibility = View.INVISIBLE
        }

    }

    override fun changeIvPhoto(bitmap: Bitmap) {
        ivPhoto.setImageBitmap(bitmap)
    }
}
