package com.forrestgump.ig.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class Comment(
    var commentId: String = "",         // ID bình luận
    var postId: String = "",            // ID bài viết liên quan
    var userId: String = "",            // ID người bình luận
    var username: String = "",          // Username của người bình luận
    var profileImageUrl: String = "",   // Ảnh đại diện của người bình luận
    var content: String = "",           // Nội dung bình luận

    @ServerTimestamp
    var timestamp: Date? = null         // Thời gian bình luận
) : Parcelable
