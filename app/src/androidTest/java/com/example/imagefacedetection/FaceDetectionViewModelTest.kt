package com.example.imagefacedetection

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.imagefacedetection.repository.ImageProcessingRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import javax.inject.Inject
//
//@Config(application = HiltTestApplication::class)
//@HiltAndroidTest
//@RunWith(AndroidJUnit4::class)
//class FaceDetectionViewModelTest {
//
//    @get:Rule
//    val hiltRule = HiltAndroidRule(this)
//
//    @Inject
//    lateinit var imageProcessingRepository: dagger.Lazy<ImageProcessingRepository>
//
//    private lateinit var viewModel: FaceDetectionViewModel
//
//    @Before
//    fun setUp() {
//        hiltRule.inject()
//        viewModel = FaceDetectionViewModel(imageProcessingRepository)
//    }
//
//    @Test
//    fun testFaceDetectionUiState() {
//        val uiState = viewModel.uiState.value
//
//        assert(!uiState.faceRecognised)
//        assert(uiState.errorMessage == null)
//        assert(uiState.pickedImageUri == null)
//        assert(uiState.pickedImage == null)
//        assert(uiState.editedImage == null)
//        assert(uiState.brightness == null)
//        assert(uiState.sharpness == null)
//        assert(uiState.rectangleCoordinates == null)
//    }
//}