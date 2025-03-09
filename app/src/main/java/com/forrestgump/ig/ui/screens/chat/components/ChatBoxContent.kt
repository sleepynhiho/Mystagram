package com.forrestgump.ig.ui.screens.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.forrestgump.ig.R
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.forrestgump.ig.data.models.Chat
import com.forrestgump.ig.data.models.Message
import com.forrestgump.ig.data.models.MessageType
import java.util.Date

@Composable
fun ChatBoxContent(
    myUserId: String,
    chat: Chat,
    messages: List<Message>,
    innerPadding: PaddingValues
) {
    val otherUsername = if (myUserId == chat.user1Id) chat.user2Username else chat.user1Username
    val otherUserProfileImage = if (myUserId == chat.user1Id) chat.user2ProfileImage else chat.user1ProfileImage

    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground),
        reverseLayout = true // Latest message
    ) {
        items(messages) { message ->
            val isMine = myUserId == message.senderId

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (!isMine) Arrangement.Start else Arrangement.End
            ) {
                if (!isMine) {
                    Surface(
                        modifier = Modifier.size(30.dp),
                        shape = CircleShape
                    ) {
                        AsyncImage(
                            model = otherUserProfileImage,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            contentDescription = otherUsername
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                }

                Box(
                    modifier = Modifier.background(
                        color = if (!isMine) Color(0xFFEFEFEF) else Color(
                            0xFF3897F0
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .widthIn(min = 10.dp, max = 190.dp)
                ) {
                    message.content?.let {
                        Text(
                            text = it,
                            color = if (!isMine) Color.Black else Color.White,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ChatBoxContentPreview() {
    val sampleChat = Chat(
        chatId = "chat123",
        user1Id = "user1",
        user2Id = "user2",
        user1Username = "Alice",
        user2Username = "Bob",
        user1ProfileImage = "https://randomuser.me/api/portraits/women/1.jpg",
        user2ProfileImage = "https://randomuser.me/api/portraits/men/1.jpg",
        lastMessage = "Hey, how are you?",
        lastMessageType = MessageType.TEXT,
        user1Read = true,
        user2Read = false,
        lastMessageTime = Date()
    )

    val sampleMessages = listOf(
        Message("msg1", "user1", MessageType.TEXT, "Hello!", null, true, Date()),
        Message("msg2", "user2", MessageType.IMAGE, null, "https://picsum.photos/200", false, Date()),
        Message("msg3", "user1", MessageType.TEXT, "What's up?", null, true, Date()),
        Message("msg4", "user2", MessageType.TEXT, "Not much, you?", null, false, Date()),
        Message("msg5", "user1", MessageType.IMAGE, null, "https://picsum.photos/201", true, Date()),
        Message("msg6", "user2", MessageType.TEXT, "Nice pic!", null, false, Date()),
        Message("msg7", "user1", MessageType.TEXT, "Thanks!", null, true, Date()),
        Message("msg8", "user2", MessageType.IMAGE, null, "https://picsum.photos/202", false, Date()),
        Message("msg9", "user1", MessageType.TEXT, "Where was that?", null, true, Date()),
        Message("msg10", "user2", MessageType.TEXT, "At the beach!", null, false, Date())
    )

    ChatBoxContent(
        myUserId = "user1",
        chat = sampleChat,
        messages = sampleMessages,
        innerPadding = PaddingValues()
    )
}
