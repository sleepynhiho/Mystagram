package com.forrestgump.ig.ui.screens.userprofile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forrestgump.ig.data.models.Notification
import com.forrestgump.ig.data.models.NotificationType
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    var _uiState = MutableStateFlow(UserProfileUiState())
        private set
    val uiState: StateFlow<UserProfileUiState> = _uiState

    fun loadUserData(userId: String, forceReload: Boolean = false) {
        // Nếu đã load và không yêu cầu force reload, không làm gì
        if (!forceReload && _uiState.value.user.userId == userId && !_uiState.value.isLoading) {
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        // Load the current user first
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            firestore.collection("users").document(currentUserId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val currentUser = User(
                            userId = currentUserId,
                            fullName = document.getString("fullName") ?: "",
                            username = document.getString("username") ?: "",
                            profileImage = document.getString("profileImage") ?: "",
                            bio = document.getString("bio") ?: "",
                            location = document.getString("location") ?: "",
                            followers = document.get("followers") as? List<String> ?: emptyList(),
                            following = document.get("following") as? List<String> ?: emptyList(),
                            isPrivate = document.getBoolean("private") ?: false,
                            isPremium = document.getBoolean("premium") ?: false,
                            premiumDate = document.getTimestamp("premiumDate")?.toDate()
                        )

                        // Now load the target user's data
                        loadTargetUserData(userId, currentUser)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("UserProfileViewModel", "Error getting current user data", exception)
                    _uiState.update { it.copy(isLoading = false) }
                }
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun loadTargetUserData(userId: String, currentUser: User) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = User(
                        userId = userId,
                        fullName = document.getString("fullName") ?: "",
                        username = document.getString("username") ?: "",
                        profileImage = document.getString("profileImage") ?: "",
                        bio = document.getString("bio") ?: "",
                        location = document.getString("location") ?: "",
                        followers = document.get("followers") as? List<String> ?: emptyList(),
                        following = document.get("following") as? List<String> ?: emptyList(),
                        isPrivate = document.getBoolean("private") ?: false,
                        isPremium = document.getBoolean("premium") ?: false,
                        premiumDate = document.getTimestamp("premiumDate")?.toDate()
                    )

                    val isCurrentUserFollowingThisUser = currentUser.following.contains(userId)
                    val isCurrentUserFollowedByThisUser = user.following.contains(currentUser.userId)

                    _uiState.update { it.copy(
                        user = user,
                        currentUser = currentUser,
                        isCurrentUserFollowingThisUser = isCurrentUserFollowingThisUser,
                        isCurrentUserFollowedByThisUser = isCurrentUserFollowedByThisUser
                    ) }

                    // Check if there's a pending follow request
                    if (user.isPrivate && !isCurrentUserFollowingThisUser) {
                        checkPendingFollowRequest(currentUser.userId, userId)
                        
                        // Also explicitly check if a request was rejected/accepted
                        checkFollowRequestStatus(currentUser.userId, userId)
                    }

                    // Load posts of the user if we're allowed to view them
                    if (!user.isPrivate || isCurrentUserFollowingThisUser) {
                        loadUserPosts(userId)
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("UserProfileViewModel", "Error getting user data", exception)
                _uiState.update { it.copy(isLoading = false) }
            }
    }

    // Check if there's a pending follow request
    private fun checkPendingFollowRequest(currentUserId: String, targetUserId: String) {
        firestore.collection("notifications")
            .whereEqualTo("senderId", currentUserId)
            .whereEqualTo("receiverId", targetUserId)
            .whereEqualTo("type", NotificationType.FOLLOW_REQUEST)
            .whereEqualTo("isRead", false)
            .get()
            .addOnSuccessListener { snapshot ->
                val isPending = !snapshot.isEmpty
                _uiState.update { it.copy(isFollowRequestPending = isPending) }
            }
    }

    private fun loadUserPosts(userId: String) {
        firestore.collection("posts")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val posts = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Post::class.java)
                }
                _uiState.update { it.copy(
                    posts = posts,
                    isLoading = false
                ) }
            }
            .addOnFailureListener { exception ->
                Log.e("UserProfileViewModel", "Error loading posts", exception)
                _uiState.update { it.copy(isLoading = false) }
            }
    }

    fun followUser() {
        val currentUser = _uiState.value.currentUser
        val targetUser = _uiState.value.user

        viewModelScope.launch {
            try {
                // If target user is private, send a follow request instead of direct follow
                if (targetUser.isPrivate && !_uiState.value.isCurrentUserFollowingThisUser) {
                    // First check if there's an existing follow request (read or unread)
                    firestore.collection("notifications")
                        .whereEqualTo("senderId", currentUser.userId)
                        .whereEqualTo("receiverId", targetUser.userId)
                        .whereEqualTo("type", NotificationType.FOLLOW_REQUEST)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                // Request already exists, update it to unread instead of creating a new one
                                val existingRequest = querySnapshot.documents.first()
                                existingRequest.reference.update("isRead", false)
                                    .addOnSuccessListener {
                                        _uiState.update { it.copy(isFollowRequestPending = true) }
                                    }
                            } else {
                                // No existing request, create a new one
                                val notificationId = firestore.collection("notifications").document().id
                                val notification = Notification(
                                    notificationId = notificationId,
                                    receiverId = targetUser.userId,
                                    senderId = currentUser.userId,
                                    senderUsername = currentUser.username,
                                    senderProfileImage = currentUser.profileImage,
                                    type = NotificationType.FOLLOW_REQUEST,
                                    isRead = false
                                )

                                // Add to notifications collection
                                firestore.collection("notifications").document(notificationId)
                                    .set(notification)
                                    .addOnSuccessListener {
                                        // Update local UI to show pending state
                                        _uiState.update { it.copy(isFollowRequestPending = true) }
                                    }
                            }
                        }
                } else {
                    // For public accounts, direct follow as before
                    // Update current user's following list
                    val currentUserRef = firestore.collection("users").document(currentUser.userId)
                    val updatedFollowing = currentUser.following.toMutableList().apply { add(targetUser.userId) }

                    currentUserRef.update("following", updatedFollowing)
                        .addOnSuccessListener {
                            // Update the target user's followers list
                            val targetUserRef = firestore.collection("users").document(targetUser.userId)
                            val updatedFollowers = targetUser.followers.toMutableList().apply { add(currentUser.userId) }

                            targetUserRef.update("followers", updatedFollowers)
                                .addOnSuccessListener {
                                    // Update UI state
                                    _uiState.update { state ->
                                        state.copy(
                                            isCurrentUserFollowingThisUser = true,
                                            currentUser = state.currentUser.copy(
                                                following = updatedFollowing
                                            ),
                                            user = state.user.copy(
                                                followers = updatedFollowers
                                            )
                                        )
                                    }

                                    // Create a notification for the follow
                                    val notificationId = firestore.collection("notifications").document().id
                                    val notification = Notification(
                                        notificationId = notificationId,
                                        receiverId = targetUser.userId,
                                        senderId = currentUser.userId,
                                        senderUsername = currentUser.username,
                                        senderProfileImage = currentUser.profileImage,
                                        type = NotificationType.FOLLOW,
                                        isRead = false
                                    )

                                    // Add to notifications collection
                                    firestore.collection("notifications").document(notificationId)
                                        .set(notification)

                                    // If the user is private and we just followed them, reload their posts
                                    if (targetUser.isPrivate) {
                                        loadUserPosts(targetUser.userId)
                                    }
                                }
                        }
                }
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error following user", e)
            }
        }
    }

    fun unfollowUser() {
        val currentUser = _uiState.value.currentUser
        val targetUser = _uiState.value.user

        viewModelScope.launch {
            try {
                // If there's a pending follow request, cancel it
                if (_uiState.value.isFollowRequestPending) {
                    firestore.collection("notifications")
                        .whereEqualTo("senderId", currentUser.userId)
                        .whereEqualTo("receiverId", targetUser.userId)
                        .whereEqualTo("type", NotificationType.FOLLOW_REQUEST)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            for (doc in querySnapshot.documents) {
                                doc.reference.delete()
                            }
                            _uiState.update { it.copy(isFollowRequestPending = false) }
                        }
                    return@launch
                }

                // Regular unfollow logic
                val currentUserRef = firestore.collection("users").document(currentUser.userId)
                val updatedFollowing = currentUser.following.toMutableList().apply { remove(targetUser.userId) }

                currentUserRef.update("following", updatedFollowing)
                    .addOnSuccessListener {
                        // Update the target user's followers list
                        val targetUserRef = firestore.collection("users").document(targetUser.userId)
                        val updatedFollowers = targetUser.followers.toMutableList().apply { remove(currentUser.userId) }

                        targetUserRef.update("followers", updatedFollowers)
                            .addOnSuccessListener {
                                // Update UI state
                                _uiState.update { state ->
                                    state.copy(
                                        isCurrentUserFollowingThisUser = false,
                                        currentUser = state.currentUser.copy(
                                            following = updatedFollowing
                                        ),
                                        user = state.user.copy(
                                            followers = updatedFollowers
                                        )
                                    )
                                }
                            }
                    }
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error unfollowing user", e)
            }
        }
    }

    fun getPostById(postId: String): Post? {
        return _uiState.value.posts.find { it.postId == postId }
    }

    // Check current status of a follow request (for updating sender's UI)
    fun checkFollowRequestStatus(senderId: String, receiverId: String) {
        // First check if the user is now following (accepted case)
        val isNowFollowing = _uiState.value.currentUser.following.contains(receiverId)
        
        if (isNowFollowing) {
            // Request was accepted, update UI
            _uiState.update { it.copy(
                isFollowRequestPending = false,
                isCurrentUserFollowingThisUser = true
            )}
            return
        }
        
        // Check for unread follow requests
        firestore.collection("notifications")
            .whereEqualTo("senderId", senderId)
            .whereEqualTo("receiverId", receiverId)
            .whereEqualTo("type", NotificationType.FOLLOW_REQUEST)
            .whereEqualTo("isRead", false)
            .get()
            .addOnSuccessListener { snapshot ->
                val hasPendingRequest = !snapshot.isEmpty
                
                if (!hasPendingRequest && _uiState.value.isFollowRequestPending) {
                    // No pending request but UI shows pending - means request was rejected
                    _uiState.update { it.copy(
                        isFollowRequestPending = false
                    )}
                    
                    Log.d("UserProfileViewModel", "Follow request was rejected")
                }
            }
    }
}