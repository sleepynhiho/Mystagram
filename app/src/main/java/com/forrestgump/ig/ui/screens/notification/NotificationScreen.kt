package com.forrestgump.ig.ui.screens.notification

import androidx.compose.foundation.background
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.forrestgump.ig.data.models.Notification
import com.forrestgump.ig.ui.screens.notification.components.NotificationList
import com.forrestgump.ig.ui.screens.notification.components.NotificationTopBar
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.forrestgump.ig.data.models.NotificationType
import java.util.Date


@Composable
fun NotificationScreen(
    notifications: List<Notification>,
    navHostController: NavHostController
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(horizontal = 10.dp),
        topBar = {
            NotificationTopBar(navHostController)
        }
    ) { innerPadding ->
        NotificationList(
            notifications = notifications,
            innerPadding = innerPadding,
            navHostController = navHostController
        )
    }
}

@Preview
@Composable
private fun NotificationScreenPreview() {
    val sampleNotifications = listOf(
        Notification(
            notificationId = "1",
            receiverId = "user_123",
            senderId = "user_456",
            senderUsername = "jane_doe",
            senderProfileImage = "https://example.com/jane.jpg",
            postId = "post_789",
            isRead = false,
            type = NotificationType.LIKE,
            timestamp = Date()
        ),
        Notification(
            notificationId = "2",
            receiverId = "user_123",
            senderId = "user_789",
            senderUsername = "john_smith",
            senderProfileImage = "https://example.com/john.jpg",
            postId = "post_321",
            isRead = true,
            type = NotificationType.COMMENT,
            timestamp = Date()
        ),
        Notification(
            notificationId = "3",
            receiverId = "user_123",
            senderId = "user_101",
            senderUsername = "alice_wonder",
            senderProfileImage = "https://example.com/alice.jpg",
            isRead = false,
            type = NotificationType.FOLLOW,
            timestamp = Date()
        ),
        Notification(
            notificationId = "4",
            receiverId = "user_123",
            senderId = "user_202",
            senderUsername = "bob_marley",
            senderProfileImage = "https://example.com/bob.jpg",
            isRead = false,
            type = NotificationType.FOLLOW_REQUEST,
            timestamp = Date()
        ),
        Notification(
            notificationId = "5",
            receiverId = "user_123",
            senderId = "user_303",
            senderUsername = "charlie_brown",
            senderProfileImage = "https://example.com/charlie.jpg",
            isRead = true,
            type = NotificationType.FOLLOW_ACCEPTED,
            timestamp = Date()
        )
    )

    NotificationScreen(
        notifications = sampleNotifications,
        navHostController = rememberNavController()
    )
}
