package com.forrestgump.ig.ui.screens.userprofile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun loadUserData(userId: String) {
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
                            isPremium = document.getBoolean("premium") ?: false
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
                        isPremium = document.getBoolean("premium") ?: false
                    )

                    val isCurrentUserFollowingThisUser = currentUser.following.contains(userId)
                    val isCurrentUserFollowedByThisUser = user.following.contains(currentUser.userId)

                    _uiState.update { it.copy(
                        user = user,
                        currentUser = currentUser,
                        isCurrentUserFollowingThisUser = isCurrentUserFollowingThisUser,
                        isCurrentUserFollowedByThisUser = isCurrentUserFollowedByThisUser
                    ) }

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

                            // If the user is private and we just followed them, reload their posts
                            if (targetUser.isPrivate) {
                                loadUserPosts(targetUser.userId)
                            }
                        }
                }
        }
    }

    fun unfollowUser() {
        val currentUser = _uiState.value.currentUser
        val targetUser = _uiState.value.user

        viewModelScope.launch {
            // Update current user's following list
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
        }
    }

    fun getPostById(postId: String): Post? {
        return _uiState.value.posts.find { it.postId == postId }
    }
}