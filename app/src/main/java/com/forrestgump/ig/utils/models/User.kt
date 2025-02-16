package com.forrestgump.ig.utils.models

data class User(
    var username: String = "",
    var name: String = "user",
    var email: String = "",
    var password: String = "",
    var profileImage: String = "",
    var followerList: List<String> = emptyList(),
    var followingList: List<String> = emptyList()
)