package com.forrestgump.ig.ui.screens.messages.components

import androidx.compose.foundation.background
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
import coil.compose.AsyncImage
import com.forrestgump.ig.R
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.forrestgump.ig.utils.models.Conversation

@Composable
fun MessagesList(
    conversations: List<Conversation>,
    innerPadding: PaddingValues
) {
    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MainBackground),
        content = {
            item { MessagesHeader() }
            items(conversations) {
                conversation -> MessagesListItem(conversation = conversation)
            }
        }
    )
}

@Composable
fun MessagesHeader() {
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
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 27.sp
            )
        )
    }
}

@Composable
fun MessagesListItem(
    conversation: Conversation
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MainBackground)
            .height(96.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        Surface(
            modifier = Modifier
                .size(74.dp),
            shape = CircleShape,
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = conversation.userProfileImage,
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(id = R.string.profile_image)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = conversation.username,
                style = TextStyle(
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Text(
                text = conversation.lastMessage,
                style = TextStyle(
                    fontSize = 19.sp,
                    fontWeight = if (conversation.isRead) FontWeight.Normal else FontWeight.Bold,
                    color = if (conversation.isRead) Color(0xFF737373) else MaterialTheme.colorScheme.onBackground
                )
            )
        }

        if (!conversation.isRead) {
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
fun MessagesListPreview() {
    MessagesList(
        conversations = listOf(
            Conversation(
                userID = "1",
                username = "sleepy",
                userProfileImage = R.drawable.default_profile_img.toString(),
                lastMessage = "Good morning!",
                timestamp = 234234L,
                isRead = false
            ),
            Conversation(
                userID = "2",
                username = "John Doe",
                userProfileImage = R.drawable.default_profile_img.toString(),
                lastMessage = "Hey, what's up?",
                timestamp = 234234L,
                isRead = true
            )
        ),
        innerPadding = PaddingValues(0.dp)
    )
}