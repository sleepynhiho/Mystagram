package com.forrestgump.ig.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.cloudinary.Cloudinary
import com.forrestgump.ig.data.repositories.StoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
        return Cloudinary("cloudinary://253743834726174:fhUOpO2-NXUavC7Kk7KoaN6CJKg@dmx7db2g6")
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