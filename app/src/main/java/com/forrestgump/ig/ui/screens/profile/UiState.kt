package com.forrestgump.ig.ui.screens.profile

import com.forrestgump.ig.data.models.Post

data class UiState(
    var profileImage: String = "",
    var username: String = "",
    var password: String = "",
    var name: String = "",
    var email: String = "",
    var myPosts: List<Post> = emptyList(),
    var followers: List<String> = emptyList(),
    var following: List<String> = emptyList(),
    var isLoading: Boolean = false
)
