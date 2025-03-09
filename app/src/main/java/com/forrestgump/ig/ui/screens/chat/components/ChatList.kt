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
import com.forrestgump.ig.data.models.MessageType
import java.util.Date

@Composable
fun ChatList(
    chats: List<Chat>,
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    myUserId: String
) {
    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MainBackground)
            .padding(horizontal = 10.dp),
        content = {
            item { ChatHeader() }
            items(chats) { chat ->
                ChatListItem(chat = chat, navHostController = navHostController, myUserId)
            }
        }
    )
}

@Composable
fun ChatHeader() {
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
fun ChatListItem(
    chat: Chat,
    navHostController: NavHostController,
    myUserId: String
) {
    val isRead = if (chat.user1Id == myUserId) chat.user1Read else chat.user2Read


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MainBackground)
            .height(72.dp)
            .clickable {
                navHostController.navigate("ChatBoxScreen/${chat.chatId}")
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
                model = if (myUserId == chat.user1Id) chat.user2ProfileImage else chat.user1ProfileImage,
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
                text = if (myUserId == chat.user1Id) chat.user2Username else chat.user1Username,
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
                    fontWeight = if (isRead) FontWeight.Normal else FontWeight.Bold,
                    color = if (isRead) Color(0xFF737373) else MaterialTheme.colorScheme.onBackground
                )
            )
        }

        if (!isRead) {
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
fun ChatListPreview() {
    val navController = rememberNavController()
    ChatList(
        chats = listOf(
            Chat("chat1", "user1", "user2", "Alice", "Bob", "https://randomuser.me/api/portraits/women/1.jpg", "https://randomuser.me/api/portraits/men/1.jpg", "Hello!", MessageType.TEXT, true, false, Date()),
            Chat("chat2", "user3", "user4", "Charlie", "David", "https://randomuser.me/api/portraits/men/2.jpg", "https://randomuser.me/api/portraits/men/3.jpg", "See you soon!", MessageType.TEXT, false, true, Date()),
            Chat("chat3", "user5", "user6", "Eve", "Frank", "https://randomuser.me/api/portraits/women/3.jpg", "https://randomuser.me/api/portraits/men/4.jpg", "Great photo!", MessageType.IMAGE, true, true, Date())
        ),
        innerPadding = PaddingValues(),
        navHostController = navController,
        myUserId = "user1"
    )
}

