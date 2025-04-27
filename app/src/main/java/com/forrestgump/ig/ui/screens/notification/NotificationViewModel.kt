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
        
        Log.d("NotificationViewModel", "Starting follow request rejection process for notificationId: ${notification.notificationId}, from sender: ${notification.senderId}")
        
        viewModelScope.launch {
            try {
                // Get the current user ID
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                
                // Verify the notification is for this user
                if (notification.receiverId != currentUserId) {
                    Log.e("NotificationViewModel", "Notification doesn't belong to current user")
                    return@launch
                }
                
                Log.d("NotificationViewModel", "Verified notification belongs to current user: $currentUserId")
                
                // Get current user data for the rejection notification
                val currentUserDoc = firestore.collection("users").document(currentUserId).get().await()
                if (!currentUserDoc.exists()) {
                    Log.e("NotificationViewModel", "Current user document doesn't exist")
                    return@launch
                }
                
                val currentUsername = currentUserDoc.getString("username") ?: ""
                val currentProfileImage = currentUserDoc.getString("profileImage") ?: ""
                
                Log.d("NotificationViewModel", "Retrieved user data: username=$currentUsername")
                
                // Use a batch for atomicity
                val batch = firestore.batch()
                
                // Get all follow requests from this sender
                val requestQuerySnapshot = firestore.collection("notifications")
                    .whereEqualTo("senderId", notification.senderId)
                    .whereEqualTo("receiverId", currentUserId)
                    .whereEqualTo("type", NotificationType.FOLLOW_REQUEST)
                    .get()
                    .await()
                
                Log.d("NotificationViewModel", "Found ${requestQuerySnapshot.size()} follow requests to delete")
                
                // Delete all these requests entirely instead of marking them as read
                for (doc in requestQuerySnapshot.documents) {
                    batch.delete(doc.reference)
                    Log.d("NotificationViewModel", "Added delete operation for follow request: ${doc.id}")
                }
                
                // Create a rejection notification for the sender (User A)
                val rejectionNotificationId = firestore.collection("notifications").document().id
                val rejectionNotification = Notification(
                    notificationId = rejectionNotificationId,
                    receiverId = notification.senderId,  // The original sender receives this
                    senderId = currentUserId,            // Current user (rejector) sends this
                    senderUsername = currentUsername,
                    senderProfileImage = currentProfileImage,
                    type = NotificationType.FOLLOW_REJECTED,  // We need to add this type
                    isRead = false
                )
                
                Log.d("NotificationViewModel", "Created rejection notification with ID: $rejectionNotificationId, type: ${rejectionNotification.type}")
                
                // Add the rejection notification to the batch
                batch.set(firestore.collection("notifications").document(rejectionNotificationId), rejectionNotification)
                
                // Commit all changes as a batch
                Log.d("NotificationViewModel", "Committing batch operations...")
                batch.commit().await()
                Log.d("NotificationViewModel", "Batch commit succeeded!")
                
                // Update local notification list by removing the rejected follow requests
                _notifications.value = _notifications.value.filter { notif ->
                    !(notif.senderId == notification.senderId 
                      && notif.receiverId == currentUserId
                      && notif.type == NotificationType.FOLLOW_REQUEST)
                }
                
                Log.d("NotificationViewModel", "Follow request rejection completed successfully. FOLLOW_REJECTED notification created with ID: $rejectionNotificationId")
                
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error rejecting follow request", e)
            }
        }
    }
}