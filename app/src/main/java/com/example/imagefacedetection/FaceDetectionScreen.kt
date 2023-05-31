package com.example.imagefacedetection

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun FaceDetectionScreen(
    viewModel: FaceDetectionViewModel,
    modifier: Modifier = Modifier)
{
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (uiState.editedImage != null) {
            AsyncImage(
                model = uiState.editedImage,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height((LocalConfiguration.current.screenHeightDp - 150).dp),
                contentScale = ContentScale.Crop,
            )
        }

        if(uiState.sharpness != null && uiState.brightness != null) {
            Text(
                text = "Sharpness: ${uiState.sharpness} and Brightness: ${uiState.brightness}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ErrorSnackBar(message: String) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { snackbarData ->
            Snackbar(snackbarData)
        }
    )

    scope.launch {
        snackbarHostState.showSnackbar(message)
    }
}