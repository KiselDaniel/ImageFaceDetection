package com.example.imagefacedetection.repository

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.imagefacedetection.R
import com.example.imagefacedetection.data.model.Rectangle
import com.innovatrics.dot.face.lite.detection.FaceDetector
import com.innovatrics.dot.image.BgraRawImageFactory
import java.io.OutputStream
import javax.inject.Inject

class ImageProcessingRepository @Inject constructor (
    private val faceDetector: FaceDetector,
    private val appContext: Application
) : ImageProcessingRepositoryInterface {

    // Test print to check that the dependency injection works as expected...
    init {
        val appName = appContext.getString(R.string.app_name)
        println("Hello from the ${ImageProcessingRepository::class.java}. The app name is $appName")
        println("Hello from the ${ImageProcessingRepository::class.java}. The face detector is $faceDetector")
    }

    override fun performFaceDetection(bitmap: Bitmap): FaceDetector.Result {
        val bgraRawImage = BgraRawImageFactory.create(bitmap)
        return faceDetector.detect(bgraRawImage)
    }

    override fun paintBoundingBoxToBitmap(
        bitmap: Bitmap,
        coordinates: Rectangle<Double>
    ): Bitmap? {
        // Create a new mutable Bitmap with the same dimensions as the original Bitmap
        val mutableBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        // Create a Canvas to draw on the new Bitmap
        val canvas = Canvas(mutableBitmap)

        // Draw the original Bitmap onto the new Bitmap
        canvas.drawBitmap(bitmap, 0f, 0f, null)

        // Create a Paint object to draw the rectangle
        val paint = Paint().apply {
            color = Color.Green.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }

        // Scale the normalized coordinates to the dimensions of the Bitmap
        val left = (coordinates.left ?: 0.0) * bitmap.width
        val right = (coordinates.right ?: 0.0) * bitmap.width
        val top = (coordinates.top ?: 0.0) * bitmap.height
        val bottom = (coordinates.bottom ?: 0.0) * bitmap.height

        // Draw the rectangle onto the new Bitmap
        canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)

        return mutableBitmap
    }

    override fun saveBitmapToStorage(
        bitmap: Bitmap,
        context: Context,
        format: Bitmap.CompressFormat,
        mimeType: String
    ): String? {
        val filename = "image.${mimeType.split("/").last()}"
        var outputStream: OutputStream? = null
        var uri: Uri? = null

        context.contentResolver?.also { resolver ->
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            outputStream = uri?.let { resolver.openOutputStream(it) }
        }

        outputStream?.use { stream ->
            bitmap.compress(format, 100, stream)
        }

        return uri?.toString()
    }
}