package com.forrestgump.ig.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.ui.screens.chat.components.NewChatList
import com.forrestgump.ig.ui.screens.chat.components.NewChatTopBar
import com.forrestgump.ig.utils.constants.Utils.MainBackground


@Composable
fun NewChatScreen(
    currentUser: User,
    users: List<User>,
    navHostController: NavHostController
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            NewChatTopBar(navHostController)
        }
    ) { innerPadding ->
        NewChatList(
            innerPadding = innerPadding,
            navHostController = navHostController,
            myUsername = currentUser.username,
            users = users
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NewChatScreenPreview() {
    // Sample current user
    val currentUser = User(
        userId = "1",
        username = "Alice",
        profileImage = "https://randomuser.me/api/portraits/women/1.jpg"
    )

    // Sample users list with 10 real images
    val sampleUsers = listOf(
        User(userId = "2", username = "Bob", profileImage = "https://randomuser.me/api/portraits/men/2.jpg"),
        User(userId = "3", username = "Charlie", profileImage = "https://randomuser.me/api/portraits/men/3.jpg"),
        User(userId = "4", username = "David", profileImage = "https://randomuser.me/api/portraits/men/4.jpg"),
        User(userId = "5", username = "Emma", profileImage = "https://randomuser.me/api/portraits/women/5.jpg"),
        User(userId = "6", username = "Sophia", profileImage = "https://randomuser.me/api/portraits/women/6.jpg"),
        User(userId = "7", username = "Michael", profileImage = "https://randomuser.me/api/portraits/men/7.jpg"),
        User(userId = "8", username = "Olivia", profileImage = "https://randomuser.me/api/portraits/women/8.jpg"),
        User(userId = "9", username = "Daniel", profileImage = "https://randomuser.me/api/portraits/men/9.jpg"),
        User(userId = "10", username = "Liam", profileImage = "https://randomuser.me/api/portraits/men/10.jpg"),
        User(userId = "11", username = "Ava", profileImage = "https://randomuser.me/api/portraits/women/11.jpg")
    )

    // Fake NavController for preview
    val fakeNavController = rememberNavController()

    NewChatScreen(
        currentUser = currentUser,
        users = sampleUsers,
        navHostController = fakeNavController
    )
}
