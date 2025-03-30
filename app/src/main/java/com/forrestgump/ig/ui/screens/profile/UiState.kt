package com.forrestgump.ig.ui.screens.profile

import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.models.User

data class UiState(
    var curUser: User = User(),
    var postCount: Int = 0,
    var posts: List<Post> = emptyList(),
    var isLoading: Boolean = false
)
