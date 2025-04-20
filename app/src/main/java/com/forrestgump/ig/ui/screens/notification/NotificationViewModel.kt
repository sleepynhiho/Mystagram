package com.forrestgump.ig.ui.screens.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forrestgump.ig.data.models.Notification
import com.forrestgump.ig.data.models.NotificationType
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.data.repositories.NotificationRepository
import com.forrestgump.ig.ui.screens.profile.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    var uiState = MutableStateFlow(UiState())
        private set

    private fun clearUiState() {
        uiState.update { UiState() }
    }

    override fun onCleared() {
        super.onCleared()
        clearUiState()
    }


    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications


    fun observeNotifications(currentUserId: String) {
        viewModelScope.launch {
            notificationRepository.observeNotifications(currentUserId).collect { newNotifications ->
                _notifications.value = newNotifications
            }
        }
    }
    
    fun acceptFollowRequest(notification: Notification) {
        // Don't process if it's not a follow request or if it's already been read (rejected)
        if (notification.type != NotificationType.FOLLOW_REQUEST || notification.isRead) {
            Log.d("NotificationViewModel", "Can't accept: not a valid follow request or already processed")
            return
        }
        
        viewModelScope.launch {
            try {
                // Get the current user (receiver of the follow request)
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                
                // Verify the notification is for this user
                if (notification.receiverId != currentUserId) {
                    Log.e("NotificationViewModel", "Notification doesn't belong to current user")
                    return@launch
                }
                
                // Get both users from Firestore
                val currentUserDoc = firestore.collection("users").document(currentUserId).get().await()
                val senderUserDoc = firestore.collection("users").document(notification.senderId).get().await()
                
                if (!currentUserDoc.exists() || !senderUserDoc.exists()) {
                    Log.e("NotificationViewModel", "User document doesn't exist")
                    return@launch
                }
                
                // Start a batch operation for atomicity
                val batch = firestore.batch()
                
                // Prepare follower/following relationships update
                val currentUserFollowers = currentUserDoc.get("followers") as? List<String> ?: emptyList()
                val updatedFollowers = currentUserFollowers.toMutableList().apply {
                    if (!contains(notification.senderId)) add(notification.senderId)
                }
                
                val senderFollowing = senderUserDoc.get("following") as? List<String> ?: emptyList()
                val updatedFollowing = senderFollowing.toMutableList().apply {
                    if (!contains(currentUserId)) add(currentUserId)
                }
                
                // Update user documents
                val currentUserRef = firestore.collection("users").document(currentUserId)
                batch.update(currentUserRef, "followers", updatedFollowers)
                
                val senderUserRef = firestore.collection("users").document(notification.senderId)
                batch.update(senderUserRef, "following", updatedFollowing)
                
                // Get all follow requests from this sender
                val requestQuerySnapshot = firestore.collection("notifications")
                    .whereEqualTo("senderId", notification.senderId)
                    .whereEqualTo("receiverId", currentUserId)
                    .whereEqualTo("type", NotificationType.FOLLOW_REQUEST)
                    .get()
                    .await()
                
                // Delete all these requests instead of marking them as read
                for (doc in requestQuerySnapshot.documents) {
                    batch.delete(doc.reference)
                }
                
                // Create a new notification to inform the user that their request was accepted
                val acceptNotification = Notification(
                    notificationId = firestore.collection("notifications").document().id,
                    receiverId = notification.senderId,
                    senderId = currentUserId,
                    senderUsername = currentUserDoc.getString("username") ?: "",
                    senderProfileImage = currentUserDoc.getString("profileImage") ?: "",
                    type = NotificationType.FOLLOW_ACCEPTED,
                    isRead = false
                )
                
                // Add the acceptance notification
                val acceptNotificationRef = firestore.collection("notifications").document(acceptNotification.notificationId)
                batch.set(acceptNotificationRef, acceptNotification)
                
                // Commit all changes as a batch
                batch.commit().await()
                
                // Update local notification list by removing the accepted follow requests
                _notifications.value = _notifications.value.filter { notif ->
                    !(notif.senderId == notification.senderId 
                      && notif.receiverId == currentUserId
                      && notif.type == NotificationType.FOLLOW_REQUEST)
                }
                
                Log.d("NotificationViewModel", "Follow request accepted and original notifications deleted successfully")
                
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error accepting follow request", e)
            }
        }
    }
    
    fun rejectFollowRequest(notification: Notification) {
        // Don't process if it's not a follow request or if it's already been read
        if (notification.type != NotificationType.FOLLOW_REQUEST || notification.isRead) {
            Log.d("NotificationViewModel", "Can't reject: not a valid follow request or already processed")
            return
        }
        
        viewModelScope.launch {
            try {
                // Get the current user ID
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                
                // Verify the notification is for this user
                if (notification.receiverId != currentUserId) {
                    Log.e("NotificationViewModel", "Notification doesn't belong to current user")
                    return@launch
                }
                
                // Use a batch for atomicity
                val batch = firestore.batch()
                
                // Get all follow requests from this sender
                val requestQuerySnapshot = firestore.collection("notifications")
                    .whereEqualTo("senderId", notification.senderId)
                    .whereEqualTo("receiverId", currentUserId)
                    .whereEqualTo("type", NotificationType.FOLLOW_REQUEST)
                    .get()
                    .await()
                
                // Delete all these requests entirely instead of marking them as read
                for (doc in requestQuerySnapshot.documents) {
                    batch.delete(doc.reference)
                }
                
                // Commit all changes as a batch
                batch.commit().await()
                
                // Update local notification list by removing the rejected follow requests
                _notifications.value = _notifications.value.filter { notif ->
                    !(notif.senderId == notification.senderId 
                      && notif.receiverId == currentUserId
                      && notif.type == NotificationType.FOLLOW_REQUEST)
                }
                
                Log.d("NotificationViewModel", "Follow request rejected and deleted successfully")
                
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error rejecting follow request", e)
            }
        }
    }
}