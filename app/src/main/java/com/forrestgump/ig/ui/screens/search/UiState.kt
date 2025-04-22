package com.forrestgump.ig.ui.screens.search

import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.models.User

data class UiState(
    val isLoading: Boolean = false,
    val userSuggestions: List<UserSuggestion> = emptyList(),
    val postSuggestions: List<PostSuggestion> = emptyList(),
    val users: List<User> = emptyList(),
    val posts: List<Post> = emptyList(),
    val errorMessage: String? = null,
    val friendSuggestions: List<FriendSuggestion> = emptyList(),
    val showFriendSuggestions: Boolean = true
)
