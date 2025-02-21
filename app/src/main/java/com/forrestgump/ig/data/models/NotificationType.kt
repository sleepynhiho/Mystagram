package com.forrestgump.ig.data.models

enum class NotificationType {
    LIKE,               // Ai đó like bài post của mình
    COMMENT,            // Ai đó trả lời bình luận của mình
    FOLLOW,             // Ai đó follow mình (chỉ áp dụng cho tài khoản public)
    FOLLOW_REQUEST,     // Ai đó gửi yêu cầu follow mình (chỉ áp dụng cho tài khoản private)
    FOLLOW_ACCEPTED     // Ai đó chấp nhận follow request của mình (áp dụng cho tài khoản private)
}
