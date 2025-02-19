package com.forrestgump.ig.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val messageID: String = "",      // ID tin nhắn (UUID)
    val senderUsername: String = "", // Người gửi
    val content: String = "",        // Nội dung tin nhắn
    val timestamp: Long = 0L,        // Thời gian gửi
    var isRead: Boolean = false      // Trạng thái đã đọc
) : Parcelable
