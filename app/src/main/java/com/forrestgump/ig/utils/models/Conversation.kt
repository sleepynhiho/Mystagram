package com.forrestgump.ig.utils.models

data class Conversation(
    val userID: String,
    val username: String,
    val userProfileImage: String,
    val lastMessage: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
