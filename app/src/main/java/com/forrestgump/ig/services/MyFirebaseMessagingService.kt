package com.forrestgump.ig.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.forrestgump.ig.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.firestore.FirebaseFirestore

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Called when a push message is received
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle notifications for chat messages
        remoteMessage.notification?.let {
            showChatNotification(it.title ?: "Tin nhắn mới", it.body ?: "")
        }

        // Handle custom data payload, such as notification data
        remoteMessage.data.isNotEmpty().let {
            val type = remoteMessage.data["type"]
            if (type == "notification") {
                val title = remoteMessage.data["title"] ?: "Thông báo mới"
                val message = remoteMessage.data["message"] ?: "Có thông báo mới"
                showSystemNotification(title, message)
            }
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

    // Save the FCM token to Firestore for the current user
    private fun saveFCMTokenToFirestore(userId: String, token: String) {
        FirebaseFirestore.getInstance().collection("users")
            .document(userId)
            .update("fcmToken", token)
            .addOnSuccessListener { println("FCM Token saved for $userId") }
            .addOnFailureListener { e -> println("Error saving FCM Token: $e") }
    }

    // Show a chat message notification
    private fun showChatNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "chat_notifications"

        val channel = NotificationChannel(channelId, "Chat Messages", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.default_profile_image) // Use your app's icon here
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(0, notification)
    }

    // Show a system notification (for generic notifications)
    private fun showSystemNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "system_notifications"

        // Create NotificationChannel for Android 8.0 and above
        val channel = NotificationChannel(channelId, "System Notifications", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.default_profile_image) // Use your app's icon here
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}

