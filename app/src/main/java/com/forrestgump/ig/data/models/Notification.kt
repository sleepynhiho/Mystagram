package com.forrestgump.ig.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class Notification(
    var notificationId: String = "",     // ID thông báo
    var receiverId: String = "",         // Người nhận thông báo
    var senderId: String = "",           // Người thực hiện hành động (like, comment,...)
    var senderUsername: String = "",     // Username người thực hiện
    var senderProfileImage: String = "", // Avatar của người gửi
    var postId: String? = null,          // ID bài viết liên quan (nếu có)
    var isRead: Boolean = false,         // Trạng thái đã đọc

    @get:PropertyName("type")
    @set:PropertyName("type")
    var type: NotificationType = NotificationType.REACT,

    @ServerTimestamp
    var timestamp: Date? = null          // Thời gian thông báo
) : Parcelable
