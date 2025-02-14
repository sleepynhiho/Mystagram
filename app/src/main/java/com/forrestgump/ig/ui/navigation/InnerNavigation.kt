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
import com.forrestgump.ig.ui.screens.home.HomeScreen
import com.forrestgump.ig.ui.screens.home.HomeViewModel
import com.forrestgump.ig.ui.screens.profile.MyProfileScreen
import com.forrestgump.ig.ui.screens.profile.ProfileViewModel
import com.forrestgump.ig.ui.screens.add.AddContentScreen
import com.forrestgump.ig.ui.screens.add.AddContentViewModel
import com.forrestgump.ig.ui.screens.messages.MessagesScreen
import com.forrestgump.ig.ui.screens.notification.NotificationScreen
import com.forrestgump.ig.ui.screens.search.SearchScreen


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
            MessagesScreen(myUsername = "sleepy")
        }
    }
}