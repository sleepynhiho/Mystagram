package com.forrestgump.ig.utils.models

data class Message(
    val messageID: String,    // ID tin nhắn (UUID hoặc số tự tăng)
    val senderUsername: String,
    val receiverUsername: String,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
