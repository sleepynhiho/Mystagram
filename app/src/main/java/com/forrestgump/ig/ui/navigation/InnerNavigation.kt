package com.forrestgump.ig.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.forrestgump.ig.R
import com.forrestgump.ig.ui.screens.home.HomeScreen
import com.forrestgump.ig.ui.screens.home.HomeViewModel
import com.forrestgump.ig.ui.screens.profile.MyProfileScreen
import com.forrestgump.ig.ui.screens.profile.ProfileViewModel
import com.forrestgump.ig.ui.screens.add.AddContentScreen
import com.forrestgump.ig.ui.screens.add.AddContentViewModel
import com.forrestgump.ig.ui.screens.messages.MessageDetailScreen
import com.forrestgump.ig.ui.screens.messages.MessagesScreen
import com.forrestgump.ig.ui.screens.notification.NotificationScreen
import com.forrestgump.ig.ui.screens.search.SearchScreen
import com.forrestgump.ig.utils.models.Conversation
import com.forrestgump.ig.utils.models.Message


@UnstableApi
@Composable
fun InnerNavigation(
    contentPadding: PaddingValues,
    navHostController: NavHostController,
    viewModelHome: HomeViewModel = hiltViewModel(),
    viewModelAdd: AddContentViewModel = hiltViewModel(),
    viewModelProfile: ProfileViewModel,
) {

    NavHost(
        navController = navHostController,
        startDestination = Routes.HomeScreen.route
    ) {
        composable(
            route = Routes.HomeScreen.route,
            enterTransition = {
                fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(350))
            }
        ) {
            val uiState by viewModelHome.uiState.collectAsState()
            val uiStateProfile by viewModelProfile.uiState.collectAsState()


            HomeScreen(
                contentPadding = contentPadding,
                uiState = uiState,
                userProfileImage = uiStateProfile.profileImage,
                currentUsername = "",
                onAddStoryClicked = {
                    navHostController.navigate("${Routes.AddContentScreen.route}/STORY")
                },
                onStoryScreenClicked = viewModelHome::onStoryScreenClicked,
                onMessagesScreenClicked = {
                    navHostController.navigate(Routes.MessagesScreen.route)
                }
            )
        }

        composable(
            route = Routes.SearchScreen.route,
            enterTransition = {
                fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(350))
            }
        ) {
            val uiState by viewModelProfile.uiState.collectAsState()

            SearchScreen(
                uiState = uiState,
            )
        }

        composable(
            route = Routes.NotificationScreen.route,
            enterTransition = {
                fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(350))
            }
        ) {
            val uiState by viewModelProfile.uiState.collectAsState()

            NotificationScreen(
                uiState = uiState,
            )
        }

        composable(
            route = Routes.MyProfileScreen.route,
            enterTransition = {
                fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(350))
            }
        ) {
            val uiState by viewModelProfile.uiState.collectAsState()

            MyProfileScreen(
                uiState = uiState,
            )
        }


        composable(
            route = "${Routes.AddContentScreen.route}/{text}",
            arguments = listOf(
                navArgument("text") {
                    type = NavType.StringType
                }
            ),
            enterTransition = {
                slideInHorizontally(
                    animationSpec = tween(),
                    initialOffsetX = { -it }
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(),
                    targetOffsetX = { -it }
                )
            }
        ) {
            AddContentScreen(
                onBackClick = { }
            )
        }

        val dummyConversations = listOf(
            Conversation(
                username = "Alice",
                userProfileImage = "https://randomuser.me/api/portraits/women/1.jpg",
                timestamp = System.currentTimeMillis(),
                isRead = false,
                messages = listOf(
                    Message(
                        "101",
                        "sleepy",
                        "Alice",
                        "Hello Alice!",
                        System.currentTimeMillis(),
                        false
                    ),
                    Message(
                        "102",
                        "Alice",
                        "sleepy",
                        "Hey, how are you?",
                        System.currentTimeMillis(),
                        true
                    )
                )
            ),
            Conversation(
                username = "Bob",
                userProfileImage = "https://randomuser.me/api/portraits/men/2.jpg",
                timestamp = System.currentTimeMillis(),
                isRead = true,
                messages = listOf(
                    Message("103", "sleepy", "Bob", "Hi Bob!", System.currentTimeMillis(), false),
                    Message(
                        "104",
                        "Bob",
                        "sleepy",
                        "Let's meet up.",
                        System.currentTimeMillis(),
                        true
                    )
                )
            ),
            Conversation(
                username = "Charlie",
                userProfileImage = "https://randomuser.me/api/portraits/men/3.jpg",
                timestamp = System.currentTimeMillis(),
                isRead = false,
                messages = listOf(
                    Message(
                        "105",
                        "sleepy",
                        "Charlie",
                        "What's up?",
                        System.currentTimeMillis(),
                        false
                    ),
                    Message(
                        "106",
                        "Charlie",
                        "sleepy",
                        "All good!",
                        System.currentTimeMillis(),
                        true
                    )
                )
            ),
            Conversation(
                username = "David",
                userProfileImage = "https://randomuser.me/api/portraits/men/4.jpg",
                timestamp = System.currentTimeMillis(),
                isRead = true,
                messages = listOf(
                    Message(
                        "107",
                        "sleepy",
                        "David",
                        "Good morning!",
                        System.currentTimeMillis(),
                        true
                    ),
                    Message(
                        "108",
                        "David",
                        "sleepy",
                        "Morning! How's your day?",
                        System.currentTimeMillis(),
                        false
                    )
                )
            ),
            Conversation(
                username = "Eve",
                userProfileImage = "https://randomuser.me/api/portraits/women/5.jpg",
                timestamp = System.currentTimeMillis(),
                isRead = false,
                messages = listOf(
                    Message(
                        "109",
                        "sleepy",
                        "Eve",
                        "See you soon!",
                        System.currentTimeMillis(),
                        false
                    ),
                    Message("110", "Eve", "sleepy", "Bye!", System.currentTimeMillis(), true)
                )
            )
        )


        composable(
            route = Routes.MessagesScreen.route,
            enterTransition = {
                slideInHorizontally(
                    animationSpec = tween(),
                    initialOffsetX = { it }
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(),
                    targetOffsetX = { it }
                )
            }
        ) {
            MessagesScreen(
                myUsername = "sleepy",
                conversations = dummyConversations,
                navHostController = navHostController
            )
        }



        composable(
            route = "${Routes.MessageDetailScreen.route}/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType }),
            enterTransition = {
                slideInHorizontally(
                    animationSpec = tween(),
                    initialOffsetX = { it }
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(),
                    targetOffsetX = { it }
                )
            }
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: return@composable

            val conversation = dummyConversations.find { it.username == username }
                ?: return@composable

            MessageDetailScreen(
                myUsername = "sleepy",
                conversation = conversation,
                navHostController = navHostController
            )
        }

    }
}