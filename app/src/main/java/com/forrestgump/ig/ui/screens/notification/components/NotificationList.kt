package com.forrestgump.ig.ui.screens.notification.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
) {
    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        content = {
            item { NotificationHeader() }
            items(notifications) { notification ->
                NotificationListItem(
                    notification = notification,
                    navHostController = navHostController
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
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .height(72.dp)
            .clickable {
//                navHostController.navigate("ChatBoxScreen/${chat.chatId}")
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(modifier = Modifier.width(10.dp))
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

        Text(
            text = getNotificationMessage(notification),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
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