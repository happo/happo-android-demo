package com.example.happo_demo

import android.graphics.Bitmap.CompressFormat
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {
  private lateinit var main: View
  private lateinit var label: TextView

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
        val bits = outStream.toByteArray()
        val hash = md5hash(bits)
        val inStream = ByteArrayInputStream(outStream.toByteArray())
        val client =
          AmazonS3Client(BasicAWSCredentials(BuildConfig.S3_ACCESS_KEY, BuildConfig.S3_SECRET_KEY))
        val metadata = ObjectMetadata()
        metadata.contentType = "image/png"
        client.putObject("happo-android-demo", "$hash.png", inStream, metadata)
        uiThread {
          label.text = hash
        }
      }
    }
  }

  private fun md5hash(input: ByteArray): String {
    return MessageDigest
      .getInstance("md5")
      .digest(input).toHex()
  }

  private fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
  }
}