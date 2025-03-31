package com.forrestgump.ig.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class User(
    var userId: String = "",                    // ID người dùng (UUID)
    var username: String = "",                  // Tên người dùng (unique)
    var fullName: String = "User",              // Tên hiển thị
    var email: String = "",                     // Email người dùng
    var profileImage: String = "",           // Ảnh đại diện
    var bio: String = "",                       // Tiểu sử người dùng
    var followers: List<String> = emptyList(),  // Danh sách follower's userId
    var following: List<String> = emptyList(),   // Danh sách following's userId
    var location: String = "",
) : Parcelable
