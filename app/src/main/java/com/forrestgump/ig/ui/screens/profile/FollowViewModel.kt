package com.forrestgump.ig.ui.screens.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forrestgump.ig.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class FollowUiState(
    val isLoading: Boolean = true,
    val users: List<User> = emptyList(),
    val currentUser: User = User(),
    val isMyProfile: Boolean = true,
    val headerText: String = "",
    val username: String = ""
)

@HiltViewModel
class FollowViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(FollowUiState())
    val uiState: StateFlow<FollowUiState> = _uiState

    // Load followers or following based on type and user ID
    fun loadUserData(isFollower: Boolean, targetUserId: String? = null) {
        _uiState.update { it.copy(isLoading = true) }

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userId = targetUserId ?: currentUserId
        val isMyProfile = userId == currentUserId

        viewModelScope.launch {
            try {
                // Load the current user info first
                val currentUserDoc = firestore.collection("users").document(currentUserId).get().await()
                if (currentUserDoc.exists()) {
                    val currentUser = User(
                        userId = currentUserId,
                        fullName = currentUserDoc.getString("fullName") ?: "",
                        username = currentUserDoc.getString("username") ?: "",
                        profileImage = currentUserDoc.getString("profileImage") ?: "",
                        bio = currentUserDoc.getString("bio") ?: "",
                        followers = currentUserDoc.get("followers") as? List<String> ?: emptyList(),
                        following = currentUserDoc.get("following") as? List<String> ?: emptyList()
                    )

                    // Update current user in state
                    _uiState.update { it.copy(currentUser = currentUser) }

                    // If not my profile, load target user info to get username
                    var username = currentUser.username
                    if (!isMyProfile) {
                        val targetUserDoc = firestore.collection("users").document(userId).get().await()
                        if (targetUserDoc.exists()) {
                            username = targetUserDoc.getString("username") ?: ""
                        }
                    }

                    // Get IDs of users to load (followers or following)
                    val userIdsToLoad = if (isFollower) {
                        // Get followers
                        loadUserIds(userId, "followers")
                    } else {
                        // Get following
                        loadUserIds(userId, "following")
                    }

                    // If no followers/following, update state with empty list
                    if (userIdsToLoad.isEmpty()) {
                        _uiState.update { it.copy(
                            isLoading = false,
                            users = emptyList(),
                            isMyProfile = isMyProfile,
                            headerText = if (isFollower) "Người theo dõi" else "Đang theo dõi",
                            username = username
                        ) }
                        return@launch
                    }

                    // Load complete user data for each ID
                    val usersList = loadUsersData(userIdsToLoad)

                    // Update state with loaded users
                    _uiState.update { it.copy(
                        isLoading = false,
                        users = usersList,
                        isMyProfile = isMyProfile,
                        headerText = if (isFollower) "Người theo dõi" else "Đang theo dõi",
                        username = username
                    ) }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                Log.e("FollowViewModel", "Error loading users: ${e.message}")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Helper function to load user IDs from Firebase
    private suspend fun loadUserIds(userId: String, field: String): List<String> {
        val document = firestore.collection("users").document(userId).get().await()
        return if (document.exists()) {
            document.get(field) as? List<String> ?: emptyList()
        } else {
            emptyList()
        }
    }

    // Helper function to load complete user data for a list of user IDs
    private suspend fun loadUsersData(userIds: List<String>): List<User> {
        val users = mutableListOf<User>()

        for (userId in userIds) {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                val user = User(
                    userId = userId,
                    fullName = document.getString("fullName") ?: "",
                    username = document.getString("username") ?: "",
                    profileImage = document.getString("profileImage") ?: "",
                    bio = document.getString("bio") ?: "",
                    followers = document.get("followers") as? List<String> ?: emptyList(),
                    following = document.get("following") as? List<String> ?: emptyList()
                )
                users.add(user)
            }
        }

        return users
    }

    // Remove a follower
    fun removeFollower(followerUser: User) {
        val currentUser = _uiState.value.currentUser

        viewModelScope.launch {
            try {
                // 1. Remove follower from current user's followers list
                val updatedFollowers = currentUser.followers.toMutableList().apply {
                    remove(followerUser.userId)
                }

                // Update Firestore
                firestore.collection("users").document(currentUser.userId)
                    .update("followers", updatedFollowers)
                    .await()

                // 2. Remove current user from follower's following list
                val followerDoc = firestore.collection("users").document(followerUser.userId).get().await()
                if (followerDoc.exists()) {
                    val followerFollowing = followerDoc.get("following") as? List<String> ?: emptyList()
                    val updatedFollowing = followerFollowing.toMutableList().apply {
                        remove(currentUser.userId)
                    }

                    firestore.collection("users").document(followerUser.userId)
                        .update("following", updatedFollowing)
                        .await()
                }

                // 3. Update local state
                val updatedCurrentUser = currentUser.copy(followers = updatedFollowers)
                val updatedUsersList = _uiState.value.users.toMutableList().apply {
                    remove(followerUser)
                }

                _uiState.update { it.copy(
                    currentUser = updatedCurrentUser,
                    users = updatedUsersList
                ) }

            } catch (e: Exception) {
                Log.e("FollowViewModel", "Error removing follower: ${e.message}")
            }
        }
    }

    // Unfollow a user
    fun unfollowUser(followingUser: User) {
        val currentUser = _uiState.value.currentUser

        viewModelScope.launch {
            try {
                // 1. Remove user from current user's following list
                val updatedFollowing = currentUser.following.toMutableList().apply {
                    remove(followingUser.userId)
                }

                // Update Firestore
                firestore.collection("users").document(currentUser.userId)
                    .update("following", updatedFollowing)
                    .await()

                // 2. Remove current user from user's followers list
                val userDoc = firestore.collection("users").document(followingUser.userId).get().await()
                if (userDoc.exists()) {
                    val userFollowers = userDoc.get("followers") as? List<String> ?: emptyList()
                    val updatedFollowers = userFollowers.toMutableList().apply {
                        remove(currentUser.userId)
                    }

                    firestore.collection("users").document(followingUser.userId)
                        .update("followers", updatedFollowers)
                        .await()
                }

                // 3. Update local state
                val updatedCurrentUser = currentUser.copy(following = updatedFollowing)
                val updatedUsersList = _uiState.value.users.toMutableList().apply {
                    remove(followingUser)
                }

                _uiState.update { it.copy(
                    currentUser = updatedCurrentUser,
                    users = updatedUsersList
                ) }

            } catch (e: Exception) {
                Log.e("FollowViewModel", "Error unfollowing user: ${e.message}")
            }
        }
    }
}