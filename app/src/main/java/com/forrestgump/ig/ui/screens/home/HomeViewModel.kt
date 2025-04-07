package com.forrestgump.ig.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.data.models.UserStory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.repositories.PostRepository
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository // Inject repository vào
) : ViewModel() {

    var uiState = MutableStateFlow(UiState())
        private set

    // Số bài viết mỗi trang
    private val pageSize = 5

    // Gọi hàm load thêm bài viết từ repository
    fun loadNextPosts() {
        if (uiState.value.isLoadingMore || !uiState.value.hasMore) return

        viewModelScope.launch {
            uiState.update { it.copy(isLoadingMore = true) }
            try {
                val newPosts: List<Post> = postRepository.getPosts(pageSize)
                if (newPosts.isNotEmpty()) {
                    uiState.update { currentState ->
                        // Nối danh sách bài viết hiện có với bài viết mới
                        currentState.copy(
                            posts = currentState.posts + newPosts,
                            isLoading = false,
                            isLoadingMore = false,
                            hasMore = newPosts.size >= pageSize
                        )
                    }
                    val totalPosts = uiState.value.posts.size
                    Log.d("HomeViewModel", "Tổng số post đã load: $totalPosts")
                } else {
                    // Không còn bài viết để load thêm (có thể thêm biến trạng thái 'hasMore')
                    uiState.update { it.copy(isLoadingMore = false, hasMore = false) }
                }
            } catch (e: Exception) {
                // Xử lý lỗi nếu cần (ví dụ: cập nhật trạng thái lỗi trong uiState)
                uiState.update { it.copy(isLoadingMore = false) }
                e.printStackTrace()
            }
        }
    }

    fun refreshPosts() {
        viewModelScope.launch {
            uiState.update { it.copy(isRefreshing = true) }
            // Reset lại phân trang để load trang đầu tiên
            postRepository.resetPagination()
            try {
                val refreshedPosts = postRepository.getPosts(pageSize)  // load 5 post đầu tiên
                uiState.update {
                    it.copy(
                        posts = refreshedPosts,  // Thay thế danh sách post cũ
                        isRefreshing = false,
                        hasMore = refreshedPosts.size >= pageSize
                    )
                }
            } catch (e: Exception) {
                uiState.update { it.copy(isRefreshing = false) }
                e.printStackTrace()
            }
        }
    }

    fun onStoryScreenClicked(value: Boolean, userStoryIndex: Int) {
        uiState.update { it.copy(showStoryScreen = value, userStoryIndex = userStoryIndex) }
    }

    private fun clearUiState() {
        uiState.update { UiState() }
    }

    override fun onCleared() {
        super.onCleared()
        clearUiState()
    }

    fun updateUserStories(newUserStories: List<UserStory>, currentUser: User) {
        val myStories = newUserStories.filter { it.userId == currentUser.userId }
        val otherUserStories = newUserStories.filter { it.userId != currentUser.userId }

        uiState.update {
            it.copy(
                myStories = myStories,
                userStories = otherUserStories
            )
        }
    }

    fun observePosts() {
        postRepository.observePosts { posts ->
            uiState.update { currentState ->
                currentState.copy(
                    posts = posts,
                    isLoading = false,
                    isRefreshing = false
                )
            }
        }
    }
}