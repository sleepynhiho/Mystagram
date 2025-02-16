package com.forrestgump.ig.utils.models

data class Story(
    var username: String = "",
    var timestamp: Long = 0L,
    var image: String = "",
    var views: List<String> = emptyList(),
    var isLiked: Boolean = false,
    var mimeType : String = ""
)
