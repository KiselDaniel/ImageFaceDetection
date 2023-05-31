package com.example.imagefacedetection

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("SuspiciousIndentation")
@Composable
fun HomeScreen(
    context: Context,
    onNavigateToNextScreen: () -> Unit,
    viewModel: FaceDetectionViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.onPicturePicked(uri) }
    )

    // Observe changes in the view model's pickedImageUri state
    LaunchedEffect(uiState.pickedImageUri) {
        if (uiState.pickedImageUri != null) {

            // Set the picked image in the view model as Bitmap
            scope.launch(Dispatchers.IO) {
            val bitmap = getBitmapFromUri(context = context, uri = uiState.pickedImageUri)

                withContext(Dispatchers.Main) {
                    viewModel.setPickedImage(bitmap)

                }
            }
        }
    }

    // Observe changes in the view model's pickedImage state and handle face detection
    LaunchedEffect(uiState.pickedImage) {
        if (uiState.pickedImage != null &&
            uiState.pickedImageUri != null
        ) {

            // perform face detection on the picked image
            scope.launch(Dispatchers.Main) {
                viewModel.handleFaceDetectionResult(uiState.pickedImage!!)
            }

        }
    }

    // Observe changes in the view model's faceRecognised state and paint the bounding box
    LaunchedEffect(uiState.faceRecognised) {
        if(uiState.faceRecognised && uiState.rectangleCoordinates != null) {
            val editedImage = viewModel.paintBoundingBoxToBitmap(
                bitmap = uiState.pickedImage!!,
                coordinates = uiState.rectangleCoordinates!!
            )

            editedImage?.let {
                viewModel.setEditedImage(editedImage)

                // Save the edited image to the gallery
                viewModel.saveBitmapToStorage(
                    context = context,
                    bitmap = editedImage,
                    mimeType = "image/png",
                    compressFormat = Bitmap.CompressFormat.PNG)
            }

            // Navigate to the FaceDetectionScreen
            onNavigateToNextScreen()
        }
    }

    // Create a snackbar to notify user when the error happens
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            showSnackbar(scope, snackbarHostState, message = it)
            viewModel.notifyErrorMessageShown()
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.weight(1f))

        GradientButton(
            text = stringResource(R.string.pick_an_image),
            textColor = Color.White,
            gradient = Brush.horizontalGradient(
                colors = listOf(
                    Color(MaterialTheme.colorScheme.primary.toArgb()),
                    Color(MaterialTheme.colorScheme.tertiary.toArgb()),
                )
            ),
            onClick = {
                // Pick image from library
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        )

        SnackbarHost(
            hostState = snackbarHostState,
            snackbar = { data ->
                Snackbar(
                    snackbarData = data,
                    actionOnNewLine = false
                )
            }
        )

        Spacer(modifier = Modifier.weight(0.11f))
    }
}

@Composable
fun GradientButton(
    text: String,
    textColor: Color,
    gradient: Brush,
    onClick: () -> Unit
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.Transparent
        ),
        contentPadding = PaddingValues(),
        onClick = { onClick() })
    {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, color = textColor)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun GradientButtonPreview() {
    Scaffold (modifier = Modifier.fillMaxWidth()) {

        GradientButton(
            text = "Pick an image",
            textColor = Color.White,
            gradient = Brush.horizontalGradient(
                colors = listOf(
                    Color(MaterialTheme.colorScheme.primary.toArgb()),
                    Color(MaterialTheme.colorScheme.tertiary.toArgb()),
                )
            ),
            onClick = { /*TODO*/ }
        )
    }
}

fun getBitmapFromUri(uri: Uri?, context: Context): Bitmap? {
    uri?.let {
        val contentResolver = context.contentResolver
        return try {
            val inputStream = contentResolver.openInputStream(it)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    return null
}

fun showSnackbar(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    message: String
) {
    scope.launch {
        snackbarHostState.showSnackbar(message)
    }
}