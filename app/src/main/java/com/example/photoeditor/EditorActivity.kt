package com.example.photoeditor


import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.LruCache
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
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
    fun getIvPhoto() : Bitmap
    fun addBitmapToMemoryCache(bitmap: Bitmap?)
}


class EditorActivity : AppCompatActivity(), stateChangesInterface {

    private val REQUEST_FILTERS: Int = 0
    private val REQUEST_ROTATE: Int = 1
    private val REQUEST_ZOOM: Int = 2
    private val REQUEST_UNSHARPMASKING: Int = 3
    private val REQUEST_DROW: Int = 4
    private val REQUEST_HEALING: Int = 5

    var CURRENT_FRAGMENT = REQUEST_FILTERS

    lateinit var buttons: Array<Button>

    private lateinit var memoryCache: LruCache<String, Bitmap>
    var maxElements = 5
    var currentKey = -1
    var countOfKeys = -1

    private var keys : MutableList<String> = ArrayList(maxElements)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        bFilters.isSelected = true
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fPlace, FiltersFragment())
        transaction.commit()

        confirmBar.visibility = View.INVISIBLE

        ivPhoto.setImageURI(intent.getParcelableExtra<Parcelable>("Image") as Uri)

        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {

            /*override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return 8
            }*/
        }

        memoryCache.put("original", (ivPhoto.drawable as BitmapDrawable).bitmap)
        addBitmapToMemoryCache((ivPhoto.drawable as BitmapDrawable).bitmap)

        bUndo.isEnabled = false



        buttons = arrayOf(
            bFilters,
            bRotate,
            bZoom,
            bUnsharpMasking,
            bDraw,
            bHealing
        )

        // Top Bar
        bBack.setOnClickListener {
            val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Exit the edit window?").setMessage("Сurrent result will be lost").setCancelable(true)

            builder.setPositiveButton("ok") {
                    dialog, which -> finish() // Закрываем активиити
            }
            builder.setNegativeButton("cancel") {
                    dialog, which -> dialog.dismiss() // Отпускает диалоговое окно
            }

            builder.create().show()
        }

        bUndo.setOnClickListener {

            memoryCache.remove(keys[currentKey])
            if (currentKey == 0) currentKey = maxElements

            currentKey--

            countOfKeys--

            ivPhoto.setImageBitmap(getBitmapFromMemCache(keys[currentKey]))

            if (countOfKeys == 0) bUndo.isEnabled = false


            when(CURRENT_FRAGMENT){
                REQUEST_FILTERS -> bFilters.callOnClick()
                REQUEST_ROTATE -> bRotate.callOnClick()
                REQUEST_ZOOM -> bZoom.callOnClick()
                REQUEST_UNSHARPMASKING -> bUnsharpMasking.callOnClick()
                REQUEST_DROW -> bDraw.callOnClick()
                REQUEST_HEALING -> bHealing.callOnClick()
            }

        }

        bCompare.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    bCompare.isSelected = true
                    ivPhoto.setImageBitmap(getBitmapFromMemCache("original"))
                }

                MotionEvent.ACTION_UP -> {
                    bCompare.isSelected = false
                    ivPhoto.setImageBitmap(getBitmapFromMemCache(keys[currentKey]))
                }
            }

            true
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
            change(REQUEST_FILTERS, FiltersFragment())
        }

        bRotate.setOnClickListener {
            change(REQUEST_ROTATE, RotateFragment())
        }

        bZoom.setOnClickListener {
            change(REQUEST_ZOOM, ZoomFragment())
        }

        bUnsharpMasking.setOnClickListener {
            change(REQUEST_UNSHARPMASKING, UnsharpMaskingFragment())
        }

        bDraw.setOnClickListener {
            change(REQUEST_DROW, DrawFragment())
        }

        bHealing.setOnClickListener {
            change(REQUEST_HEALING, HealingFragment())
        }
        // Bottom Bar
    }


    override fun onBackPressed(){
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Exit the edit window?").setMessage("Сurrent result will be lost").setCancelable(true)

        builder.setPositiveButton("ok") {
                dialog, which -> finish() // Закрываем активиити
        }
        builder.setNegativeButton("cancel") {
                dialog, which -> dialog.dismiss() // Отпускает диалоговое окно
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

        CURRENT_FRAGMENT = k

        ivPhoto.isEnabled = (k == REQUEST_DROW || k == REQUEST_HEALING)
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

        bUndo.isEnabled = (boolean && (countOfKeys > 0))
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

    override fun getIvPhoto() : Bitmap {
        return (ivPhoto.drawable as BitmapDrawable).bitmap
    }


    override fun addBitmapToMemoryCache(bitmap: Bitmap?) {
        val key = creatKey()
        keys.add(key)
        currentKey++
        if (countOfKeys < maxElements - 1) countOfKeys++

        if (currentKey == maxElements) {
            memoryCache.remove(keys[0])
            keys[0] = key
            currentKey = 0
        } else {
            memoryCache.remove(keys[currentKey])
            keys[currentKey] = key
        }

        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap)
        }

        if (countOfKeys > 0) bUndo.isEnabled = true
    }

    fun getBitmapFromMemCache(key: String?): Bitmap? {
        return memoryCache.get(key)
    }

    fun creatKey () : String {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return "IMG_$timeStamp"
    }
}
