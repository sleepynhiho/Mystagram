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
        // Load all posts and users first
        loadBasicData()
        // Then load friend suggestions, which will also load recommended posts
        loadFriendSuggestions()
    }
    
    // Function to clear caches
    fun clearCaches() {
        com.forrestgump.ig.ui.screens.search.UserCache.clearCache()
        com.forrestgump.ig.ui.screens.search.PostCache.clearCache()
    }
    
    // Simple function to load all users and posts
    fun loadBasicData() {
        // Clear caches first to ensure fresh data
        clearCaches()
        
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
                    uiState.update { it.copy(isLoading = false) }
                }
                
            // Load all posts for search functionality
            firestore.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { postsSnapshot ->
                    val allPosts = postsSnapshot.documents.mapNotNull { 
                        it.toObject(Post::class.java) 
                    }
                    
                    uiState.update { 
                        it.copy(
                            posts = allPosts,
                            isLoading = false
                        ) 
                    }
                    
                    Log.d("SearchViewModel", "Loaded ${allPosts.size} posts for search")
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
                    // If no suggested users, update UI with empty recommendations
                    uiState.update { 
                        it.copy(
                            postSuggestions = emptyList()
                        ) 
                    }
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
                
                // Filter posts from suggested users only
                val recommendedPosts = allPosts.filter { 
                    suggestedUserIds.contains(it.userId) 
                }
                
                // Group posts by user and take the most recent post from each user
                val onePostPerUser = recommendedPosts
                    .groupBy { it.userId } // Group by user ID
                    .mapValues { (_, posts) -> 
                        // Take the first post in each group (posts are already sorted by timestamp DESC)
                        posts.firstOrNull() 
                    }
                    .values
                    .filterNotNull() // Remove null values if any
                
                // Map to post suggestions (only use posts from suggested users)
                val postSuggestions = onePostPerUser.map { post ->
                    PostSuggestion(
                        postId = post.postId,
                        userId = post.userId,
                        caption = post.caption ?: "",
                        imageUrl = post.mediaUrls.firstOrNull() ?: ""
                    )
                }
                
                // Update UI state with the post suggestions ONLY
                // Don't update the complete posts list which is needed for search
                uiState.update { 
                    it.copy(
                        postSuggestions = postSuggestions
                    ) 
                }
                
                Log.d("SearchViewModel", "Loaded ${postSuggestions.size} post suggestions from recommended users (one per user)")
                
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
    
    // Function to search for posts and users by query
    fun searchSuggestions(
        query: String,
        filterByLocation: Boolean = false,
        location: String? = null,
        filterByTimeRange: Boolean = false,
        fromTime: String? = null,
        toTime: String? = null
    ) {
        if (query.isEmpty()) {
            // Clear caches to ensure fresh data on subsequent searches
            clearCaches()
            // Just reload basic data, friend suggestions will trigger post loading
            loadBasicData() 
            loadFriendSuggestions()
            return
        }
        
        viewModelScope.launch {
            uiState.update { it.copy(isLoading = true) }
            
            // Filter users based on criteria
            val filteredUsers = uiState.value.users.filter { user ->
                val matchesQuery = user.username.contains(query, ignoreCase = true) ||
                                  user.fullName.contains(query, ignoreCase = true)
                
                // Apply location filter if enabled
                val matchesLocation = if (filterByLocation && !location.isNullOrEmpty()) {
                    user.location.contains(location, ignoreCase = true)
                } else {
                    true // No location filter or empty location means all users match
                }
                
                matchesQuery && matchesLocation
            }
            
            // Create user suggestions from filtered users
            val userSuggestions = filteredUsers.map { user ->
                UserSuggestion(
                    userId = user.userId,
                    username = user.username,
                    fullName = user.fullName,
                    profilePicture = user.profileImage,
                    reason = if (filterByLocation && !location.isNullOrEmpty() && user.location.contains(location, ignoreCase = true)) 
                              "Lives in $location" else ""
                )
            }
            
            // Filter posts by criteria - using all posts from the database
            val allPosts = uiState.value.posts
            val filteredPosts = allPosts.filter { post ->
                // Match by caption content
                val matchesContent = post.caption?.contains(query, ignoreCase = true) ?: false
                
                // Apply time filter if enabled
                val matchesTimeRange = if (filterByTimeRange && (!fromTime.isNullOrEmpty() || !toTime.isNullOrEmpty())) {
                    try {
                        val postDate = post.timestamp
                        if (postDate != null) {
                            val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                            
                            val fromDate = if (!fromTime.isNullOrEmpty()) {
                                dateFormat.parse(fromTime)
                            } else null
                            
                            val toDate = if (!toTime.isNullOrEmpty()) {
                                dateFormat.parse(toTime)
                            } else null
                            
                            val isAfterFromDate = fromDate?.let { postDate.after(it) || postDate == it } ?: true
                            val isBeforeToDate = toDate?.let { postDate.before(it) || postDate == it } ?: true
                            
                            isAfterFromDate && isBeforeToDate
                        } else {
                            false
                        }
                    } catch (e: Exception) {
                        Log.e("SearchViewModel", "Error parsing dates", e)
                        true // If date parsing fails, include the post
                    }
                } else {
                    true // No time filter means all posts match
                }
                
                matchesContent && matchesTimeRange
            }
            
            // Group posts by user and take the most recent post from each user
            val onePostPerUser = filteredPosts
                .groupBy { it.userId } // Group by user ID
                .mapValues { (_, posts) -> 
                    // Take the first post in each group (posts are already sorted by timestamp DESC)
                    posts.firstOrNull() 
                }
                .values
                .filterNotNull()
            
            // Create post suggestions from filtered posts
            val postSuggestions = onePostPerUser.map { post ->
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
            
            Log.d("SearchViewModel", "Search results: ${userSuggestions.size} users, ${postSuggestions.size} posts")
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