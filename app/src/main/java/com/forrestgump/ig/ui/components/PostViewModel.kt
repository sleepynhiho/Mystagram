package com.forrestgump.ig.ui.components

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forrestgump.ig.data.models.Comment
import com.forrestgump.ig.data.models.Notification
import com.forrestgump.ig.data.models.NotificationType
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.data.repositories.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {
    fun sendComment(post: Post, commentText: String, currentUser: User) {
        val comment = Comment(
            commentId = UUID.randomUUID().toString(),
            userId = currentUser.userId,
            username = currentUser.username,
            profileImage = currentUser.profileImage,
            text = commentText,
            timestamp = null
        )

        viewModelScope.launch {
            postRepository.addComment(post, comment, currentUser)
        }
    }

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments
    private var commentsJob: Job? = null

    fun loadComments(postId: String) {
        commentsJob?.cancel()
        commentsJob = viewModelScope.launch {
            postRepository.getComments(postId).collect {
                _comments.value = it
            }
        }
    }

    // Reactions
    private val _reactions = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val reactions: StateFlow<Map<String, List<String>>> = _reactions
    private var reactionsJob: Job? = null

//    fun loadReactions(postId: String) {
//        reactionsJob?.cancel()
//        reactionsJob = viewModelScope.launch {
//            postRepository.getReactions(postId).collect {
//                _reactions.value = it
//            }
//        }
//    }

    fun updateReaction(post: Post, currentUser: User, previousReaction: String?, newReaction: String?) {
        viewModelScope.launch {
            try {
                postRepository.updateReaction(post, currentUser, previousReaction, newReaction)
                observePost(post.postId)
            } catch (e: Exception) {
                Log.e("UpdateReaction", "Error updating reaction", e)
            }
        }
    }

    private val _currentPost = MutableStateFlow<Post?>(null)
    val currentPost: StateFlow<Post?> = _currentPost
    private var postJob: Job? = null

    private fun observePost(postId: String) {
        postJob?.cancel()
        postJob = viewModelScope.launch {
            postRepository.getPostById(postId).collect { post ->
                _currentPost.value = post
            }
        }
    }
}