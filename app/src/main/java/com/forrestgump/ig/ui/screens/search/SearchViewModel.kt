package com.forrestgump.ig.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val cloudinary: Cloudinary,
) : ViewModel() {

    var uiState = MutableStateFlow(UiState())
        private set
        
    private val currentUser = FirebaseAuth.getInstance().currentUser

    init {
        loadInitialSuggestions()
    }
    
    fun loadInitialSuggestions() {
        viewModelScope.launch {
            uiState.update { it.copy(isLoading = true) }
            
            // Get current user ID to exclude from results
            val currentUserId = currentUser?.uid ?: ""
            
            // Load suggested users (limited to 5)
            firestore.collection("users")
                .whereNotEqualTo("userId", currentUserId) // Exclude current user
                .limit(5)
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
                }
                .addOnFailureListener { exception ->
                    Log.e("SearchViewModel", "Error loading suggested users", exception)
                }
                
            // Load recent/popular posts (limited to 5)
            firestore.collection("posts")
                .orderBy("timestamp") // Sort by newest
                .limit(5)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val postSuggestions = querySnapshot.documents.mapNotNull { doc ->
                        val post = doc.toObject(Post::class.java)
                        post?.let {
                            PostSuggestion(
                                postId = it.postId,
                                userId = it.userId,
                                caption = it.caption,
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
                }
                .addOnFailureListener { exception ->
                    Log.e("SearchViewModel", "Error loading suggested posts", exception)
                    uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun loadAllData() {
        viewModelScope.launch {
            uiState.update { it.copy(isLoading = true) }

            // Fetch all users
            firestore.collection("users").get()
                .addOnSuccessListener { querySnapshot ->
                    val users = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(User::class.java)
                    }
                    uiState.update { it.copy(users = users) } // Ensure 'users' is part of UiState
                }
                .addOnFailureListener { exception ->
                    Log.e("SearchViewModel", "Error loading users", exception)
                }

            // Fetch all posts
            firestore.collection("posts").get()
                .addOnSuccessListener { querySnapshot ->
                    val posts = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Post::class.java)
                    }
                    uiState.update { it.copy(posts = posts, isLoading = false) }
                }
                .addOnFailureListener { exception ->
                    Log.e("SearchViewModel", "Error loading posts", exception)
                    uiState.update { it.copy(isLoading = false) }
                }
        }
    }
    
    fun searchSuggestions(query: String) {
        if (query.isEmpty()) {
            loadInitialSuggestions() // Show initial suggestions when search is cleared
            return
        }
        
        viewModelScope.launch {
            uiState.update { it.copy(isLoading = true) }
            
            // User suggestions - limit to 5 results
            firestore.collection("users")
                .orderBy("username")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(5)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val userSuggestions = querySnapshot.documents.mapNotNull { doc ->
                        // Only extract minimal data needed for suggestions
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
                }
                .addOnFailureListener { exception ->
                    Log.e("SearchViewModel", "Error loading user suggestions", exception)
                }
            
            // Post suggestions based on caption - limit to 5 results
            firestore.collection("posts")
                .orderBy("caption")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(5)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val postSuggestions = querySnapshot.documents.mapNotNull { doc ->
                        // Only extract minimal data needed for suggestions
                        val post = doc.toObject(Post::class.java)
                        post?.let {
                            PostSuggestion(
                                postId = it.postId,
                                userId = it.userId,
                                caption = it.caption,
                                imageUrl = it.mediaUrls.firstOrNull() ?: ""
                            )
                        }
                    }
                    uiState.update { it.copy(
                        postSuggestions = postSuggestions,
                        isLoading = false
                    )}
                }
                .addOnFailureListener { exception ->
                    Log.e("SearchViewModel", "Error loading post suggestions", exception)
                    uiState.update { it.copy(isLoading = false) }
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