package com.forrestgump.ig.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.forrestgump.ig.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.firestore.FirebaseFirestore

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            showNotification(it.title ?: "Tin nhắn mới", it.body ?: "")
        }
    }

    // Handling FCM token refresh
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Get the current user ID if available and save the new token
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            saveFCMTokenToFirestore(it, token)
        }
    }

    private fun saveFCMTokenToFirestore(userId: String, token: String) {
        FirebaseFirestore.getInstance().collection("users")
            .document(userId)
            .update("fcmToken", token)
            .addOnSuccessListener { println("FCM Token saved for $userId") }
            .addOnFailureListener { e -> println("Error saving FCM Token: $e") }
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "chat_notifications"

        val channel = NotificationChannel(channelId, "Chat Messages", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.default_profile_img)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(0, notification)
    }
}
