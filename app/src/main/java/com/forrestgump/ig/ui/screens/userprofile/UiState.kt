package com.forrestgump.ig.ui.screens.userprofile

import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.models.User

data class UserProfileUiState(
    var user: User = User(),
    var currentUser: User = User(),
    var posts: List<Post> = emptyList(),
    var isLoading: Boolean = true,
    var isFollowing: Boolean = false,
    var isCurrentUserFollowingThisUser: Boolean = false,
    var isCurrentUserFollowedByThisUser: Boolean = false
)