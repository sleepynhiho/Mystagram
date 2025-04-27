package com.forrestgump.ig.ui.screens.notification.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.forrestgump.ig.R
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.forrestgump.ig.data.models.Notification
import com.forrestgump.ig.data.models.NotificationType
import com.forrestgump.ig.utils.constants.formatAsElapsedTime

@Composable
fun NotificationList(
    notifications: List<Notification>,
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    onAcceptFollowRequest: (Notification) -> Unit = {},
    onRejectFollowRequest: (Notification) -> Unit = {}
) {
    // Filter out read follow requests and sort by timestamp
    val filteredNotifications = notifications
        .filter { notification ->
            // Keep unread notifications and all non-follow-request notifications
            notification.type != NotificationType.FOLLOW_REQUEST || !notification.isRead
        }
        .sortedByDescending { it.timestamp } // Sort by timestamp, newest first
    
    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        content = {
            item { NotificationHeader() }
            items(filteredNotifications) { notification ->
                NotificationListItem(
                    notification = notification,
                    navHostController = navHostController,
                    onAcceptFollowRequest = onAcceptFollowRequest,
                    onRejectFollowRequest = onRejectFollowRequest
                )
            }
        }
    )
}

@Composable
fun NotificationHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 10.dp),
            text = stringResource(R.string.notification_header),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp
            )
        )
    }
}

@Composable
fun getNotificationMessage(notification: Notification): AnnotatedString {
    return buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        ) {
            append(notification.senderUsername)
        }
        append(
            when (notification.type) {
                NotificationType.REACT -> stringResource(R.string.react_noti)
                NotificationType.COMMENT -> stringResource(R.string.comment_noti)
                NotificationType.FOLLOW -> stringResource(R.string.follow_noti)
                NotificationType.FOLLOW_REQUEST -> stringResource(R.string.follow_req_noti)
                NotificationType.FOLLOW_ACCEPTED -> stringResource(R.string.follow_accepted_noti)
                NotificationType.FOLLOW_REJECTED -> stringResource(R.string.follow_rejected_noti)
            }
        )

        withStyle(
            style = SpanStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF737373)
            )
        ) {
            notification.timestamp?.let { append(it.formatAsElapsedTime()) }
        }
    }
}

@Composable
fun NotificationListItem(
    notification: Notification,
    navHostController: NavHostController,
    onAcceptFollowRequest: (Notification) -> Unit = {},
    onRejectFollowRequest: (Notification) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(vertical = 8.dp, horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Profile Image
            Surface(
                modifier = Modifier
                    .size(56.dp),
                shape = CircleShape,
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = notification.senderProfileImage,
                    contentScale = ContentScale.Crop,
                    contentDescription = stringResource(id = R.string.profile_image)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Notification text
            Text(
                text = getNotificationMessage(notification),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Add follow request buttons if this is a follow request notification
        if (notification.type == NotificationType.FOLLOW_REQUEST && !notification.isRead) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 66.dp, end = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onAcceptFollowRequest(notification) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Chấp nhận",
                        fontSize = 14.sp
                    )
                }
                
                OutlinedButton(
                    onClick = { onRejectFollowRequest(notification) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Từ chối",
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun NotificationListPreview() {
    NotificationList(
        notifications = listOf(
            Notification(
                senderUsername = "jane_doe",
                senderProfileImage = R.drawable.default_profile_image.toString(),
                type = NotificationType.REACT
            ),
            Notification(
                senderUsername = "john_doe",
                senderProfileImage = R.drawable.default_profile_image.toString(),
                type = NotificationType.FOLLOW
            )
        ),
        innerPadding = PaddingValues(0.dp),
        navHostController = rememberNavController()
    )
}