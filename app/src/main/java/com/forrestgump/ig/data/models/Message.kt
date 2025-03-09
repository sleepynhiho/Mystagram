package com.forrestgump.ig.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class Message(
    val messageID: String = "",    // Unique ID for the message (UUID)
    val senderId: String = "",     // ID of the sender
    val messageType: MessageType = MessageType.TEXT,  // Message type (TEXT, IMAGE)
    val content: String? = null,  // Encrypted text content (if any)
    val imageUrl: String? = null,  // URL of the encrypted image (if any)
    var isRead: Boolean = false,   // Read status for the receiver

    @ServerTimestamp
    var timestamp: Date? = null   // Automatic timestamp when sent
) : Parcelable

enum class MessageType {
    TEXT, IMAGE
}
