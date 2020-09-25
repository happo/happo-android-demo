package com.example.happo_demo

import android.graphics.Bitmap.CompressFormat
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import org.jetbrains.anko.doAsync
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {
  private lateinit var main: View
  private lateinit var label: View

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    main = findViewById(R.id.main)
    label = findViewById(R.id.label)
    main.post {
      doAsync {
        val bitmap = main.drawToBitmap()
        val outStream = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, outStream)
        val inStream = ByteArrayInputStream(outStream.toByteArray())
        val client =
          AmazonS3Client(BasicAWSCredentials(BuildConfig.S3_ACCESS_KEY, BuildConfig.S3_SECRET_KEY))
        val metadata = ObjectMetadata()
        metadata.contentType = "image/png"
        client.putObject("happo-android-demo", "happo-test.png", inStream, metadata)
        main.setBackgroundColor(Color.parseColor("#999999"))
      }
    }
  }
}