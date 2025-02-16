package com.forrestgump.ig.ui.screens.messages.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.forrestgump.ig.utils.models.Conversation
import com.forrestgump.ig.utils.models.Message

@Composable
fun MessageDetailContent(
    myUsername: String,
    conversation: Conversation,
    innerPadding: PaddingValues
) {
    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground),
        reverseLayout = true // Latest message
    ) {
        items(conversation.messages) { message ->
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
                            model = conversation.userProfileImage,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            contentDescription = conversation.username
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
fun MessageDetailContentPreview() {
    MessageDetailContent(
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
        innerPadding = PaddingValues(0.dp)
    )
}