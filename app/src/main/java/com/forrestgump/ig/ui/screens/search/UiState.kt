package com.forrestgump.ig.ui.screens.search

import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.data.models.Post

data class UiState(
    var isLoading: Boolean = false,
    var users: List<User> = emptyList(), // Add 'users' property
    var posts: List<Post> = emptyList()  // List of all posts
)
