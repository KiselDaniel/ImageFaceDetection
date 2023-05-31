package com.example.imagefacedetection.di

import android.app.Application
import com.example.imagefacedetection.data.ImageDataSource
import com.example.imagefacedetection.repository.ImageProcessingRepository
import com.example.imagefacedetection.repository.ImageProcessingRepositoryInterface
import com.innovatrics.dot.face.lite.detection.FaceDetector
import com.innovatrics.dot.face.lite.detection.FaceDetectorFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideImagaDataSource(): ImageDataSource {
        return ImageDataSource()
    }

    @Provides
    @Singleton
    fun provideFaceDetector(): FaceDetector {
        return FaceDetectorFactory.create()
    }

    @Provides
    @Singleton
    fun provideImageProcessingRepository(
        faceDetector: FaceDetector,
        appContext: Application
    ): ImageProcessingRepositoryInterface {
        return ImageProcessingRepository(
            faceDetector = faceDetector,
            appContext = appContext
        )
    }
}