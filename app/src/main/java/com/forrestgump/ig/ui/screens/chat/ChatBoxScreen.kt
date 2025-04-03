package com.forrestgump.ig.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.forrestgump.ig.R
import com.forrestgump.ig.ui.screens.chat.components.ChatBoxContent
import com.forrestgump.ig.ui.screens.chat.components.ChatBoxInputBar
import com.forrestgump.ig.ui.screens.chat.components.ChatBoxTopBar
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.forrestgump.ig.data.models.Chat
import com.forrestgump.ig.data.models.Message
import com.forrestgump.ig.data.models.MessageType
import com.forrestgump.ig.data.models.User
import java.util.Date
import java.util.UUID

@Composable
fun ChatBoxScreen(
    currentUser: User,
    chat: Chat,
    messages: List<Message>,
    navHostController: NavHostController
) {
    var updatedChat by remember { mutableStateOf(chat) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            ChatBoxTopBar(
                navHostController = navHostController,
                chat = updatedChat,
                myUserId = currentUser.userId
            )
        },
        bottomBar = {
            ChatBoxInputBar(
                onSendMessage = { messageContent ->
                    val newMessage = Message(
                        senderId = currentUser.userId,
                        content = messageContent,
                        isRead = true,
                        messageType = MessageType.TEXT,
                    )

                    updatedChat = newMessage.content?.let {
                        updatedChat.copy(
                            lastMessage = it,
                            lastMessageTime = newMessage.timestamp,
                            user1Read = updatedChat.user1Id == currentUser.userId,
                            user2Read = updatedChat.user2Id == currentUser.userId
                        )
                    }!!
                },
                onUploadImage = {
                    // Xử lý khi tải ảnh lên
                }
            )
        }
    ) { innerPadding ->
        ChatBoxContent(
            currentUser.userId, updatedChat, messages,
            innerPadding = innerPadding
        )
    }
}

@Preview
@Composable
fun ChatBoxScreenPreview() {
    val navController = rememberNavController()
    val dummyChat = Chat(
        chatId = "chat1",
        user1Id = "user1",
        user2Id = "user2",
        user1Username = "Alice",
        user2Username = "Bob",
        user1ProfileImage = "https://randomuser.me/api/portraits/women/1.jpg",
        user2ProfileImage = "https://randomuser.me/api/portraits/men/1.jpg",
        lastMessage = "Hello!",
        lastMessageType = MessageType.TEXT,
        user1Read = true,
        user2Read = false,
        lastMessageTime = Date()
    )

    val dummyMessages = listOf(
        Message("msg1", "user1", MessageType.TEXT, "Hello!", null, true, Date()),
        Message("msg2", "user2", MessageType.TEXT, "Hi Alice!", null, false, Date()),
        Message("msg3", "user1", MessageType.TEXT, "How are you?", null, true, Date()),
        Message("msg4", "user2", MessageType.TEXT, "I'm good, you?", null, false, Date()),
        Message("msg5", "user1", MessageType.IMAGE, null, "https://picsum.photos/200", true, Date()),
        Message("msg6", "user2", MessageType.TEXT, "Nice photo!", null, false, Date()),
        Message("msg7", "user1", MessageType.TEXT, "Thanks!", null, true, Date()),
        Message("msg8", "user2", MessageType.IMAGE, null, "https://picsum.photos/201", false, Date()),
        Message("msg9", "user1", MessageType.TEXT, "Where was that?", null, true, Date()),
        Message("msg10", "user2", MessageType.TEXT, "At the beach!", null, false, Date())
    )

    val currentUser = User(
        userId = "user1",
        username = "Alice",
        profileImage = "https://randomuser.me/api/portraits/women/1.jpg"
    )

    ChatBoxScreen(
        currentUser = currentUser,
        chat = dummyChat,
        messages = dummyMessages,
        navHostController = navController
    )
}

