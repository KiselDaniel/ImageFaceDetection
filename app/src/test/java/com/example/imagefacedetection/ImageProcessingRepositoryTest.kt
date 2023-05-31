package com.example.imagefacedetection

import android.app.Application
import android.graphics.Bitmap
import com.example.imagefacedetection.data.model.Rectangle
import com.example.imagefacedetection.repository.ImageProcessingRepository
import com.innovatrics.dot.face.lite.detection.FaceDetector
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(JUnit4::class)
class ImageProcessingRepositoryTest {

    private lateinit var faceDetector: FaceDetector
    private lateinit var appContext: Application
    private lateinit var repository: ImageProcessingRepository

    @Before
    fun setUp() {
        // Create mock objects for the FaceDetector and Application dependencies
        faceDetector = mock(FaceDetector::class.java)
        appContext = mock(Application::class.java)

        // Create an instance of the ImageProcessingRepository using the mock dependencies
        repository = ImageProcessingRepository(faceDetector, appContext)
    }

    @Test
    fun testPerformFaceDetection() {}
}