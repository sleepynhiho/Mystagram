package com.forrestgump.ig.data.repositories

import com.forrestgump.ig.data.models.Notification
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun observeNotifications(currentUserId: String): Flow<List<Notification>> = callbackFlow {
        val listenerRegistration = firestore.collection("notifications")
            .whereEqualTo("receiverId", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val notifications = snapshot?.documents?.mapNotNull { it.toObject(Notification::class.java) }
                    ?: emptyList()
                trySend(notifications)
            }

        awaitClose { listenerRegistration.remove() }
    }
}