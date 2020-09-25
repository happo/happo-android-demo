package com.example.happo_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import androidx.core.view.drawToBitmap

class MainActivity : AppCompatActivity() {
    private lateinit var main: View
    private lateinit var label: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main = findViewById(R.id.main)
        label = findViewById(R.id.label)
        main.post {
            val b = main.drawToBitmap()
            main.setBackgroundColor(Color.parseColor("#999999"))
        }
    }
}