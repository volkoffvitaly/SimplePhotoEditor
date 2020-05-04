package com.example.photoeditor

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity



class EditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)


        val intent = intent

        if (intent.hasExtra("CameraImage")) {
            var ivPhoto = findViewById<ImageView>(R.id.ivPhoto)
            ivPhoto.setImageURI(intent.getParcelableExtra<Parcelable>("CameraImage") as Uri)
        }

        else if (intent.hasExtra("GalleryImage")){
            var ivPhoto = findViewById<ImageView>(R.id.ivPhoto)
            ivPhoto.setImageURI(intent.getParcelableExtra<Parcelable>("GalleryImage") as Uri)
        }

    }
}
