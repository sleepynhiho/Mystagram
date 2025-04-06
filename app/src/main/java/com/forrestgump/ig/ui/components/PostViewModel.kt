package com.forrestgump.ig.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forrestgump.ig.data.models.Comment
import com.forrestgump.ig.data.repositories.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {
    fun sendComment(postId: String, userId: String, commentText: String, username: String, profileImage: String) {
        val comment = Comment(
            commentId = "",
            userId = userId,
            username = username,
            profileImage = profileImage,
            text = commentText,
            timestamp = null
        )

        viewModelScope.launch {
            postRepository.addComment(postId, comment)
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



}