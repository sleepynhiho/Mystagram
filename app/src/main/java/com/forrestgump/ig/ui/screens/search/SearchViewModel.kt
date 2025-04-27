package com.forrestgump.ig.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.data.repositories.FriendSuggestionRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val cloudinary: Cloudinary,
    private val friendSuggestionRepository: FriendSuggestionRepository
) : ViewModel() {

    var uiState = MutableStateFlow(UiState())
        private set
        
    private val currentUser = FirebaseAuth.getInstance().currentUser

    init {
        loadBasicData()
        loadFriendSuggestions()
    }
    
    // Simple function to load all users and posts
    fun loadBasicData() {
        viewModelScope.launch {
            uiState.update { it.copy(isLoading = true) }
            
            // Get current user ID to exclude from results
            val currentUserId = currentUser?.uid ?: ""
            
            // Load all users except current user
            firestore.collection("users")
                .whereNotEqualTo("userId", currentUserId) // Exclude current user
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val userSuggestions = querySnapshot.documents.mapNotNull { doc ->
                        val user = doc.toObject(User::class.java)
                        user?.let {
                            UserSuggestion(
                                userId = it.userId,
                                username = it.username,
                                fullName = it.fullName,
                                profilePicture = it.profileImage,
                                reason = ""
                            )
                        }
                    }
                    
                    uiState.update { it.copy(userSuggestions = userSuggestions) }
                    
                    // Also store complete user objects
                    val users = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(User::class.java)
                    }
                    uiState.update { it.copy(users = users) }
                }
                .addOnFailureListener { exception ->
                    Log.e("SearchViewModel", "Error loading users", exception)
                }
                
            // Load all posts
            firestore.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Sort by newest
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val postSuggestions = querySnapshot.documents.mapNotNull { doc ->
                        val post = doc.toObject(Post::class.java)
                        post?.let {
                            PostSuggestion(
                                postId = it.postId,
                                userId = it.userId,
                                caption = it.caption ?: "",
                                imageUrl = it.mediaUrls.firstOrNull() ?: ""
                            )
                        }
                    }
                    
                    uiState.update { 
                        it.copy(
                            postSuggestions = postSuggestions,
                            isLoading = false
                        ) 
                    }
                    
                    // Also store complete post objects
                    val posts = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Post::class.java)
                    }
                    uiState.update { it.copy(posts = posts) }
                }
                .addOnFailureListener { exception ->
                    Log.e("SearchViewModel", "Error loading posts", exception)
                    uiState.update { it.copy(isLoading = false) }
                }
        }
    }
    
    // Function to load friend suggestions
    fun loadFriendSuggestions() {
        viewModelScope.launch {
            try {
                val currentUserId = currentUser?.uid ?: return@launch
                
                // Get combined suggestions from the repository
                val suggestions = friendSuggestionRepository.getCombinedSuggestions(currentUserId)
                
                // Map to user suggestions and ensure reason is properly set
                val friendSuggestions = suggestions.map { suggestion ->
                    FriendSuggestion(
                        userId = suggestion.user.userId,
                        username = suggestion.user.username,
                        fullName = suggestion.user.fullName,
                        profilePicture = suggestion.user.profileImage,
                        // If repository returns empty reason, use default text
                        reason = if (suggestion.reason.isNotEmpty()) suggestion.reason else "Suggested for you"
                    )
                }
                
                // Log reasons for debugging
                friendSuggestions.forEach { 
                    Log.d("SearchViewModel", "Friend suggestion for ${it.username} with reason: ${it.reason}")
                }
                
                // Update the UI state with the new suggestions
                uiState.update {
                    it.copy(friendSuggestions = friendSuggestions)
                }
                
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error loading friend suggestions", e)
            }
        }
    }
    
    // Follow a suggested user
    fun followUser(userId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = currentUser?.uid ?: return@launch
                
                // Get current user document
                val currentUserDoc = firestore.collection("users").document(currentUserId)
                
                // Get target user document
                val targetUserDoc = firestore.collection("users").document(userId)
                
                // Update current user's following list
                firestore.runTransaction { transaction ->
                    val currentUserSnapshot = transaction.get(currentUserDoc)
                    val currentUserData = currentUserSnapshot.toObject(User::class.java)
                    
                    if (currentUserData != null) {
                        val updatedFollowing = currentUserData.following.toMutableList()
                        
                        if (!updatedFollowing.contains(userId)) {
                            updatedFollowing.add(userId)
                            transaction.update(currentUserDoc, "following", updatedFollowing)
                        }
                    }
                    
                    // Update target user's followers list
                    val targetUserSnapshot = transaction.get(targetUserDoc)
                    val targetUserData = targetUserSnapshot.toObject(User::class.java)
                    
                    if (targetUserData != null) {
                        val updatedFollowers = targetUserData.followers.toMutableList()
                        
                        if (!updatedFollowers.contains(currentUserId)) {
                            updatedFollowers.add(currentUserId)
                            transaction.update(targetUserDoc, "followers", updatedFollowers)
                        }
                    }
                }.await()
                
                // Remove the followed user from suggestions
                uiState.update { state ->
                    val updatedSuggestions = state.friendSuggestions.filter { it.userId != userId }
                    state.copy(friendSuggestions = updatedSuggestions)
                }
                
                // Reload friend suggestions to get new ones
                loadFriendSuggestions()
                
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error following user", e)
            }
        }
    }
    
    // Simple search function that filters locally instead of using complex Firestore queries
    fun searchSuggestions(query: String) {
        if (query.isEmpty()) {
            loadBasicData() // Show all data when search is cleared
            return
        }
        
        viewModelScope.launch {
            uiState.update { it.copy(isLoading = true) }
            
            // Filter users by username
            val filteredUsers = uiState.value.users.filter { user ->
                user.username.contains(query, ignoreCase = true) ||
                user.fullName.contains(query, ignoreCase = true) ||
                user.location.contains(query, ignoreCase = true)
            }
            
            // Create user suggestions from filtered users
            val userSuggestions = filteredUsers.map { user ->
                UserSuggestion(
                    userId = user.userId,
                    username = user.username,
                    fullName = user.fullName,
                    profilePicture = user.profileImage,
                    reason = ""
                )
            }
            
            // Filter posts by caption
            val filteredPosts = uiState.value.posts.filter { post ->
                post.caption?.contains(query, ignoreCase = true) ?: false
            }
            
            // Create post suggestions from filtered posts
            val postSuggestions = filteredPosts.map { post ->
                PostSuggestion(
                    postId = post.postId,
                    userId = post.userId,
                    caption = post.caption ?: "",
                    imageUrl = post.mediaUrls.firstOrNull() ?: ""
                )
            }
            
            // Update UI with filtered results
            uiState.update { 
                it.copy(
                    userSuggestions = userSuggestions,
                    postSuggestions = postSuggestions,
                    isLoading = false
                ) 
            }
        }
    }

    private fun clearUiState() {
        uiState.update { UiState() }
    }

    override fun onCleared() {
        super.onCleared()
        clearUiState()
    }

    // Add a function to get the current UI state
    fun getUiState(): UiState = uiState.value
}

// Lightweight data classes for suggestions
data class UserSuggestion(
    val userId: String = "",
    val username: String = "",
    val fullName: String = "",
    val profilePicture: String = "",
    val reason: String = ""
)

data class PostSuggestion(
    val postId: String = "",
    val userId: String = "",
    val caption: String = "",
    val imageUrl: String = ""
)

// New data class for friend suggestions
data class FriendSuggestion(
    val userId: String = "",
    val username: String = "",
    val fullName: String = "",
    val profilePicture: String = "",
    val reason: String = ""
)