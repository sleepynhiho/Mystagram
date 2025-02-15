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
import com.forrestgump.ig.ui.screens.messages.MessagesScreen
import com.forrestgump.ig.ui.screens.notification.NotificationScreen
import com.forrestgump.ig.ui.screens.search.SearchScreen
import com.forrestgump.ig.utils.models.Conversation


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
                currentUserId = "",
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

        composable(
            route = Routes.MessagesScreen.route,
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
            MessagesScreen(
                myUsername = "sleepy",
                conversations = listOf(
                    Conversation("1", "Alice", "https://randomuser.me/api/portraits/women/1.jpg", "Hello!", System.currentTimeMillis(), false),
                    Conversation("2", "Bob", "https://randomuser.me/api/portraits/men/2.jpg", "How are you?", System.currentTimeMillis(), true),
                    Conversation("3", "Charlie", "https://randomuser.me/api/portraits/men/3.jpg", "What's up?", System.currentTimeMillis(), false),
                    Conversation("4", "David", "https://randomuser.me/api/portraits/men/4.jpg", "Good morning!", System.currentTimeMillis(), true),
                    Conversation("5", "Eve", "https://randomuser.me/api/portraits/women/5.jpg", "See you soon!", System.currentTimeMillis(), false),
                    Conversation("6", "Frank", "https://randomuser.me/api/portraits/men/6.jpg", "Nice to meet you.", System.currentTimeMillis(), true),
                    Conversation("7", "Grace", "https://randomuser.me/api/portraits/women/7.jpg", "Can we talk?", System.currentTimeMillis(), false),
                    Conversation("8", "Hank", "https://randomuser.me/api/portraits/men/8.jpg", "Just chilling.", System.currentTimeMillis(), true),
                    Conversation("9", "Ivy", "https://randomuser.me/api/portraits/women/9.jpg", "Call me later.", System.currentTimeMillis(), false),
                    Conversation("10", "Jack", "https://randomuser.me/api/portraits/men/10.jpg", "Happy birthday!", System.currentTimeMillis(), true),
                    Conversation("11", "Kate", "https://randomuser.me/api/portraits/women/11.jpg", "Let's hang out.", System.currentTimeMillis(), false),
                    Conversation("12", "Leo", "https://randomuser.me/api/portraits/men/12.jpg", "See you tomorrow.", System.currentTimeMillis(), true),
                    Conversation("13", "Mia", "https://randomuser.me/api/portraits/women/13.jpg", "I'll be there.", System.currentTimeMillis(), false),
                    Conversation("14", "Noah", "https://randomuser.me/api/portraits/men/14.jpg", "Sure thing!", System.currentTimeMillis(), true),
                    Conversation("15", "Olivia", "https://randomuser.me/api/portraits/women/15.jpg", "Text me later.", System.currentTimeMillis(), false)
                ),
                navHostController = navHostController
            )
        }


    }
}