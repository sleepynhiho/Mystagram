package com.forrestgump.ig.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class Comment(
    var commentId: String = "",      // ID của bình luận
    var postId: String = "",         // ID bài viết mà bình luận thuộc về
    var userId: String = "",         // ID người bình luận
    var username: String = "",       // Username người bình luận
    var profileImage: String = "", // Ảnh đại diện người bình luận
    var text: String = "",           // Nội dung bình luận

    @ServerTimestamp
    var timestamp: Date? = null      // Thời gian bình luận
) : Parcelable
