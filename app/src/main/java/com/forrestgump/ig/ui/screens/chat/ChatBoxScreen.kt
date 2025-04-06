package com.forrestgump.ig.ui.screens.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.forrestgump.ig.ui.screens.chat.components.ChatBoxContent
import com.forrestgump.ig.ui.screens.chat.components.ChatBoxInputBar
import com.forrestgump.ig.ui.screens.chat.components.ChatBoxTopBar
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.forrestgump.ig.data.models.Message
import com.forrestgump.ig.data.models.MessageType
import com.forrestgump.ig.data.models.User
import java.util.Date

@Composable
fun ChatBoxScreen(
    currentUser: User,
    chatId: String,
    navHostController: NavHostController,
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    // Lấy chat và messages từ ViewModel
    val chat by chatViewModel.chat.observeAsState()


    // Tải chat và messages nếu chưa tải
    LaunchedEffect(chatId) {
        chatViewModel.loadChatAndMessages(chatId)
        chatViewModel.listenForMessages(chatId)
    }

    // Lắng nghe cập nhật messages
    chat?.let { updatedChat ->
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MainBackground),
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
                            timestamp = Date()
                        )
                        chatViewModel.saveMessageToFirestore(updatedChat, newMessage, currentUser.userId)
                    },
                    onUploadImage = {
                        // fix: Xử lý khi tải ảnh lên
                    }
                )
            }
        ) { innerPadding ->
            ChatBoxContent(
                currentUser.userId, updatedChat,
                innerPadding = innerPadding
            )

        }
    }
}

@Preview
@Composable
fun ChatBoxScreenPreview() {
    val navController = rememberNavController()
    val currentUser = User(
        userId = "user1",
        username = "Alice",
        profileImage = "https://randomuser.me/api/portraits/women/1.jpg"
    )

    ChatBoxScreen(
        currentUser = currentUser,
        chatId = "chat1",
        navHostController = navController
    )
}
