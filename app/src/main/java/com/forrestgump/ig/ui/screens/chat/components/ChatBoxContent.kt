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

@Composable
fun ChatBoxContent(
    myUsername: String,
    chat: Chat,
    messages: List<Message>,
    innerPadding: PaddingValues
) {
    val otherUsername = if (myUsername == chat.user1Username) chat.user2Username else chat.user1Username
    val otherUserProfileImage = if (myUsername == chat.user1Username) chat.user2ProfileImage else chat.user1ProfileImage

    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground),
        reverseLayout = true // Latest message
    ) {
        items(messages) { message ->
            val isMine = myUsername == message.senderUsername

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
                    Text(
                        text = message.content,
                        color = if (!isMine) Color.Black else Color.White,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ChatBoxContentPreview() {
    val chat = Chat(
        chatId = "123",
        user1Username = "sleepy",
        user2Username = "John Doe",
        user1ProfileImage = R.drawable.default_profile_img.toString(),
        user2ProfileImage = R.drawable.default_profile_img.toString(),
        lastMessage = "Hey, what's up?",
        lastMessageTime = 234235L,
        lastMessageRead = true
    )

    val messages = listOf(
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
            content = "Long message content to check UI layout",
            timestamp = 234235L,
            isRead = true
        ),
        Message(
            messageID = "1",
            senderUsername = "John Doe",
            content = "Hey, what's up?",
            timestamp = 234235L,
            isRead = true
        )
    )

    ChatBoxContent(
        myUsername = "sleepy",
        chat = chat,
        messages = messages,
        innerPadding = PaddingValues(0.dp)
    )
}

