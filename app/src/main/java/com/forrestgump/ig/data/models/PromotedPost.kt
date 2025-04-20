package com.forrestgump.ig.data.models

import androidx.annotation.Keep
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
data class PromotedPost(
    var postId: String = "",       // Original post ID
    var userId: String = "",       // User who owns the post
    @ServerTimestamp
    var promotionStartDate: Date? = null,  // When promotion started
    @ServerTimestamp
    var promotionEndDate: Date? = null     // When promotion will end (optional for now)
)