package com.forrestgump.ig.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class UserStory(
    var userId: String = "",             // ID người dùng
    var username: String = "",           // Tên người dùng
    var profileImage: String = "",    // Ảnh đại diện
    var stories: List<Story> = emptyList() // Danh sách stories của user
) : Parcelable
