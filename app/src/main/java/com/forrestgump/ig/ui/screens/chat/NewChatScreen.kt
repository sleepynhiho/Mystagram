package com.forrestgump.ig.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.ui.screens.chat.components.NewChatList
import com.forrestgump.ig.ui.screens.chat.components.NewChatTopBar
import com.forrestgump.ig.utils.constants.Utils.MainBackground


@Composable
fun NewChatScreen(
    currentUser: User,
    navHostController: NavHostController,
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    // fix: change logic to following users
    val users by chatViewModel.users.observeAsState(emptyList())

    LaunchedEffect(currentUser.userId) {
        chatViewModel.getChatsForUser(currentUser.userId)
    }
    val chats by chatViewModel.chatsState.collectAsState()

    LaunchedEffect(Unit) {
        chatViewModel.loadUsers()
    }
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
            users = users,
            currentUser = currentUser,
            filterChats = chats
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

    // Fake NavController for preview
    val fakeNavController = rememberNavController()

    NewChatScreen(
        currentUser = currentUser,
        navHostController = fakeNavController
    )
}
