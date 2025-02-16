package com.forrestgump.ig.utils.models

data class Conversation(
    val username: String,
    val userProfileImage: String,
    val timestamp: Long,
    val isRead: Boolean = false,  // Trạng thái tin nhắn cuối
    val messages: List<Message> = emptyList()
)
