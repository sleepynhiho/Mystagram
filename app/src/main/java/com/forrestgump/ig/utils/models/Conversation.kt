package com.forrestgump.ig.utils.models

data class Conversation(
    val id: String,
    val senderName: String,
    val lastMessage: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
