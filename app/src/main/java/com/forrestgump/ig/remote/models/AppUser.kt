package com.forrestgump.ig.remote.models

data class AppUser(
    var userId: String = "",
    var username: String = "",
    var password: String = "",
    var name: String = "User",
    var profileImage: String = "",
    var email: String = "",
    var followerList: List<String> = emptyList(),
    var followingList: List<String> = emptyList(),
)
