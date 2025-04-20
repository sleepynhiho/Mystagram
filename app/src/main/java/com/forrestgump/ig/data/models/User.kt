package com.forrestgump.ig.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.Date

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
    var fcmToken: String = "",                  // Token nhận thông báo từ FCM
    var isPremium: Boolean = false,
    var isPrivate: Boolean = false,
    var premiumDate: Date? = null             // Ngày đăng ký premium
) : Parcelable {
    // Check if premium has expired (1 month = 30 days)
    fun isPremiumExpired(): Boolean {
        if (!isPremium || premiumDate == null) return true
        val currentTime = System.currentTimeMillis()
        val premiumTime = premiumDate!!.time
        val oneMonthInMillis = 30 * 24 * 60 * 60 * 1000L // 30 days in milliseconds
        return currentTime - premiumTime > oneMonthInMillis
    }

    // Calculate remaining premium days
    fun getRemainingPremiumDays(): Int {
        if (!isPremium || premiumDate == null) return 0
        val currentTime = System.currentTimeMillis()
        val premiumTime = premiumDate!!.time
        val oneMonthInMillis = 30 * 24 * 60 * 60 * 1000L // 30 days in milliseconds
        val remainingMillis = oneMonthInMillis - (currentTime - premiumTime)
        return if (remainingMillis <= 0) 0 else (remainingMillis / (24 * 60 * 60 * 1000L)).toInt()
    }
}
