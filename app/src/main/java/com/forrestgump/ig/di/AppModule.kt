package com.forrestgump.ig.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.cloudinary.Cloudinary
import com.forrestgump.ig.BuildConfig
import com.forrestgump.ig.data.repositories.StoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Properties
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @UnstableApi
    @Singleton
    @Provides
    fun provideExoplayer(@ApplicationContext context: Context): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setHandleAudioBecomingNoisy(true)
            .setDeviceVolumeControlEnabled(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideCloudinary(): Cloudinary {
        val cloudName = BuildConfig.CLOUDINARY_CLOUD_NAME
        val apiKey = BuildConfig.CLOUDINARY_API_KEY
        val apiSecret = BuildConfig.CLOUDINARY_API_SECRET

        require(cloudName.isNotEmpty() && apiKey.isNotEmpty() && apiSecret.isNotEmpty()) {
            "Cloudinary credentials are missing!"
        }

        return Cloudinary("cloudinary://$apiKey:$apiSecret@$cloudName")
    }


    @Provides
    @Singleton
    fun provideStoryRepository(
        firestore: FirebaseFirestore,
        cloudinary: Cloudinary
    ): StoryRepository {
        return StoryRepository(firestore, cloudinary)
    }
}