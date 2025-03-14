package com.forrestgump.ig.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class Chat(
    val chatId: String = "",   // Unique chat ID (combination of user1Id and user2Id)
    val user1Id: String = "",  // First user in the chat
    val user2Id: String = "",  // Second user in the chat
    val user1Username: String = "",  // Username of user1
    val user2Username: String = "",  // Username of user2
    val user1ProfileImage: String = "",  // Profile image of user1
    val user2ProfileImage: String = "",  // Profile image of user2
    val lastMessage: String = "",  // Last sent message (preview in chat list, decrypted)
    val lastMessageType: MessageType = MessageType.TEXT,  // Type of last message
    val user1Read: Boolean = false,  // Read status for user1
    val user2Read: Boolean = false,  // Read status for user2

    @ServerTimestamp
    var lastMessageTime: Date? = null // Timestamp of last message
) : Parcelable
