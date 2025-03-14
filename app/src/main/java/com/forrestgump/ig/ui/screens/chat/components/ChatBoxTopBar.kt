package com.forrestgump.ig.ui.screens.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.forrestgump.ig.R
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.forrestgump.ig.data.models.Chat

@Composable
fun ChatBoxTopBar(
    navHostController: NavHostController,
    chat: Chat,
    myUserId: String
) {

    val otherUsername = if (myUserId == chat.user1Id) chat.user2Username else chat.user1Username
    val otherUserProfileImage = if (myUserId == chat.user1Id) chat.user2ProfileImage else chat.user1ProfileImage


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .background(color = MainBackground),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.width(10.dp))

        Row(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.5f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                modifier = Modifier
                    .size(37.dp)
                    .padding(5.dp)
                    .clickable { navHostController.popBackStack() },
                painter = painterResource(id = R.drawable.back),
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = stringResource(id = R.string.back_home)
            )

            Spacer(modifier = Modifier.width(20.dp))

            Surface(
                modifier = Modifier
                    .size(32.dp),
                shape = CircleShape
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = otherUserProfileImage,
                    contentScale = ContentScale.Crop,
                    contentDescription = otherUsername
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = otherUsername,
                style = TextStyle(
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }

    }
}

@Preview
@Composable
fun ChatBoxTopBarPreview() {
    ChatBoxTopBar(
        navHostController = rememberNavController(),
        chat = Chat(
            chatId = "chat_123",
            user1Username = "_menf",
            user2Username = "john_doe",
            user1ProfileImage = R.drawable.default_profile_img.toString(),
            user2ProfileImage = R.drawable.default_profile_img.toString(),
            lastMessage = "Hey, what's up?",
        ),
        myUserId = "_menf"
    )
}
