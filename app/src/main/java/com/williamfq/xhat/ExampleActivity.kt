// ExampleActivity.kt
package com.williamfq.xhat

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ExampleActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        imageView = findViewById(R.id.imageView)

        // Cargar una imagen con Glide
        Glide.with(this)
            .load("https://example.com/image.jpg")
            .into(imageView)
    }
}

