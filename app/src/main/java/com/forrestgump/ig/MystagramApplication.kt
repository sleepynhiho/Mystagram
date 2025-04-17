package com.forrestgump.ig

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.VideoFrameDecoder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.stripe.android.PaymentConfiguration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MystagramApplication : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .build()
    }
    override fun onCreate() {
        super.onCreate()
        PaymentConfiguration.init(
            applicationContext,
            BuildConfig.STRIPE_PUBLIC_KEY
        )
        // Check for premium expiration
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        auth.currentUser?.let { user ->
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val isPremium = document.getBoolean("premium") ?: false
                        val premiumDate = document.getTimestamp("premiumDate")?.toDate()

                        if (isPremium && premiumDate != null) {
                            // Check if premium has expired (30 days)
                            val currentTime = System.currentTimeMillis()
                            val premiumTime = premiumDate.time
                            val oneMonthInMillis = 30 * 24 * 60 * 60 * 1000L

                            if (currentTime - premiumTime > oneMonthInMillis) {
                                // Premium has expired, update status
                                firestore.collection("users").document(user.uid)
                                    .update("premium", false)
                            }
                        }
                    }
                }
        }
    }
}