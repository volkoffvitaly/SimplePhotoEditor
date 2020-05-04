package com.example.photoeditor


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {

    companion object {
        val REQUEST_CAMERA: Int = 1
        val REQUEST_GALLERY: Int = 2
    }

    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bTakeANewPhoto.setOnClickListener {
            if (checkPermissions())
                TakeANewPhoto()
            else
                RequestPermissions()
        }

        bPickFromGallery.setOnClickListener {
            if (checkPermissions())
                PickFromGallery()
            else
                RequestPermissions()
        }
    }



    fun checkPermissions(): Boolean {
        var writeAccess = false
        var readAccess = false


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            readAccess = true
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            writeAccess = true
        }


        return (readAccess && writeAccess)
    }



    fun RequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        ActivityCompat.requestPermissions(this, permissions, 0)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                val uri = Uri.parse(currentPhotoPath)

                val intent = Intent(this, EditorActivity::class.java).apply {
                    putExtra("CameraImage", uri)
                }

                startActivity(intent)
            }

            else if (requestCode == REQUEST_GALLERY) {
                val uri = data!!.data

                val intent = Intent(this, EditorActivity::class.java).apply {
                    putExtra("GalleryImage", uri)
                }

                startActivity(intent)
            }
        }
    }



    fun CreateImageFile(): File {
        val imageFileName = "JPEG_temp"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)

        currentPhotoPath = image.absolutePath

        return image
    }



    fun TakeANewPhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            val pathToImage: Uri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", CreateImageFile())
            intent.putExtra(MediaStore.EXTRA_OUTPUT, pathToImage)
            startActivityForResult(intent, REQUEST_CAMERA)
        } catch (e: IOException) {
            Toast.makeText(getApplicationContext(), "Error! " + e, Toast.LENGTH_LONG).show()
        }
    }



    fun PickFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, REQUEST_GALLERY)
    }
}
