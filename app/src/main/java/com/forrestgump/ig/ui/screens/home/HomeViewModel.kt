package com.forrestgump.ig.ui.screens.home

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
import com.forrestgump.ig.data.repositories.PromotedPostRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.async

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository, // Inject repository vào
    private val promotedPostRepository: PromotedPostRepository // Add this
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
                    // Xử lý promoted posts
                    val promotedPosts = promotedPostRepository.getPromotedPosts()
                    val promotedPostIds = promotedPosts.map { it.postId }

                    // Mark promoted posts
                    val updatedNewPosts = newPosts.map { post ->
                        if (promotedPostIds.contains(post.postId)) {
                            post.copy(isSponsored = true)
                        } else {
                            post
                        }
                    }

                    // Combine with existing posts
                    val allPosts = uiState.value.posts + updatedNewPosts

                    // Reapply the 3:1 pattern
                    val combinedPosts = combineRegularAndPromotedPosts(allPosts)

                    uiState.update {
                        it.copy(
                            posts = combinedPosts,
                            isLoadingMore = false,
                            hasMore = newPosts.size >= pageSize
                        )
                    }

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
            // Mark as refreshing and completely clear the post list during refresh
            // to ensure we don't show any posts until we have the final formatted list
            uiState.update { it.copy(isRefreshing = true, posts = emptyList()) }

            // Reset pagination to load the first page
            postRepository.resetPagination()
            try {
                // Get regular posts
                val refreshedPosts = postRepository.getPosts(pageSize)

                // Get promoted posts
                val promotedPosts = promotedPostRepository.getPromotedPosts()
                val promotedPostIds = promotedPosts.map { it.postId }

                // Mark posts as sponsored
                val updatedPosts = refreshedPosts.map { post ->
                    if (promotedPostIds.contains(post.postId)) {
                        post.copy(isSponsored = true)
                    } else {
                        post
                    }
                }

                // Combine posts according to the 3:1 rule
                val combinedPosts = combineRegularAndPromotedPosts(updatedPosts)

                // Update UI state only once all processing is complete
                uiState.update {
                    it.copy(
                        posts = combinedPosts,
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

    // Modify observePosts method to include promoted posts
    fun observePosts() {
        postRepository.observePosts { regularPosts ->
            viewModelScope.launch {
                // Get all promoted posts
                val promotedPosts = promotedPostRepository.getPromotedPosts()
                val promotedPostIds = promotedPosts.map { it.postId }

                // Mark regular posts that are promoted
                val updatedRegularPosts = regularPosts.map { post ->
                    if (promotedPostIds.contains(post.postId)) {
                        post.copy(isSponsored = true)
                    } else {
                        post
                    }
                }

                // Insert a promoted post after every 3 regular posts
                val combinedPosts = combineRegularAndPromotedPosts(updatedRegularPosts)

                uiState.update { currentState ->
                    currentState.copy(
                        posts = combinedPosts,
                        isLoading = false,
                        isRefreshing = false
                    )
                }
            }
        }
    }

    // Helper function to combine regular and promoted posts
    // Phiên bản cải tiến, mỗi post quảng cáo chỉ xuất hiện một lần
    private fun combineRegularAndPromotedPosts(posts: List<Post>): List<Post> {
        val result = mutableListOf<Post>()
        val regularPosts = posts.filter { !it.isSponsored }
        val promotedPosts = posts.filter { it.isSponsored }.distinctBy { it.postId }

        if (promotedPosts.isEmpty()) {
            return regularPosts
        }

        var promotedIndex = 0

        // Thêm regular posts với một promoted post sau mỗi 3 bài
        regularPosts.forEachIndexed { index, post ->
            result.add(post)

            // Sau mỗi 3 bài regular, thêm một promoted post nếu còn
            if ((index + 1) % 3 == 0 && promotedIndex < promotedPosts.size) {
                result.add(promotedPosts[promotedIndex])
                promotedIndex++
            }
        }

        // Không cần thêm các promoted posts còn lại, mỗi promoted post chỉ hiển thị 1 lần

        return result
    }
}