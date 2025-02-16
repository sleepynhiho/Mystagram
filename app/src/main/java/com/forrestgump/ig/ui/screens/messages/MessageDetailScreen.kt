package com.forrestgump.ig.ui.screens.messages

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.forrestgump.ig.R
import com.forrestgump.ig.ui.screens.messages.components.MessageDetailContent
import com.forrestgump.ig.ui.screens.messages.components.MessageDetailInputBar
import com.forrestgump.ig.ui.screens.messages.components.MessageDetailTopBar
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.forrestgump.ig.utils.models.Conversation
import com.forrestgump.ig.utils.models.Message
import java.util.UUID

@Composable
fun MessageDetailScreen(
    myUsername: String,
    conversation: Conversation,
    navHostController: NavHostController
) {

    var updatedConversation by remember { mutableStateOf(conversation) }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground),
        topBar = {
            MessageDetailTopBar(
                navHostController = navHostController,
                conversation = updatedConversation
            )
        },
        bottomBar = {
            MessageDetailInputBar (
                onSendMessage = { messageContent ->
                    val newMessage = Message(
                        messageID = UUID.randomUUID().toString(),
                        senderUsername = myUsername,
                        receiverUsername = conversation.username,
                        content = messageContent,
                        timestamp = System.currentTimeMillis(),
                        isRead = false
                    )
                    updatedConversation = updatedConversation.copy(
                        messages = listOf(newMessage) + updatedConversation.messages
                    )
                },
                onUploadImage = {
                    // Xử lý khi tải ảnh lên
                }
            )
        }
    ) { innerPadding ->
        MessageDetailContent(myUsername, updatedConversation, innerPadding)
    }
}


@Preview
@Composable
fun MessageDetailScreenPreview() {
    MessageDetailScreen(
        myUsername = "sleepy",
        Conversation(
            username = "John Doe",
            userProfileImage = R.drawable.default_profile_img.toString(),
            timestamp = 234234L,
            isRead = true,
            messages = listOf( // Tin nhắn mới thêm vào đầu list
                Message(
                    messageID = "3",
                    senderUsername = "sleepy",
                    receiverUsername = "2",
                    content = "I'm trying to wake up in the morning but it's so hard",
                    timestamp = 234235L,
                    isRead = true
                ),
                Message(
                    messageID = "2",
                    senderUsername = "2",
                    receiverUsername = "sleepy",
                    content = "dfsfdsfsdfsdfdsfdsf fsdfsdf dsfsdf fsdf sdfdsf sdf sdfsd fsd fsdf sdf sd fsd fsd fsd fsdf sdfsdfsdfsdf",
                    timestamp = 234235L,
                    isRead = true
                ),
                Message(
                    messageID = "1",
                    senderUsername = "2",
                    receiverUsername = "sleepy",
                    content = " Hey, what's up?",
                    timestamp = 234235L,
                    isRead = true
                )
            )
        ),
        navHostController = rememberNavController()
    )
}

