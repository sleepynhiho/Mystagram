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

@Composable
fun ChatScreen(
    myUsername: String,
    chats: List<Chat>,
    navHostController: NavHostController
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground),
        topBar = {
            ChatTopBar(myUsername, navHostController)
        }
    ) { innerPadding ->
        ChatList(
            chats = chats,
            innerPadding = innerPadding,
            navHostController = navHostController,
            myUsername = myUsername
        )
    }
}

@Preview
@Composable
fun ChatScreenPreview() {
    ChatScreen(
        myUsername = "sleepy",
        chats = listOf(
            Chat(
                chatId = "sleepy_Alice",
                user1Username = "sleepy",
                user2Username = "Alice",
                lastMessage = "Hello!",
                lastMessageTime = System.currentTimeMillis(),
                user1ProfileImage = "https://example.com/alice.jpg",
                user2ProfileImage = "https://example.com/sleepy.jpg",
                lastMessageRead = true
            ),
            Chat(
                chatId = "sleepy_Bob",
                user1Username = "sleepy",
                user2Username = "Bob",
                lastMessage = "How are you?",
                lastMessageTime = System.currentTimeMillis(),
                user1ProfileImage = "https://example.com/bob.jpg",
                user2ProfileImage = "https://example.com/sleepy.jpg",
                lastMessageRead = false
            )
        ),
        navHostController = rememberNavController()
    )
}
