package com.forrestgump.ig.ui.screens.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
) : ViewModel() {

    var uiState = MutableStateFlow(UiState())
        private set


    fun loadUserData() {
        val userFromFB = FirebaseAuth.getInstance().currentUser
        userFromFB?.let { user ->
            val userId = user.uid
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val updatedUser = User(
                            userId = userId,
                            fullName = document.getString("fullName") ?: "",
                            username = document.getString("username") ?: "",
                            profileImage = document.getString("profileImage") ?: "@drawable/default_profile_image",
                            bio = document.getString("bio") ?: "No Bio yet",
                            location = document.getString("location") ?: "",
                            // followers và following được lưu là List<String> trong document
                            followers = document.get("followers") as? List<String> ?: emptyList(),
                            following = document.get("following") as? List<String> ?: emptyList()
                        )
                        uiState.update { currentState ->
                            currentState.copy(isLoading = false, curUser = updatedUser)
                        }
                        // Sau đó, load thông tin posts của user
                        db.collection("posts")
                            .whereEqualTo("userId", userId)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                val posts = querySnapshot.documents.mapNotNull { doc ->
                                    doc.toObject(Post::class.java)
                                }
                                uiState.update { currentState ->
                                    currentState.copy(
                                        postCount = posts.size,
                                        posts = posts
                                    )
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e("ProfileViewModel", "Error loading posts", exception)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileViewModel", "Error getting user data", exception)
                    // Cập nhật lại state nếu cần thông báo lỗi...
                    uiState.update { currentState ->
                        currentState.copy(isLoading = false)
                    }
                }
        } ?: run {
            // Nếu user là null
            uiState.update { currentState ->
                currentState.copy(isLoading = false)
            }
        }
    }

    fun getPostById(postId: String): Post? {
        return uiState.value.posts.find { it.postId == postId }
    }

    private fun clearUiState() {
        uiState.update { UiState() }
    }

    override fun onCleared() {
        super.onCleared()
        clearUiState()
    }
}