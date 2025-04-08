package com.forrestgump.ig.ui.screens.chat.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.forrestgump.ig.R
import com.forrestgump.ig.data.models.Chat
import com.forrestgump.ig.data.models.MessageType
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.ui.screens.chat.ChatViewModel
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import java.util.Date

@Composable
fun NewChatList(
    users: List<User>,
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    currentUser: User,
    filterChats: List<Chat>
) {
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .height(40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = stringResource(R.string.search_new_chat),
                fontSize = 14.sp,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5F),
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(5.dp)
            )

            // Sticky Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .padding(5.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (searchText.isEmpty()) {
                    Text(
                        text = stringResource(R.string.search_placeholder),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                BasicTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 14.sp
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {})
                )
            }
        }

        val filteredUsers = users.filter {
            it.username.contains(
                searchText,
                ignoreCase = true
            ) && it.username != currentUser.username
        }

        // User List (Scrollable)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(horizontal = 10.dp)
        ) {
            item { NewChatHeader() }
            items(filteredUsers) { user ->
                val chat = filterChats.firstOrNull { chat ->
                    (chat.user1Id == user.userId || chat.user2Id == user.userId)
                }
                Log.d("NHII", "Chat: $chat.toString()")
                NewChatListItem(
                    otherUser = user,
                    currentUser = currentUser,
                    navHostController = navHostController,
                    chat = chat
                )
            }
        }
    }
}


@Composable
fun NewChatHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 10.dp),
            text = stringResource(R.string.new_chat_header),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp
            )
        )
    }
}

@Composable
fun NewChatListItem(
    otherUser: User,
    currentUser: User,
    chat: Chat?,
    navHostController: NavHostController,
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .height(72.dp)
            .clickable {
                if (chat != null) {
                    navHostController.navigate("ChatBoxScreen/${chat.chatId}")
                } else {
                    chatViewModel.createChatIfNotExists(currentUser, otherUser) { newChat ->
                        navHostController.navigate("ChatBoxScreen/${newChat.chatId}")
                    }
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        Surface(
            modifier = Modifier
                .size(56.dp),
            shape = CircleShape,
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = otherUser.profileImage,
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(id = R.string.profile_image)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = otherUser.username,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun NewChatListPreview() {
    // Sample users with real profile images
    val sampleUsers = listOf(
        User(
            userId = "1",
            username = "Alice",
            profileImage = "https://randomuser.me/api/portraits/women/1.jpg"
        ),
        User(
            userId = "2",
            username = "Bob",
            profileImage = "https://randomuser.me/api/portraits/men/2.jpg"
        ),
        User(
            userId = "3",
            username = "Charlie",
            profileImage = "https://randomuser.me/api/portraits/men/3.jpg"
        ),
        User(
            userId = "4",
            username = "David",
            profileImage = "https://randomuser.me/api/portraits/men/4.jpg"
        ),
        User(
            userId = "5",
            username = "Emma",
            profileImage = "https://randomuser.me/api/portraits/women/5.jpg"
        ),
    )

    // Fake NavController for preview
    val fakeNavController = rememberNavController()

    // Function to filter the list of users
    val filterChats = listOf(
        Chat(
            chatId = "1_2", // Combination of user1Id and user2Id
            user1Id = "1", // User ID 1
            user2Id = "2", // User ID 2
            user1Username = "Alice",
            user2Username = "Bob",
            user1ProfileImage = "https://randomuser.me/api/portraits/women/1.jpg",
            user2ProfileImage = "https://randomuser.me/api/portraits/men/2.jpg",
            lastMessage = "Hey Bob, how are you?",
            lastMessageType = MessageType.TEXT,
            user1Read = true,
            user2Read = false,
            lastMessageTime = Date() // Current timestamp
        ),
        Chat(
            chatId = "3_4",
            user1Id = "3",
            user2Id = "4",
            user1Username = "Charlie",
            user2Username = "David",
            user1ProfileImage = "https://randomuser.me/api/portraits/men/3.jpg",
            user2ProfileImage = "https://randomuser.me/api/portraits/men/4.jpg",
            lastMessage = "David, are you free for dinner?",
            lastMessageType = MessageType.TEXT,
            user1Read = false,
            user2Read = true,
            lastMessageTime = Date() // Current timestamp
        ),
        Chat(
            chatId = "5_1",
            user1Id = "5",
            user2Id = "1",
            user1Username = "Emma",
            user2Username = "Alice",
            user1ProfileImage = "https://randomuser.me/api/portraits/women/5.jpg",
            user2ProfileImage = "https://randomuser.me/api/portraits/women/1.jpg",
            lastMessage = "Emma, do you have the notes from the meeting?",
            lastMessageType = MessageType.TEXT,
            user1Read = true,
            user2Read = false,
            lastMessageTime = Date() // Current timestamp
        ),
        Chat(
            chatId = "2_3",
            user1Id = "2",
            user2Id = "3",
            user1Username = "Bob",
            user2Username = "Charlie",
            user1ProfileImage = "https://randomuser.me/api/portraits/men/2.jpg",
            user2ProfileImage = "https://randomuser.me/api/portraits/men/3.jpg",
            lastMessage = "Got the files, thanks for sending them over.",
            lastMessageType = MessageType.TEXT,
            user1Read = false,
            user2Read = false,
            lastMessageTime = Date() // Current timestamp
        ),
        Chat(
            chatId = "4_5",
            user1Id = "4",
            user2Id = "5",
            user1Username = "David",
            user2Username = "Emma",
            user1ProfileImage = "https://randomuser.me/api/portraits/men/4.jpg",
            user2ProfileImage = "https://randomuser.me/api/portraits/women/5.jpg",
            lastMessage = "Hey, I saw the new movie. It was awesome!",
            lastMessageType = MessageType.TEXT,
            user1Read = true,
            user2Read = true,
            lastMessageTime = Date() // Current timestamp
        )
    )

    NewChatList(
        users = sampleUsers,
        innerPadding = PaddingValues(8.dp),
        navHostController = fakeNavController,
        filterChats = filterChats,
        currentUser = User()
    )
}
