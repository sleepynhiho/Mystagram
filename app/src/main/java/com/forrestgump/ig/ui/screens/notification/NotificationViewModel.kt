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
        if (notification.type != NotificationType.FOLLOW_REQUEST) return
        
        viewModelScope.launch {
            try {
                // Get the current user (receiver of the follow request)
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                
                // Get both users from Firestore
                val currentUserDoc = firestore.collection("users").document(currentUserId).get().await()
                val senderUserDoc = firestore.collection("users").document(notification.senderId).get().await()
                
                if (!currentUserDoc.exists() || !senderUserDoc.exists()) {
                    Log.e("NotificationViewModel", "User document doesn't exist")
                    return@launch
                }
                
                // Update follower/following relationships
                val currentUserFollowers = currentUserDoc.get("followers") as? List<String> ?: emptyList()
                val updatedFollowers = currentUserFollowers.toMutableList().apply {
                    if (!contains(notification.senderId)) add(notification.senderId)
                }
                
                val senderFollowing = senderUserDoc.get("following") as? List<String> ?: emptyList()
                val updatedFollowing = senderFollowing.toMutableList().apply {
                    if (!contains(currentUserId)) add(currentUserId)
                }
                
                // Update Firestore documents
                firestore.collection("users").document(currentUserId)
                    .update("followers", updatedFollowers)
                    .await()
                
                firestore.collection("users").document(notification.senderId)
                    .update("following", updatedFollowing)
                    .await()
                
                // Mark ALL follow requests from this sender as read
                firestore.collection("notifications")
                    .whereEqualTo("senderId", notification.senderId)
                    .whereEqualTo("receiverId", currentUserId)
                    .whereEqualTo("type", NotificationType.FOLLOW_REQUEST)
                    .get()
                    .await()
                    .documents
                    .forEach { doc ->
                        doc.reference.update("isRead", true)
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
                
                firestore.collection("notifications").document(acceptNotification.notificationId)
                    .set(acceptNotification)
                    .await()
                
                // Update local notification list
                _notifications.value = _notifications.value.map {
                    if (it.senderId == notification.senderId 
                        && it.receiverId == currentUserId
                        && it.type == NotificationType.FOLLOW_REQUEST) {
                        it.copy(isRead = true)
                    } else {
                        it
                    }
                }
                
                Log.d("NotificationViewModel", "Follow request accepted")
                
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error accepting follow request", e)
            }
        }
    }
    
    fun rejectFollowRequest(notification: Notification) {
        if (notification.type != NotificationType.FOLLOW_REQUEST) return
        
        viewModelScope.launch {
            try {
                // Get the current user ID
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                
                // Mark ALL follow requests from this sender as read
                firestore.collection("notifications")
                    .whereEqualTo("senderId", notification.senderId)
                    .whereEqualTo("receiverId", currentUserId)
                    .whereEqualTo("type", NotificationType.FOLLOW_REQUEST)
                    .get()
                    .await()
                    .documents
                    .forEach { doc ->
                        doc.reference.update("isRead", true)
                    }
                
                // Update local notification list
                _notifications.value = _notifications.value.map {
                    if (it.senderId == notification.senderId 
                        && it.receiverId == currentUserId
                        && it.type == NotificationType.FOLLOW_REQUEST) {
                        it.copy(isRead = true)
                    } else {
                        it
                    }
                }
                
                Log.d("NotificationViewModel", "Follow request rejected")
                
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error rejecting follow request", e)
            }
        }
    }
}