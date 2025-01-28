package com.forrestgump.ig.utils.models

data class Post(
    var profileImage: String = "",
    var userId: String = "",
    var username: String = "",
    var timeStamp: Long = 0L,
    var mediaList: List<String> = emptyList(),
    var likes: List<String> = emptyList(),
    var comments: List<String> = emptyList(),
    var caption: String = "",
    var mimeType: String = ""
)
