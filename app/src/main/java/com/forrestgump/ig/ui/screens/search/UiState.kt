package com.forrestgump.ig.ui.screens.search

import com.forrestgump.ig.remote.models.AppUser
import com.forrestgump.ig.utils.models.Post

data class UiState(
    var username: String = "",
    var isLoading: Boolean = false,
    var isFollowing: Boolean = false
)
