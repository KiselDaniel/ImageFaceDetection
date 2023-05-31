package com.example.imagefacedetection.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.imagefacedetection.data.model.Rectangle
import com.innovatrics.dot.face.lite.detection.FaceDetector

interface ImageProcessingRepositoryInterface {

    fun performFaceDetection(bitmap: Bitmap): FaceDetector.Result
    fun paintBoundingBoxToBitmap(bitmap: Bitmap, coordinates: Rectangle<Double>): Bitmap?
    fun saveBitmapToStorage(bitmap: Bitmap, context: Context, format: Bitmap.CompressFormat, mimeType: String): String?
}