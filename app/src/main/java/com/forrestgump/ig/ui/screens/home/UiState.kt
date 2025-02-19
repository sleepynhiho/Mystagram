package com.forrestgump.ig.ui.screens.home

import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.models.UserStory

data class UiState(
    var posts: List<Post> = emptyList(),
    var myStories: List<UserStory> = emptyList(),
    var userStories: List<UserStory> = emptyList(),
    var isLoading: Boolean = true,
    var showStoryScreen: Boolean = false
)
