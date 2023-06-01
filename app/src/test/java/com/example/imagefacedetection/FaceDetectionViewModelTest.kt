package com.example.imagefacedetection

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.imagefacedetection.data.model.Rectangle
import com.example.imagefacedetection.repository.ImageProcessingRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FaceDetectionViewModelTest {

    @Mock
    private lateinit var imageProcessingRepository: ImageProcessingRepository

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var uri: Uri

    private lateinit var faceDetectionViewModel: FaceDetectionViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        faceDetectionViewModel = FaceDetectionViewModel { imageProcessingRepository }
    }

    @Test
    fun setPickedImage() {
        val bitmap = Mockito.mock(Bitmap::class.java)
        faceDetectionViewModel.setPickedImage(bitmap)
        val pickedImage = faceDetectionViewModel.uiState.value.pickedImage
        assertEquals(bitmap, pickedImage)
    }

    @Test
    fun createErrorMessageUpdatesUiStateCorrectly() {
        val errorMessage = "Test Error Message"
        faceDetectionViewModel.createErrorMessage(errorMessage)

        val actualErrorMessage = faceDetectionViewModel.uiState.value.errorMessage
        assertEquals(errorMessage, actualErrorMessage)
    }

    @Test
    fun notifyErrorMessageShownShouldResetErrorMessage() {
        val errorMessage = "Test Error Message"
        faceDetectionViewModel.createErrorMessage(errorMessage)
        faceDetectionViewModel.notifyErrorMessageShown()

        val actualErrorMessage = faceDetectionViewModel.uiState.value.errorMessage
        assertNull(actualErrorMessage)
    }

    @Test
    fun handleFaceDetectionResultInteractsWithRepositoryCorrectly() {
        val bitmap = Mockito.mock(Bitmap::class.java)
        faceDetectionViewModel.handleFaceDetectionResult(bitmap)
        verify(imageProcessingRepository).performFaceDetection(bitmap)
    }

    @Test
    fun getBitmapFromUriHandlesExceptions() {
        `when`(imageProcessingRepository.getBitmapFromUri(any(), any())).thenThrow(RuntimeException("Test exception"))
        val result = faceDetectionViewModel.getBitmapFromUri(uri, context)
        assertNull(result)
        assertEquals("Test exception", faceDetectionViewModel.uiState.value.errorMessage)
    }

    @Test
    fun getBitmapFromUriReturnsExpectedBitmap() {
        val bitmap = Mockito.mock(Bitmap::class.java)
        `when`(imageProcessingRepository.getBitmapFromUri(uri, context)).thenReturn(bitmap)
        val result = faceDetectionViewModel.getBitmapFromUri(uri, context)
        assertEquals(bitmap, result)
    }

    @Test
    fun paintBoundingBoxToBitmapInteractsWithRepositoryCorrectly() {
        val bitmap = Mockito.mock(Bitmap::class.java)
        val rectangle = Rectangle(0.0, 0.5, 0.0, 0.5)
        faceDetectionViewModel.paintBoundingBoxToBitmap(bitmap, rectangle)
        verify(imageProcessingRepository).paintBoundingBoxToBitmap(bitmap, rectangle)
    }

    // any could be null which the compiler doesn't like
    fun <T> any(): T = Mockito.any<T>()

    @Test
    fun saveBitmapToStorageHandlesExceptions() {
        val bitmap = mock(Bitmap::class.java)
        `when`(imageProcessingRepository.saveBitmapToStorage(any(), any(), any(), any())).thenThrow(RuntimeException("Test exception"))
        val result = faceDetectionViewModel.saveBitmapToStorage(bitmap, mock(Context::class.java), Bitmap.CompressFormat.PNG, "image/png")
        assertNull(result)
        assertEquals("Test exception", faceDetectionViewModel.uiState.value.errorMessage)
    }

    @Test
    fun handleRepositoryExceptions() {
        val bitmap = mock(Bitmap::class.java)
        `when`(imageProcessingRepository.performFaceDetection(any())).thenThrow(RuntimeException("Face detection failed"))
        faceDetectionViewModel.handleFaceDetectionResult(bitmap)
        assertEquals("Face detection failed", faceDetectionViewModel.uiState.value.errorMessage)
    }
}