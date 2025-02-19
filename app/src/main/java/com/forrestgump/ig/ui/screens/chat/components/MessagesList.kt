package com.forrestgump.ig.ui.screens.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
fun MessagesList(
    chats: List<Chat>,
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    myUsername: String
) {
    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MainBackground),
        content = {
            item { MessagesHeader() }
            items(chats) { chat ->
                MessagesListItem(chat = chat, navHostController = navHostController, myUsername)
            }
        }
    )
}

@Composable
fun MessagesHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MainBackground)
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 10.dp),
            text = stringResource(R.string.messages_header),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp
            )
        )
    }
}

@Composable
fun MessagesListItem(
    chat: Chat,
    navHostController: NavHostController,
    myUsername: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MainBackground)
            .height(72.dp)
            .clickable {
                navHostController.navigate("message_detail/${chat.chatId}")
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
                model = if (myUsername == chat.user1Username) chat.user2ProfileImage else chat.user1ProfileImage,
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(id = R.string.profile_image)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = if (myUsername == chat.user1Username) chat.user2Username else chat.user1Username,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = chat.lastMessage,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = if (chat.lastMessageRead) FontWeight.Normal else FontWeight.Bold,
                    color = if (chat.lastMessageRead) Color(0xFF737373) else MaterialTheme.colorScheme.onBackground
                )
            )
        }

        if (!chat.lastMessageRead) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0095F6))
            )
        }
        Spacer(modifier = Modifier.width(20.dp))

    }
}


@Preview
@Composable
fun MessagesListPreview() {
    MessagesList(
        chats = listOf(
            Chat(
                chatId = "chat_1",
                user1Username = "sleepy",
                user2Username = "john_doe",
                user1ProfileImage = R.drawable.default_profile_img.toString(),
                user2ProfileImage = R.drawable.default_profile_img.toString(),
                lastMessage = "Good morning!",
                lastMessageTime = 234234L,
                lastMessageRead = false
            ),
            Chat(
                chatId = "chat_2",
                user1Username = "john_doe",
                user2Username = "sleepy",
                user1ProfileImage = R.drawable.default_profile_img.toString(),
                user2ProfileImage = R.drawable.default_profile_img.toString(),
                lastMessage = "Hey, what's up?",
                lastMessageTime = 234235L,
                lastMessageRead = true
            )
        ),
        innerPadding = PaddingValues(0.dp),
        navHostController = rememberNavController(),
        myUsername = "sleepy"  // Thêm myUsername để xác định ảnh đối phương
    )
}
