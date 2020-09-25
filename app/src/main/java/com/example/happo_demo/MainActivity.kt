package com.example.happo_demo

import android.graphics.Bitmap.CompressFormat
import android.os.Bundle
import android.util.Log
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
  private lateinit var button: View
  private lateinit var chip: View
  private lateinit var switchOff: View
  private lateinit var switchOn: View
  private lateinit var progressBar: View

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    main = findViewById(R.id.main)
    label = findViewById(R.id.label)
    button = findViewById(R.id.button)
    chip = findViewById(R.id.chip)
    switchOff = findViewById(R.id.switchOff)
    switchOn = findViewById(R.id.switchOn)
    progressBar = findViewById(R.id.progressBar)

    main.post {
      runSuite()
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

  private fun runSuite() {
    Log.d("Happo","starting suite")
    doAsync {
      val results = listOf(
        processComponent(label, "Label", "default"),
        processComponent(button, "Button", "default"),
        processComponent(switchOn, "Switch", "on"),
        processComponent(switchOff, "Switch", "off"),
        processComponent(chip, "Chip", "default"),
        processComponent(progressBar, "Progress Bar", "half")
      )
      uiThread {
        Log.d("Happo","Done")
      }
    }
  }

  private fun processComponent(view: View, component: String, variant: String): Snapshot {
    val bitmap = view.drawToBitmap()
    val outStream = ByteArrayOutputStream()
    bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, outStream)
    val bits = outStream.toByteArray()
    val hash = md5hash(bits)
    val inStream = ByteArrayInputStream(outStream.toByteArray())
    val client =
      AmazonS3Client(BasicAWSCredentials(BuildConfig.S3_ACCESS_KEY, BuildConfig.S3_SECRET_KEY))
    val metadata = ObjectMetadata()
    metadata.contentType = "image/png"
    val res = client.putObject("happo-android-demo", "$hash.png", inStream, metadata)
    val snapshot = Snapshot(
      component,
      variant,
      "https://happo-android-demo.s3.eu-north-1.amazonaws.com/$hash.png",
      bitmap.width,
      bitmap.height
    )
    Log.d("Happo", "Processed $snapshot")
    return snapshot
  }

  data class Snapshot(
    val component: String,
    val variant: String,
    val url: String,
    val width: Int,
    val height: Int
  )
}