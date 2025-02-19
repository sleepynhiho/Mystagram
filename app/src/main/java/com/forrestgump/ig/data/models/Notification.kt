package com.forrestgump.ig.data.models

import android.os.Parcelable
import androidx.annotation.Keep
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
    var senderProfileImageUrl: String = "", // Avatar của người gửi
    var type: String = "",               // Loại thông báo (like, comment, follow)
    var postId: String? = null,          // ID bài viết liên quan (nếu có)
    var isRead: Boolean = false,         // Trạng thái đã đọc

    @ServerTimestamp
    var timestamp: Date? = null          // Thời gian thông báo
) : Parcelable
