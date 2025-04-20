package com.forrestgump.ig.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.data.repositories.PromotedPostRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class PostOptionsUiState(
    val isPostPromoted: Boolean = false,
    val isPromoting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isDeleting: Boolean = false,
    val isPostDeleted: Boolean = false
)

@HiltViewModel
class PostOptionsViewModel @Inject constructor(
    private val promotedPostRepository: PromotedPostRepository,
    private val firestore: FirebaseFirestore  // Inject Firestore directly
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostOptionsUiState())
    val uiState: StateFlow<PostOptionsUiState> = _uiState

    fun checkIfPostIsPromoted(postId: String) {
        viewModelScope.launch {
            val isPromoted = promotedPostRepository.isPostPromoted(postId)
            _uiState.update { it.copy(isPostPromoted = isPromoted) }
        }
    }

    fun promotePost(postId: String, userId: String, isPremium: Boolean) {
        viewModelScope.launch {
            // Recheck premium status from Firestore
            val userDoc = firestore.collection("users").document(userId).get().await()
            val currentIsPremium = userDoc.getBoolean("premium") ?: false

            if (!currentIsPremium) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Bạn cần nâng cấp tài khoản Premium để sử dụng tính năng này"
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isPromoting = true,
                    errorMessage = null,
                    successMessage = null
                )
            }

            val success = promotedPostRepository.promotePost(postId, userId)

            if (success) {
                _uiState.update {
                    it.copy(
                        isPromoting = false,
                        isPostPromoted = true,
                        successMessage = "Đã thêm bài viết vào danh sách quảng cáo!"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isPromoting = false,
                        errorMessage = "Không thể đăng quảng cáo. Vui lòng thử lại sau."
                    )
                }
            }
        }

    }

    fun removePromotion(postId: String) {
        _uiState.update { it.copy(isPromoting = true, errorMessage = null, successMessage = null) }

        viewModelScope.launch {
            val success = promotedPostRepository.removePromotion(postId)

            if (success) {
                _uiState.update {
                    it.copy(
                        isPromoting = false,
                        isPostPromoted = false,
                        successMessage = "Đã hủy quảng cáo bài viết!"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isPromoting = false,
                        errorMessage = "Không thể hủy quảng cáo. Vui lòng thử lại sau."
                    )
                }
            }
        }
    }

    fun deletePost(postId: String, profileViewModel: ProfileViewModel? = null) {
        _uiState.update { it.copy(isDeleting = true, errorMessage = null, successMessage = null) }

        viewModelScope.launch {
            try {
                val firestore = FirebaseFirestore.getInstance()

                // Check if post is promoted
                val isPromoted = promotedPostRepository.isPostPromoted(postId)

                // Delete from posts collection
                firestore.collection("posts").document(postId).delete().await()

                // Delete from promotedPosts if it exists
                if (isPromoted) {
                    firestore.collection("promotedPosts").document(postId).delete().await()
                }

                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        isPostDeleted = true,
                        successMessage = "Đã xóa bài viết thành công!"
                    )
                }
                // Refresh profile data if view model is provided
                profileViewModel?.loadUserData()

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        errorMessage = "Không thể xóa bài viết. Vui lòng thử lại sau."
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun resetDeleteStatus() {
        _uiState.update { it.copy(isPostDeleted = false) }
    }
}