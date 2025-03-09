package com.forrestgump.ig.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.forrestgump.ig.ui.screens.chat.components.ChatTopBar
import com.forrestgump.ig.ui.screens.chat.components.ChatList
import com.forrestgump.ig.data.models.Chat
import com.forrestgump.ig.data.models.MessageType
import com.forrestgump.ig.data.models.User
import java.util.Date

@Composable
fun ChatScreen(
    currentUser: User,
    chats: List<Chat>,
    navHostController: NavHostController,
    onNewChatClicked: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground),
        topBar = {
            ChatTopBar(currentUser.username, navHostController, onNewChatClicked)
        }
    ) { innerPadding ->
        ChatList(
            chats = chats,
            innerPadding = innerPadding,
            navHostController = navHostController,
            myUserId = currentUser.userId
        )
    }
}

@Preview
@Composable
fun ChatScreenPreview() {
    val navController = rememberNavController()
    val dummyChats = listOf(
        Chat("chat1", "user1", "user2", "Alice", "Bob", "https://randomuser.me/api/portraits/women/1.jpg", "https://randomuser.me/api/portraits/men/1.jpg", "Hello!", MessageType.TEXT, true, false, Date()),
        Chat("chat2", "user3", "user4", "Charlie", "David", "https://randomuser.me/api/portraits/men/2.jpg", "https://randomuser.me/api/portraits/men/3.jpg", "See you soon!", MessageType.TEXT, false, true, Date()),
        Chat("chat3", "user5", "user6", "Eve", "Frank", "https://randomuser.me/api/portraits/women/3.jpg", "https://randomuser.me/api/portraits/men/4.jpg", "Great photo!", MessageType.IMAGE, true, true, Date())
    )

    val currentUser = User(
        userId = "user1",
        username = "Alice",
        profileImage = "https://randomuser.me/api/portraits/women/1.jpg"
    )

    ChatScreen(
        currentUser = currentUser,
        chats = dummyChats,
        navHostController = navController,
        onNewChatClicked = TODO(),
    )
}
