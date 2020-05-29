package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.filters_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.math.max
import kotlin.math.min

class FiltersFragment : Fragment() {

    lateinit var ivPhoto: Bitmap
    lateinit var names: Array<TextView>

    val alpha = -0x1000000

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.filters_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        ivPhoto = (activity as stateChangesInterface).getIvPhoto()

        setPreview()

        names = arrayOf(
            tContrast,
            tBright,
            tNegative,
            tSepia,
            tGray,
            tRed,
            tGreen,
            tBlue
        )

        var tempBitmap : Bitmap

        llBright.setOnClickListener() {
            doAsync {
                uiThread {
                    textSelectedOff()
                    tBright.isSelected = true

                    showConfirmBar(true)

                    enabledButtons(false)
                }

                tempBitmap = onBrightFilter(ivPhoto)

                uiThread {
                    (activity as stateChangesInterface).changeIvPhoto(tempBitmap)

                    enabledButtons(true)
                }
            }
        }

        llContrast.setOnClickListener() {
            doAsync {
                uiThread {
                    textSelectedOff()
                    tContrast.isSelected = true

                    showConfirmBar(true)

                    enabledButtons(false)
                }

                tempBitmap = onContrastFilter(ivPhoto)

                uiThread {
                    (activity as stateChangesInterface).changeIvPhoto(tempBitmap)

                    enabledButtons(true)
                }
            }
        }

        llNegative.setOnClickListener() {
            doAsync {
                uiThread {
                    textSelectedOff()
                    tNegative.isSelected = true

                    showConfirmBar(true)

                    enabledButtons(false)
                }

                tempBitmap = onNegativeFilter(ivPhoto)

                uiThread {
                    (activity as stateChangesInterface).changeIvPhoto(tempBitmap)

                    enabledButtons(true)
                }
            }
        }

        llSepia.setOnClickListener() {
            doAsync {
                uiThread {
                    textSelectedOff()
                    tSepia.isSelected = true

                    showConfirmBar(true)

                    enabledButtons(false)
                }

                tempBitmap = onSepiaFilter(ivPhoto)

                uiThread {
                    (activity as stateChangesInterface).changeIvPhoto(tempBitmap)

                    enabledButtons(true)
                }
            }
        }

        llGray.setOnClickListener() {
            doAsync {
                uiThread {
                    textSelectedOff()
                    tGray.isSelected = true

                    showConfirmBar(true)

                    enabledButtons(false)
                }

                tempBitmap = onGrayFilter(ivPhoto)

                uiThread {
                    (activity as stateChangesInterface).changeIvPhoto(tempBitmap)

                    enabledButtons(true)
                }
            }
        }

        llRed.setOnClickListener() {
            doAsync {
                uiThread {
                    textSelectedOff()
                    tRed.isSelected = true

                    showConfirmBar(true)

                    enabledButtons(false)
                }

                tempBitmap = onRedFilter(ivPhoto)

                uiThread {
                    (activity as stateChangesInterface).changeIvPhoto(tempBitmap)

                    enabledButtons(true)
                }
            }
        }

        llGreen.setOnClickListener() {
            doAsync {
                uiThread {
                    textSelectedOff()
                    tGreen.isSelected = true

                    showConfirmBar(true)

                    enabledButtons(false)
                }

                tempBitmap = onGreenFilter(ivPhoto)

                uiThread {
                    (activity as stateChangesInterface).changeIvPhoto(tempBitmap)

                    enabledButtons(true)
                }
            }
        }

        llBlue.setOnClickListener() {
            doAsync {
                uiThread {
                    textSelectedOff()
                    tBlue.isSelected = true

                    showConfirmBar(true)

                    enabledButtons(false)
                }

                tempBitmap = onBlueFilter(ivPhoto)

                uiThread {
                    (activity as stateChangesInterface).changeIvPhoto(tempBitmap)

                    enabledButtons(true)
                }
            }
        }

        activity!!.bConfirm!!.setOnClickListener {
            ivPhoto = (activity as stateChangesInterface).getIvPhoto()

            showConfirmBar(false)
            (activity as stateChangesInterface).stateOfTopBar(true)

            setPreview()
            textSelectedOff()
        }

        activity!!.bCancel!!.setOnClickListener {
            (activity as stateChangesInterface).changeIvPhoto(ivPhoto)

            showConfirmBar(false)
            (activity as stateChangesInterface).stateOfTopBar(true)

            textSelectedOff()
        }
    }



    private fun showConfirmBar(boolean: Boolean) {
        (activity as stateChangesInterface).stateOfConfirmBar(boolean)
    }


    private fun enabledButtons(boolean: Boolean) {

        // Блокируем/разблокируем кнопки фильтров
        for (i in 0 until llSelectFilters.childCount) {
            llSelectFilters.getChildAt(i).isEnabled = boolean
        }

        // Блокируем/разблокируем кнопки подтверждений
        (activity as stateChangesInterface).stateOfConfirmBarButtons(boolean)

        // Блок верхнего бара (разблокировка только после подтверждения или отклонения)
        (activity as stateChangesInterface).stateOfTopBar(false)

        // Показ анимации загрузки
        (activity as stateChangesInterface).stateOfProgressLoading(!boolean)

        // Смена цвета надписей для фильтров
        for (i in names.indices) {
            names[i].isEnabled = boolean
        }
    }


    private fun textSelectedOff() {
        for (i in names.indices) {
            names[i].isSelected = false
        }
    }


    private fun setPreview(){
        val prevBitmap : Bitmap

        var width : Int = 150
        var height : Int = 150
        if (ivPhoto.width > ivPhoto.height) {
            width = ((ivPhoto.width.toDouble() / ivPhoto.height) * 150).toInt()
        } else {
            height = ((ivPhoto.height.toDouble() / ivPhoto.width) * 150).toInt()
        }

        prevBitmap = Bitmap.createScaledBitmap(ivPhoto, width, height, false)

        bContrast.setImageBitmap(onContrastFilter(prevBitmap))
        bBright.setImageBitmap(onBrightFilter(prevBitmap))
        bNegative.setImageBitmap(onNegativeFilter(prevBitmap))
        bSepia.setImageBitmap(onSepiaFilter(prevBitmap))
        bGray.setImageBitmap(onGrayFilter(prevBitmap))
        bRed.setImageBitmap(onRedFilter(prevBitmap))
        bGreen.setImageBitmap(onGreenFilter(prevBitmap))
        bBlue.setImageBitmap(onBlueFilter(prevBitmap))
    }



              // <-- FILTERS ONLY --> //

    private fun onRedFilter(bitmap: Bitmap) : Bitmap {

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var oldPixel: Int

        var r: Int
        var g: Int
        var b: Int

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                oldPixel = pixels[bitmap.width * y + x]

                r = (oldPixel shr 16 and 0xff) + 10
                r = if (r > 255) 255 else r
                g = oldPixel shr 8 and 0xff
                b = oldPixel and 0xff

                pixels[bitmap.width * y + x] = alpha or (r shl 16) or (g shl 8) or b
            }
        }


        return Bitmap.createBitmap(pixels, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    }


    private fun onGreenFilter(bitmap: Bitmap) : Bitmap {

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var oldPixel: Int

        var r: Int
        var g: Int
        var b: Int

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                oldPixel = pixels[bitmap.width * y + x]

                r = oldPixel shr 16 and 0xff
                g = (oldPixel shr 8 and 0xff) + 10
                g = if (g > 255) 255 else g
                b = oldPixel and 0xff

                pixels[bitmap.width * y + x] = alpha or (r shl 16) or (g shl 8) or b
            }
        }


        return Bitmap.createBitmap(pixels, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    }


    private fun onBlueFilter(bitmap: Bitmap) : Bitmap {

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var oldPixel: Int

        var r: Int
        var g: Int
        var b: Int

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                oldPixel = pixels[bitmap.width * y + x]

                r = oldPixel shr 16 and 0xff
                g = oldPixel shr 8 and 0xff
                b = (oldPixel and 0xff) + 10
                b = if (b > 255) 255 else b

                pixels[bitmap.width * y + x] = alpha or (r shl 16) or (g shl 8) or b
            }
        }


        return Bitmap.createBitmap(pixels, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    }


    /*private fun onYellowFilter(bitmap: Bitmap) : Bitmap {

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var oldPixel: Int

        var r: Int
        var g: Int
        var b: Int

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                oldPixel = pixels[bitmap.width * y + x]

                r = (oldPixel shr 16 and 0xff) + 10
                r = if (r > 255) 255 else r
                g = (oldPixel shr 8 and 0xff) + 10
                g = if (g > 255) 255 else g
                b = oldPixel and 0xff

                pixels[bitmap.width * y + x] = alpha or (r shl 16) or (g shl 8) or b
            }
        }


        return Bitmap.createBitmap(pixels, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    }


    private fun onMagentaFilter(bitmap: Bitmap) : Bitmap {

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var oldPixel: Int

        var r: Int
        var g: Int
        var b: Int

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                oldPixel = pixels[bitmap.width * y + x]

                r = (oldPixel shr 16 and 0xff) + 10
                r = if (r > 255) 255 else r
                g = oldPixel shr 8 and 0xff
                b = (oldPixel and 0xff) + 10
                b = if (b > 255) 255 else b

                pixels[bitmap.width * y + x] = alpha or (r shl 16) or (g shl 8) or b
            }
        }


        return Bitmap.createBitmap(pixels, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    }


    private fun onCyanFilter(bitmap: Bitmap) : Bitmap {

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var oldPixel: Int

        var r: Int
        var g: Int
        var b: Int

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                oldPixel = pixels[bitmap.width * y + x]

                r = oldPixel shr 16 and 0xff
                g = (oldPixel shr 8 and 0xff) + 10
                g = if (g > 255) 255 else g
                b = (oldPixel and 0xff) + 10
                b = if (b > 255) 255 else b

                pixels[bitmap.width * y + x] = alpha or (r shl 16) or (g shl 8) or b
            }
        }


        return Bitmap.createBitmap(pixels, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    }*/


    private fun onNegativeFilter(bitmap: Bitmap) : Bitmap {

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var oldPixel: Int
        var r: Int
        var g: Int
        var b: Int

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                oldPixel = pixels[bitmap.width * y + x]

                r = 255 - (oldPixel shr 16 and 0xff)
                g = 255 - (oldPixel shr 8 and 0xff)
                b = 255 - (oldPixel and 0xff)

                pixels[bitmap.width * y + x] = alpha or (r shl 16) or (g shl 8) or b
            }
        }


        return Bitmap.createBitmap(pixels, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    }


    private fun onSepiaFilter(bitmap: Bitmap) : Bitmap {

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var oldPixel: Int
        var r: Int
        var g: Int
        var b: Int

        var red : Int
        var green : Int
        var blue : Int

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                oldPixel = pixels[bitmap.width * y + x]

                r = oldPixel shr 16 and 0xff
                g = oldPixel shr 8 and 0xff
                b = oldPixel and 0xff

                red = (r * 0.393 + g * 0.769 + b * 0.189).toInt()
                green = (r * 0.349 + g * 0.686 + b * 0.168).toInt()
                blue = (r * 0.272 + g * 0.534 + b * 0.131).toInt()

                red = if (red > 255) 255 else red
                green = if (green > 255) 255 else green
                blue = if (blue > 255) 255 else blue

                pixels[bitmap.width * y + x] = alpha or (red shl 16) or (green shl 8) or blue
            }
        }

        return Bitmap.createBitmap(pixels, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    }


    private fun onGrayFilter(bitmap: Bitmap) : Bitmap {

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var oldPixel: Int
        var r: Int
        var g: Int
        var b: Int

        var gray : Int

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                oldPixel = pixels[bitmap.width * y + x]

                r = oldPixel shr 16 and 0xff
                g = oldPixel shr 8 and 0xff
                b = oldPixel and 0xff
                gray = (r * 0.2126 + g * 0.7152 + b * 0.0722).toInt()

                pixels[bitmap.width * y + x] = alpha or (gray shl 16) or (gray shl 8) or gray
            }
        }

        return Bitmap.createBitmap(pixels, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    }


    private fun onBrightFilter(bitmap: Bitmap) : Bitmap {

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var oldPixel: Int
        var r: Int
        var g: Int
        var b: Int

        val k = 1.5

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                oldPixel = pixels[bitmap.width * y + x]

                r = oldPixel shr 16 and 0xff
                g = oldPixel shr 8 and 0xff
                b = oldPixel and 0xff

                r = min(255, (r * k).toInt())
                g = min(255, (g * k).toInt())
                b = min(255, (b * k).toInt())

                pixels[bitmap.width * y + x] = alpha or (r shl 16) or (g shl 8) or b
            }
        }

        return Bitmap.createBitmap(pixels, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    }


    private fun onContrastFilter(bitmap: Bitmap) : Bitmap {

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var oldPixel: Int
        var r: Int
        var g: Int
        var b: Int

        val k = 1.5
        var avg = 0.0

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                oldPixel = pixels[bitmap.width * y + x]

                r = oldPixel shr 16 and 0xff
                g = oldPixel shr 8 and 0xff
                b = oldPixel and 0xff

                avg += r * 0.299 + g * 0.587 + b * 0.114
            }
        }

        avg /= bitmap.width * bitmap.height

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                oldPixel = pixels[bitmap.width * y + x]

                r = oldPixel shr 16 and 0xff
                g = oldPixel shr 8 and 0xff
                b = oldPixel and 0xff

                r = min(255, max(0, (avg + k * (r - avg)).toInt()))
                g = min(255, max(0, (avg + k * (g - avg)).toInt()))
                b = min(255, max(0, (avg + k * (b - avg)).toInt()))

                pixels[bitmap.width * y + x] = alpha or (r shl 16) or (g shl 8) or b
            }
        }

        return Bitmap.createBitmap(pixels, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    }
}
