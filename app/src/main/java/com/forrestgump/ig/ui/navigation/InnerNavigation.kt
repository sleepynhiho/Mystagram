package com.forrestgump.ig.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.forrestgump.ig.ui.screens.home.HomeScreen
import com.forrestgump.ig.ui.screens.home.HomeViewModel
import com.forrestgump.ig.ui.screens.profile.MyProfileScreen
import com.forrestgump.ig.ui.screens.profile.ProfileViewModel
import com.forrestgump.ig.ui.screens.addStory.AddStoryScreen
import com.forrestgump.ig.ui.screens.chat.ChatBoxScreen
import com.forrestgump.ig.ui.screens.chat.ChatScreen
import com.forrestgump.ig.ui.screens.notification.NotificationScreen
import com.forrestgump.ig.ui.screens.search.SearchScreen
import com.forrestgump.ig.data.models.Chat
import com.forrestgump.ig.data.models.Message
import com.forrestgump.ig.data.models.Notification
import com.forrestgump.ig.data.models.NotificationType
import com.forrestgump.ig.ui.screens.auth.LoginScreen
import com.forrestgump.ig.ui.screens.auth.SignupScreen
import com.forrestgump.ig.ui.screens.addPost.AddPostScreen
import com.forrestgump.ig.ui.screens.addPost.AddPostViewModel
import com.forrestgump.ig.ui.screens.addStory.AddStoryViewModel
import java.util.Date
import com.forrestgump.ig.ui.screens.settings.SettingsScreen


@UnstableApi
@Composable
fun InnerNavigation(
    contentPadding: PaddingValues,
    navHostController: NavHostController,
    viewModelHome: HomeViewModel = hiltViewModel(),
    viewModelAdd: AddPostViewModel = hiltViewModel(),
    viewModelProfile: ProfileViewModel,
) {

    NavHost(
        navController = navHostController, startDestination = Routes.HomeScreen.route
    ) {
        composable(route = Routes.HomeScreen.route, enterTransition = {
            fadeIn(animationSpec = tween(350))
        }, exitTransition = {
            fadeOut(animationSpec = tween(350))
        }) {
            val uiState by viewModelHome.uiState.collectAsState()
            val uiStateProfile by viewModelProfile.uiState.collectAsState()


            HomeScreen(contentPadding = contentPadding,
                uiState = uiState,
                userProfileImage = uiStateProfile.profileImage,
                currentUsername = "",
                onAddStoryClicked = {
                    navHostController.navigate(Routes.AddStoryScreen.route)
                },
                onStoryScreenClicked = viewModelHome::onStoryScreenClicked,
                onMessagesScreenClicked = {
                    navHostController.navigate(Routes.MessagesScreen.route)
                })
        }

        composable(route = Routes.SearchScreen.route, enterTransition = {
            fadeIn(animationSpec = tween(350))
        }, exitTransition = {
            fadeOut(animationSpec = tween(350))
        }) {
            val uiState by viewModelProfile.uiState.collectAsState()

            SearchScreen(
                uiState = uiState,
            )
        }

        val dummyNotifications = listOf(
            Notification(
                notificationId = "1",
                receiverId = "user_123",
                senderId = "user_456",
                senderUsername = "jane_doe",
                senderProfileImage = "https://randomuser.me/api/portraits/women/1.jpg",
                postId = "post_789",
                isRead = false,
                type = NotificationType.LIKE,
                timestamp = Date()
            ), Notification(
                notificationId = "2",
                receiverId = "user_123",
                senderId = "user_789",
                senderUsername = "john_smith",
                senderProfileImage = "https://randomuser.me/api/portraits/men/2.jpg",
                postId = "post_321",
                isRead = true,
                type = NotificationType.COMMENT,
                timestamp = Date()
            ), Notification(
                notificationId = "3",
                receiverId = "user_123",
                senderId = "user_101",
                senderUsername = "alice_wonder",
                senderProfileImage = "https://randomuser.me/api/portraits/women/3.jpg",
                isRead = false,
                type = NotificationType.FOLLOW,
                timestamp = Date()
            ), Notification(
                notificationId = "4",
                receiverId = "user_123",
                senderId = "user_202",
                senderUsername = "bob_marley",
                senderProfileImage = "https://randomuser.me/api/portraits/men/4.jpg",
                isRead = false,
                type = NotificationType.FOLLOW_REQUEST,
                timestamp = Date()
            ), Notification(
                notificationId = "5",
                receiverId = "user_123",
                senderId = "user_303",
                senderUsername = "charlie_brownyloveyu",
                senderProfileImage = "https://randomuser.me/api/portraits/men/5.jpg",
                isRead = true,
                type = NotificationType.FOLLOW_ACCEPTED,
                timestamp = Date()
            )
        )



        composable(route = Routes.NotificationScreen.route, enterTransition = {
            fadeIn(animationSpec = tween(350))
        }, exitTransition = {
            fadeOut(animationSpec = tween(350))
        }) {

            NotificationScreen(
                notifications = dummyNotifications, navHostController = navHostController
            )
        }

        composable(route = Routes.MyProfileScreen.route, enterTransition = {
            fadeIn(animationSpec = tween(350))
        }, exitTransition = {
            fadeOut(animationSpec = tween(350))
        }) {
            val uiState by viewModelProfile.uiState.collectAsState()

            MyProfileScreen(
                uiState = uiState, navController = navHostController
            )
        }


        composable(
            route = Routes.AddStoryScreen.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 350)
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(durationMillis = 350)
                )
            }
        ) {
            AddStoryScreen(
                navHostController
            )
        }

        composable(
            route = Routes.AddPostScreen.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 350)
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(durationMillis = 350)
                )
            }
        ) {
            AddPostScreen(
                navHostController
            )
        }

        val dummyChats = listOf(
            Chat(
                chatId = "chat_1",
                user1Username = "sleepy",
                user2Username = "john_doe",
                user1ProfileImage = "https://i.pravatar.cc/150?img=1",
                user2ProfileImage = "https://i.pravatar.cc/150?img=2",
                lastMessage = "See you tomorrow!",
                lastMessageTime = 1708209200000L,
                lastMessageRead = true
            ), Chat(
                chatId = "chat_2",
                user1Username = "sleepy",
                user2Username = "alice",
                user1ProfileImage = "https://i.pravatar.cc/150?img=3",
                user2ProfileImage = "https://i.pravatar.cc/150?img=4",
                lastMessage = "Let's meet at 5PM",
                lastMessageTime = 1708213200000L,
                lastMessageRead = false
            ), Chat(
                chatId = "chat_3",
                user1Username = "sleepy",
                user2Username = "bob",
                user1ProfileImage = "https://i.pravatar.cc/150?img=5",
                user2ProfileImage = "https://i.pravatar.cc/150?img=6",
                lastMessage = "I'll check and let you know",
                lastMessageTime = 1708215000000L,
                lastMessageRead = true
            ), Chat(
                chatId = "chat_4",
                user1Username = "sleepy",
                user2Username = "charlie",
                user1ProfileImage = "https://i.pravatar.cc/150?img=7",
                user2ProfileImage = "https://i.pravatar.cc/150?img=8",
                lastMessage = "Sure, I'll send it now",
                lastMessageTime = 1708217000000L,
                lastMessageRead = true
            ), Chat(
                chatId = "chat_5",
                user1Username = "sleepy",
                user2Username = "david",
                user1ProfileImage = "https://i.pravatar.cc/150?img=9",
                user2ProfileImage = "https://i.pravatar.cc/150?img=10",
                lastMessage = "Thanks for your help!",
                lastMessageTime = 1708219000000L,
                lastMessageRead = false
            ), Chat(
                chatId = "chat_6",
                user1Username = "sleepy",
                user2Username = "emma",
                user1ProfileImage = "https://i.pravatar.cc/150?img=11",
                user2ProfileImage = "https://i.pravatar.cc/150?img=12",
                lastMessage = "Let's catch up soon!",
                lastMessageTime = 1708221000000L,
                lastMessageRead = true
            ), Chat(
                chatId = "chat_7",
                user1Username = "sleepy",
                user2Username = "frank",
                user1ProfileImage = "https://i.pravatar.cc/150?img=13",
                user2ProfileImage = "https://i.pravatar.cc/150?img=14",
                lastMessage = "I'll be there in 10 minutes",
                lastMessageTime = 1708223000000L,
                lastMessageRead = false
            ), Chat(
                chatId = "chat_8",
                user1Username = "sleepy",
                user2Username = "grace",
                user1ProfileImage = "https://i.pravatar.cc/150?img=15",
                user2ProfileImage = "https://i.pravatar.cc/150?img=16",
                lastMessage = "Great job on the project!",
                lastMessageTime = 1708225000000L,
                lastMessageRead = true
            ), Chat(
                chatId = "chat_9",
                user1Username = "sleepy",
                user2Username = "henry",
                user1ProfileImage = "https://i.pravatar.cc/150?img=17",
                user2ProfileImage = "https://i.pravatar.cc/150?img=18",
                lastMessage = "Can you send me the details?",
                lastMessageTime = 1708227000000L,
                lastMessageRead = true
            ), Chat(
                chatId = "chat_10",
                user1Username = "sleepy",
                user2Username = "isabella",
                user1ProfileImage = "https://i.pravatar.cc/150?img=19",
                user2ProfileImage = "https://i.pravatar.cc/150?img=20",
                lastMessage = "See you at the meeting!",
                lastMessageTime = 1708229000000L,
                lastMessageRead = false
            )
        )

        val dummyMessages = listOf(
            Message("chat_1", "john_doe", "Hey, how's it going?", 1708208000000L),
            Message("chat_1", "sleepy", "I'm good, what about you?", 1708208500000L),
            Message("chat_1", "john_doe", "Doing great! See you tomorrow!", 1708209200000L),

            Message("chat_2", "alice", "Hey, are we still meeting at 5PM?", 1708212000000L),
            Message("chat_2", "sleepy", "Yes, see you then!", 1708212500000L),

            Message("chat_3", "bob", "Did you check the document?", 1708214000000L),
            Message("chat_3", "sleepy", "Not yet, I’ll check and let you know.", 1708215000000L),

            Message("chat_4", "charlie", "Can you send me the file?", 1708216000000L),
            Message("chat_4", "sleepy", "Sure, I'll send it now.", 1708217000000L),

            Message("chat_5", "david", "Thanks for your help!", 1708218000000L),
            Message("chat_5", "sleepy", "No problem at all!", 1708219000000L),

            Message("chat_6", "emma", "Long time no see!", 1708220000000L),
            Message("chat_6", "sleepy", "Yeah! Let's catch up soon!", 1708221000000L),

            Message("chat_7", "frank", "I'm on my way.", 1708222000000L),
            Message("chat_7", "sleepy", "Great! I'll be there in 10 minutes.", 1708223000000L),

            Message("chat_8", "grace", "The project presentation was awesome!", 1708224000000L),
            Message("chat_8", "sleepy", "Thanks! Great job, team!", 1708225000000L),

            Message("chat_9", "henry", "Can you send me the details?", 1708226000000L),
            Message("chat_9", "sleepy", "Sure, I'll forward them now.", 1708227000000L),

            Message("chat_10", "isabella", "Are you joining the meeting?", 1708228000000L),
            Message("chat_10", "sleepy", "Yes, see you there!", 1708229000000L)
        )



        composable(route = Routes.MessagesScreen.route, enterTransition = {
            slideInHorizontally(animationSpec = tween(), initialOffsetX = { it })
        }, exitTransition = {
            slideOutHorizontally(animationSpec = tween(), targetOffsetX = { it })
        }) {
            ChatScreen(
                myUsername = "sleepy", chats = dummyChats, navHostController = navHostController
            )
        }


        composable(route = "${Routes.ChatBoxScreen.route}/{chatId}",
            arguments = listOf(navArgument("chatId") { type = NavType.StringType }),
            enterTransition = {
                slideInHorizontally(animationSpec = tween(), initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = tween(), targetOffsetX = { it })
            }) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable

            val chat = dummyChats.find { it.chatId == chatId } ?: return@composable

            ChatBoxScreen(
                myUsername = "sleepy",
                chat = chat,
                messages = dummyMessages,
                navHostController = navHostController
            )
        }

        composable(route = Routes.SettingsScreen.route, enterTransition = {
            fadeIn(animationSpec = tween(350))
        }, exitTransition = {
            fadeOut(animationSpec = tween(350))
        }) {
            SettingsScreen(navController = navHostController)
        }

        composable(route = Routes.LoginScreen.route, enterTransition = {
            fadeIn(animationSpec = tween(350))
        }, exitTransition = {
            fadeOut(animationSpec = tween(350))
        }) {
            LoginScreen(
                navController = navHostController,
                authViewModel = hiltViewModel(),
            )
        }

        composable(route = Routes.InnerContainer.route, enterTransition = {
            fadeIn(
                animationSpec = tween(350)
            )
        }, exitTransition = {
            fadeOut(
                animationSpec = tween(350)
            )
        }) {
            InnerContainer()
        }

        composable(route = Routes.SignupScreen.route, enterTransition = {
            fadeIn(animationSpec = tween(350))
        }, exitTransition = {
            fadeOut(animationSpec = tween(350))
        }) {
            SignupScreen(
                navController = navHostController,
                authViewModel = hiltViewModel(),
            )
        }
    }
}