package com.forrestgump.ig.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
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
import java.util.UUID

@Composable
fun ChatBoxScreen(
    myUsername: String,
    chat: Chat,
    messages: List<Message>,
    navHostController: NavHostController
) {
    var updatedChat by remember { mutableStateOf(chat) }

    updatedChat = updatedChat.copy(
        lastMessageRead = true
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground),
        topBar = {
            ChatBoxTopBar(
                navHostController = navHostController,
                chat = updatedChat,
                myUsername = myUsername
            )
        },
        bottomBar = {
            ChatBoxInputBar(
                onSendMessage = { messageContent ->
                    val newMessage = Message(
                        messageID = UUID.randomUUID().toString(),
                        senderUsername = myUsername,
                        content = messageContent,
                        timestamp = System.currentTimeMillis(),
                        isRead = true
                    )
                    updatedChat = updatedChat.copy(
                        lastMessage = newMessage.content,
                        lastMessageTime = newMessage.timestamp,
                        lastMessageRead = newMessage.isRead
                    )
                },
                onUploadImage = {
                    // Xử lý khi tải ảnh lên
                }
            )
        }
    ) { innerPadding ->
        ChatBoxContent(
            myUsername, updatedChat, messages,
            innerPadding = innerPadding
        )
    }
}

@Preview
@Composable
fun ChatBoxScreenPreview() {
    ChatBoxScreen(
        myUsername = "sleepy",
        chat = Chat(
            chatId = "chat_123",
            user1Username = "sleepy",
            user2Username = "John Doe",
            user1ProfileImage = R.drawable.default_profile_img.toString(),
            user2ProfileImage = R.drawable.default_profile_img.toString(),
            lastMessage = "Hey, what's up?",
            lastMessageTime = 234235L,
            lastMessageRead = true
        ),
        messages = listOf(
            Message(
                messageID = "3",
                senderUsername = "sleepy",
                content = "I'm trying to wake up in the morning but it's so hard",
                timestamp = 234235L,
                isRead = true
            ),
            Message(
                messageID = "2",
                senderUsername = "John Doe",
                content = "Did you try setting an alarm?",
                timestamp = 234230L,
                isRead = true
            ),
            Message(
                messageID = "1",
                senderUsername = "sleepy",
                content = "Yeah, but I always snooze it.",
                timestamp = 234225L,
                isRead = true
            )
        ),
        navHostController = rememberNavController()
    )
}
