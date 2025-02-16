package com.forrestgump.ig.ui.screens.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.forrestgump.ig.R
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.forrestgump.ig.ui.screens.messages.components.MessagesTopBar
import com.forrestgump.ig.ui.screens.messages.components.MessagesList
import com.forrestgump.ig.utils.models.Conversation
import com.forrestgump.ig.utils.models.Message


@Composable
fun MessagesScreen(myUsername: String,
                   conversations: List<Conversation>,
                   navHostController: NavHostController) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground),
        topBar = {
            MessagesTopBar(myUsername, navHostController)
        }
    ) { innerPadding ->
        MessagesList(
            conversations = conversations,
            innerPadding = innerPadding,
            navHostController = navHostController
        )
    }
}


@Preview
@Composable
fun MessagesScreenPreview() {
    MessagesScreen(
        myUsername = "sleepy",
        conversations = listOf(
            Conversation(
                username = "Alice",
                userProfileImage = R.drawable.default_profile_img.toString(),
                timestamp = System.currentTimeMillis(),
                isRead = false,
                messages = listOf(
                    Message(
                        messageID = "101",
                        senderUsername = "1",
                        receiverUsername = "sleepy",
                        content = "Hello!",
                        timestamp = System.currentTimeMillis(),
                        isRead = false
                    )
                )
            ),
            Conversation(
                username = "Bob",
                userProfileImage = R.drawable.default_profile_img.toString(),
                timestamp = System.currentTimeMillis(),
                isRead = true,
                messages = listOf(
                    Message(
                        messageID = "102",
                        senderUsername = "2",
                        receiverUsername = "sleepy",
                        content = "How are you?",
                        timestamp = System.currentTimeMillis(),
                        isRead = true
                    )
                )
            )
        ),
        navHostController = rememberNavController()
    )
}

