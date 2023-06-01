package com.example.imagefacedetection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.imagefacedetection.data.FaceDetectionUiState
import com.example.imagefacedetection.data.model.Rectangle
import com.example.imagefacedetection.repository.ImageProcessingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import dagger.Lazy

@HiltViewModel
class FaceDetectionViewModel @Inject constructor(
    private val imageProcessingRepository: Lazy<ImageProcessingRepository>
): ViewModel() {

    init {
        imageProcessingRepository.get()
    }

    /**
     * FaceDetectionState is a data class that holds the state of the UI.
     */
    private val _uiState = MutableStateFlow(FaceDetectionUiState())
    val uiState: StateFlow<FaceDetectionUiState> = _uiState.asStateFlow()

    /**
     * Set the [pickedImage] for the face recognition screen's state.
     */
    fun setPickedImage(pickedImage: Bitmap?) {
        _uiState.update { currentState ->
            currentState.copy(pickedImage = pickedImage)
        }
    }

    /**
     * Set the [editedImage] with the bounding box for the face recognition screen's state.
     */
    fun setEditedImage(editedImage: Bitmap?) {
        _uiState.update { currentState ->
            currentState.copy(editedImage = editedImage)
        }
    }

    fun onPicturePicked(uri: Uri?) {
        _uiState.update { currentState ->
            currentState.copy(pickedImageUri = uri)
        }
    }

    fun setFaceRecognised(faceRecognised: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(faceRecognised = faceRecognised)
        }
    }

    fun setSharpness(sharpness: Double?) {
        _uiState.update { currentState ->
            currentState.copy(sharpness = sharpness)
        }
    }

    fun setBrightness(brightness: Double?) {
        _uiState.update { currentState ->
            currentState.copy(brightness = brightness)
        }
    }

    fun setRectangleCoordinates(rectangleCoordinates: Rectangle<Double>?) {
        _uiState.update { currentState ->
            currentState.copy(rectangleCoordinates = rectangleCoordinates)
        }
    }

    fun createErrorMessage(errorMessage: String) {
        _uiState.update { currentState ->
            currentState.copy(errorMessage = errorMessage)
        }
    }

    fun notifyErrorMessageShown() {
        _uiState.update { currentState ->
            currentState.copy(errorMessage = null)
        }
    }

    /**
     * Reset the state of the whole UI.
     */
    fun resetState() {
        _uiState.value = FaceDetectionUiState()
    }


    /**
     * Perform face detection on the given [bitmap] and update the UI state accordingly.
     */
    fun handleFaceDetectionResult(bitmap: Bitmap) {
        try {
            val result = imageProcessingRepository.get().performFaceDetection(bitmap)
            if (result.confidence < 0.5) {
                createErrorMessage("No face detected!")
                return
            }
            setFaceRecognised(true)

            result.imageParameters?.let { parameters ->
                setSharpness(parameters.sharpness)
                setBrightness(parameters.brightness)
            }

            result.normalizedRectangle?.let { rectangle ->
                setRectangleCoordinates(
                    Rectangle(
                        top = rectangle.top,
                        bottom = rectangle.bottom,
                        left = rectangle.left,
                        right = rectangle.right
                    )
                )
            }
        } catch (e: Exception) {
            createErrorMessage(e.message ?: "Unknown error")
        }
    }

    /**
     * Paint the bounding box to the [bitmap] on specified [coordinates].
     */
    fun paintBoundingBoxToBitmap(bitmap: Bitmap, coordinates: Rectangle<Double>): Bitmap? {
        return imageProcessingRepository.get().paintBoundingBoxToBitmap(bitmap, coordinates)
    }

    fun getBitmapFromUri(uri: Uri?, context: Context): Bitmap? {
        return try {
            imageProcessingRepository.get().getBitmapFromUri(uri, context)
        } catch (e: Exception) {
            createErrorMessage(e.message ?: "Unknown error")
            null
        }
    }

    fun saveBitmapToStorage(
        bitmap: Bitmap,
        context: Context,
        compressFormat: CompressFormat,
        mimeType: String
    ): String? {
        return  try {
            imageProcessingRepository.get().saveBitmapToStorage(bitmap = bitmap,
                context = context,
                format = compressFormat,
                mimeType = mimeType,
            )
        } catch (e: Exception) {
            createErrorMessage(e.message ?: "Unknown error")
            null
        }
    }
}