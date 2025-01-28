package com.forrestgump.ig.utils.models

data class Story(
    var userId: String = "",
    var timeStamp: Long = 0L,
    var image: String = "",
    var views: List<String> = emptyList(),
    var liked: Boolean = false,
    var mimeType : String = ""
)
