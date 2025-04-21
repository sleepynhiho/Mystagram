package com.forrestgump.ig.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.*

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val cloudinary: Cloudinary,
) : ViewModel() {

    var uiState = MutableStateFlow(UiState())
        private set
        
    private val currentUser = FirebaseAuth.getInstance().currentUser

    init {
        loadBasicData()
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
                                profilePicture = it.profileImage
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
                    profilePicture = user.profileImage
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
}

// Lightweight data classes for suggestions
data class UserSuggestion(
    val userId: String = "",
    val username: String = "",
    val fullName: String = "",
    val profilePicture: String = ""
)

data class PostSuggestion(
    val postId: String = "",
    val userId: String = "",
    val caption: String = "",
    val imageUrl: String = ""
)