package com.forrestgump.ig.utils.models

data class UserStory(
    var username: String = "",
    var profileImage: String = "",
    var stories: List<Story> = emptyList()
)