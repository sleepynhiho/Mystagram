package com.forrestgump.ig.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize

data class Chat(
    val chatId: String,   // ID cuộc trò chuyện (VD: "sleepy_Alice")
    val user1Username: String,    // Username của người 1
    val user2Username: String,    // Username của người 2
    val user1ProfileImage: String, // Avatar user 1
    val user2ProfileImage: String, // Avatar user 2
    val lastMessage: String,      // Nội dung tin nhắn cuối
    val lastMessageTime: Long,     // Thời gian tin nhắn cuối
    val lastMessageRead: Boolean
) : Parcelable
