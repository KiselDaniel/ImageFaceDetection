package com.example.imagefacedetection

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

enum class ScreenNavigation (@StringRes val title: Int) {
    Home(title = R.string.home),
    FaceDetection(title = R.string.face_detection),
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FaceDetectionAppBar(
    currentScreen: ScreenNavigation,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

/**
 * Entry point composable for the face detector app.
 * This composable is the start destination of the [NavHost],
 * and handles the navigation to other screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceDetectionAppEntry(
    viewModel: FaceDetectionViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = ScreenNavigation.valueOf(
        backStackEntry?.destination?.route ?: ScreenNavigation.Home.name
    )

    Scaffold(
        topBar = {
            FaceDetectionAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = {
                    navController.navigateUp()
                    viewModel.resetState()
                }
            )
        })
    {   innerPadding ->

        NavHost(
            navController = navController,
            startDestination = ScreenNavigation.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = ScreenNavigation.Home.name) {
                HomeScreen(
                    context = LocalContext.current,
                    onNavigateToNextScreen = {
                        navController.navigate(ScreenNavigation.FaceDetection.name)
                    },
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
            }
            composable(route = ScreenNavigation.FaceDetection.name) {
                FaceDetectionScreen(
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
            }
        }
    }
}