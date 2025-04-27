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
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.DocumentChange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlinx.coroutines.delay

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    var _uiState = MutableStateFlow(UserProfileUiState())
        private set
    val uiState: StateFlow<UserProfileUiState> = _uiState

    private var followResponseListener: ListenerRegistration? = null

    fun loadUserData(userId: String, forceReload: Boolean = false) {
        // Nếu đã load và không yêu cầu force reload, không làm gì
        if (!forceReload && _uiState.value.user.userId == userId && !_uiState.value.isLoading) {
            return
        }

        Log.d("UserProfileViewModel", "Loading user data for userId: $userId, forceReload: $forceReload")
        _uiState.update { it.copy(isLoading = true) }

        // Reset follow request pending state when loading a new user
        if (_uiState.value.user.userId != userId) {
            _uiState.update { it.copy(isFollowRequestPending = false) }
        }

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

        // Add a listener to detect follow request responses
        if (userId != currentUserId && currentUserId != null) {
            Log.d("UserProfileViewModel", "Setting up follow response listener for current user: $currentUserId viewing user: $userId")
            observeFollowResponses(currentUserId, userId)
            
            // Schedule a check to verify listener is working
            viewModelScope.launch {
                delay(2000) // Wait 2 seconds
                if (followResponseListener == null) {
                    Log.e("UserProfileViewModel", "Follow response listener is null after setup! Re-attempting...")
                    observeFollowResponses(currentUserId, userId)
                } else {
                    Log.d("UserProfileViewModel", "Follow response listener verified active")
                    
                    // Check if there's a pending follow request whenever viewing a profile
                    checkPendingFollowRequest(currentUserId, userId)
                }
            }
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

    private fun observeFollowResponses(currentUserId: String?, targetUserId: String) {
        // Remove any existing listener
        followResponseListener?.remove()
        followResponseListener = null
        
        Log.d("UserProfileViewModel", "Setting up observer for follow responses, currentUserId: $currentUserId, targetUserId: $targetUserId")
        
        if (currentUserId == null) {
            Log.e("UserProfileViewModel", "Cannot set up follow responses observer: currentUserId is null")
            return
        }
        
        // Set up a listener for both follow acceptance and rejection notifications
        try {
            followResponseListener = firestore.collection("notifications")
                .whereEqualTo("receiverId", currentUserId)
                .whereEqualTo("senderId", targetUserId)
                .whereIn("type", listOf(NotificationType.FOLLOW_ACCEPTED, NotificationType.FOLLOW_REJECTED))
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("UserProfileViewModel", "Error listening for follow responses", error)
                        return@addSnapshotListener
                    }
                    
                    val changes = snapshot?.documentChanges ?: emptyList()
                    Log.d("UserProfileViewModel", "Received snapshot update: documentChanges count: ${changes.size}")
                    
                    for (change in changes) {
                        // If a new notification is added
                        if (change.type == DocumentChange.Type.ADDED) {
                            val notification = change.document.toObject(Notification::class.java)
                            Log.d("UserProfileViewModel", "New notification detected: type=${notification.type}, sender=${notification.senderId}, receiver=${notification.receiverId}, id=${notification.notificationId}")
                            
                            when (notification.type) {
                                NotificationType.FOLLOW_ACCEPTED -> {
                                    Log.d("UserProfileViewModel", "Processing FOLLOW_ACCEPTED notification")
                                    // Update the UI state to reflect that the user is now followed
                                    updateFollowStateAfterAcceptance(currentUserId, targetUserId)
                                }
                                NotificationType.FOLLOW_REJECTED -> {
                                    Log.d("UserProfileViewModel", "Processing FOLLOW_REJECTED notification")
                                    // Update the UI state to reflect that the follow request was rejected
                                    updateFollowStateAfterRejection()
                                }
                                else -> { 
                                    Log.d("UserProfileViewModel", "Ignoring notification of type: ${notification.type}")
                                }
                            }
                        } else {
                            Log.d("UserProfileViewModel", "Document change type: ${change.type} (not handling)")
                        }
                    }
                }
            
            Log.d("UserProfileViewModel", "Follow response listener set up completed successfully")
        } catch (e: Exception) {
            Log.e("UserProfileViewModel", "Error setting up follow response listener", e)
        }
    }
    
    private fun updateFollowStateAfterAcceptance(currentUserId: String?, targetUserId: String) {
        if (currentUserId == null) {
            Log.e("UserProfileViewModel", "Cannot update follow state: currentUserId is null")
            return
        }
        
        viewModelScope.launch {
            try {
                // Get updated user data
                val currentUserDoc = firestore.collection("users").document(currentUserId).get().await()
                val targetUserDoc = firestore.collection("users").document(targetUserId).get().await()
                
                if (!currentUserDoc.exists() || !targetUserDoc.exists()) {
                    Log.e("UserProfileViewModel", "User document doesn't exist")
                    return@launch
                }
                
                val currentUserFollowing = currentUserDoc.get("following") as? List<String> ?: emptyList()
                val targetUserFollowers = targetUserDoc.get("followers") as? List<String> ?: emptyList()
                
                // Update UI state to reflect the new follow state
                _uiState.update { state ->
                    state.copy(
                        isCurrentUserFollowingThisUser = currentUserFollowing.contains(targetUserId),
                        isFollowRequestPending = false,
                        currentUser = state.currentUser.copy(
                            following = currentUserFollowing
                        ),
                        user = state.user.copy(
                            followers = targetUserFollowers
                        )
                    )
                }
                
                // If the target user is private, reload their posts now that we can see them
                if (_uiState.value.user.isPrivate) {
                    loadUserPosts(targetUserId)
                }
                
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error updating follow state after acceptance", e)
            }
        }
    }
    
    private fun updateFollowStateAfterRejection() {
        val currentUserId = _uiState.value.currentUser.userId
        val targetUserId = _uiState.value.user.userId
        
        Log.d("UserProfileViewModel", "Updating UI state after rejection: currentUserId=$currentUserId, targetUserId=$targetUserId")
        Log.d("UserProfileViewModel", "Before update - isFollowRequestPending: ${_uiState.value.isFollowRequestPending}, isFollowing: ${_uiState.value.isCurrentUserFollowingThisUser}")
        
        // First, ensure all pending requests are deleted from Firestore
        firestore.collection("notifications")
            .whereEqualTo("senderId", currentUserId)
            .whereEqualTo("receiverId", targetUserId)
            .whereEqualTo("type", NotificationType.FOLLOW_REQUEST)
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("UserProfileViewModel", "Found ${querySnapshot.size()} pending follow requests in Firestore")
                for (doc in querySnapshot.documents) {
                    doc.reference.delete()
                    Log.d("UserProfileViewModel", "Deleted pending follow request: ${doc.id}")
                }
                
                // CRITICAL: Update UI state AFTER Firestore operations, within the callback
                // Make absolutely sure we update the UI state with both flags set correctly
                _uiState.update { state ->
                    Log.d("UserProfileViewModel", "Inside update block - OLD values: isFollowRequestPending=${state.isFollowRequestPending}, isFollowing=${state.isCurrentUserFollowingThisUser}")
                    val updated = state.copy(
                        isFollowRequestPending = false,
                        isCurrentUserFollowingThisUser = false
                    )
                    Log.d("UserProfileViewModel", "Inside update block - NEW values: isFollowRequestPending=${updated.isFollowRequestPending}, isFollowing=${updated.isCurrentUserFollowingThisUser}")
                    updated
                }
                
                Log.d("UserProfileViewModel", "After update - isFollowRequestPending: ${_uiState.value.isFollowRequestPending}, isFollowing: ${_uiState.value.isCurrentUserFollowingThisUser}")
                Log.d("UserProfileViewModel", "UI should now show 'Theo dõi' button")
            }
            .addOnFailureListener { e ->
                Log.e("UserProfileViewModel", "Error checking for pending requests", e)
                
                // Update UI even in case of failure
                _uiState.update { state ->
                    state.copy(
                        isFollowRequestPending = false,
                        isCurrentUserFollowingThisUser = false
                    )
                }
            }
    }
    
    override fun onCleared() {
        super.onCleared()
        // Clean up the listener
        followResponseListener?.remove()
    }
}