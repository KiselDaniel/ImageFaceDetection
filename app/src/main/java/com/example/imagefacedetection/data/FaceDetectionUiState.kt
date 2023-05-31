package com.example.imagefacedetection.data

import android.graphics.Bitmap
import android.net.Uri
import com.example.imagefacedetection.data.model.Rectangle

data class FaceDetectionUiState (
    val faceRecognised: Boolean = false,
    val errorMessage: String? = null,
    val pickedImageUri: Uri? = null,
    val pickedImage: Bitmap? = null,
    val editedImage: Bitmap? = null,
    val brightness: Double? = null,
    val sharpness: Double? = null,
    val rectangleCoordinates: Rectangle<Double>? = null
)