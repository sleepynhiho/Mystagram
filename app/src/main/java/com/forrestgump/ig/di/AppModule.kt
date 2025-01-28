package com.forrestgump.ig.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
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
}