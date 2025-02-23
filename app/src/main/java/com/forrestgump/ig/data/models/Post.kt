package com.forrestgump.ig.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class Post(
    var postId: String = "",               // ID bài viết
    var userId: String = "",               // ID người đăng
    var username: String = "",             // Username của người đăng
    var profileImageUrl: String = "",      // Ảnh đại diện của người đăng
    var mediaUrls: List<String> = emptyList(),             // Link ảnh/video trên Cloudinary
    var caption: String = "",              // Caption bài viết
    var reactions: Map<String, Int> = emptyMap(), // Số lượng từng loại react
    var commentsCount: Int = 0,            // Số lượng bình luận
    var mimeType: String = "",             // Kiểu file (image/video)

    @ServerTimestamp
    var timestamp: Date? = null            // Thời gian tạo bài viết
) : Parcelable
