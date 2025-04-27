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
                    uiState.update { it.copy(users = users, isLoading = false) }
                }
                .addOnFailureListener { exception ->
                    Log.e("SearchViewModel", "Error loading users", exception)
                    uiState.update { it.copy(isLoading = false) }
                }
                
            // Posts are loaded in loadRecommendedPosts() which is called after loadFriendSuggestions()
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
                
                // After updating friend suggestions, load posts from these users
                loadRecommendedPosts(friendSuggestions.map { it.userId })
                
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error loading friend suggestions", e)
            }
        }
    }
    
    // Function to load posts from recommended users
    private fun loadRecommendedPosts(suggestedUserIds: List<String>) {
        viewModelScope.launch {
            try {
                if (suggestedUserIds.isEmpty()) {
                    return@launch
                }
                
                // Get posts from Firestore
                val postsSnapshot = firestore.collection("posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
                
                val allPosts = postsSnapshot.documents.mapNotNull { 
                    it.toObject(Post::class.java) 
                }
                
                // Filter posts from suggested users
                val recommendedPosts = allPosts.filter { 
                    suggestedUserIds.contains(it.userId) 
                }
                
                // If we don't have enough posts from suggested users, add random posts
                val minPostCount = 10 // Minimum number of posts to show
                val randomPosts = if (recommendedPosts.size < minPostCount) {
                    // Get posts from users not in the suggestion list
                    val otherPosts = allPosts.filter { 
                        !suggestedUserIds.contains(it.userId) 
                    }
                    
                    // Shuffle and take enough to reach minPostCount
                    otherPosts.shuffled().take(minPostCount - recommendedPosts.size)
                } else {
                    emptyList()
                }
                
                // Combine recommended and random posts
                val finalPosts = (recommendedPosts + randomPosts).take(minPostCount)
                
                // Map to post suggestions
                val postSuggestions = finalPosts.map { post ->
                    PostSuggestion(
                        postId = post.postId,
                        userId = post.userId,
                        caption = post.caption ?: "",
                        imageUrl = post.mediaUrls.firstOrNull() ?: ""
                    )
                }
                
                // Update UI state with the post suggestions
                uiState.update { 
                    it.copy(
                        postSuggestions = postSuggestions,
                        posts = finalPosts
                    ) 
                }
                
                Log.d("SearchViewModel", "Loaded ${postSuggestions.size} post suggestions " +
                        "(${recommendedPosts.size} from recommended users, ${randomPosts.size} random)")
                
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error loading recommended posts", e)
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
            // Just reload basic data, friend suggestions will trigger post loading
            loadBasicData() 
            loadFriendSuggestions()
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
            
            // Filter posts by caption - search in all posts
            val allPosts = uiState.value.posts
            val filteredPosts = allPosts.filter { post ->
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