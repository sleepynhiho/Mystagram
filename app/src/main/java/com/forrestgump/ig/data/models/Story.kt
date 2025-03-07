package com.forrestgump.ig.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class Story(
    var storyId: String = "",             // ID của Story
    var username: String = "",            // Người đăng
    var media: String = "",            // URL ảnh/video từ Cloudinary
    var views: List<String> = emptyList(),// Danh sách người đã xem
    var mimeType: String = "",            // Kiểu file (image/video)

    @ServerTimestamp
    var timestamp: Date? = null           // Thời gian tạo (Firestore tự động cập nhật)
) : Parcelable {
    val expiry: Long
        get() = (timestamp?.time ?: System.currentTimeMillis()) + 24 * 60 * 60 * 1000
}
