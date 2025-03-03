package com.forrestgump.ig.ui.navigation

import com.forrestgump.ig.R

sealed class Routes(
    val name: String = "",
    val route: String = "",
    val iconOutlined: Int? = null,
    val iconFilled: Int? = null
) {
    data object SplashScreen: Routes(name = "Splash", route = "SplashScreen")
    data object InnerContainer: Routes(name = "InnerScreen", route = "InnerContainer")
    data object HomeScreen: Routes(iconOutlined = R.drawable.home_outlined, iconFilled = R.drawable.home_filled, name = "Home", route = "HomeScreen")
    data object SearchScreen: Routes(iconOutlined = R.drawable.search, iconFilled = R.drawable.search_selected, name = "Search", route = "SearchScreen")
    data object AddPostScreen: Routes(iconOutlined = R.drawable.add_outlined, name = "AddPost", route = "AddPostScreen")
    data object NotificationScreen: Routes(iconOutlined = R.drawable.heart_outlined, iconFilled = R.drawable.heart_filled, name = "Notification", route = "NotificationScreen")
    data object AddStoryScreen: Routes(name = "AddStory", route = "AddStoryScreen")
    data object MyProfileScreen: Routes(name = "MyProfile", route = "MyProfileScreen")
    data object SettingsScreen: Routes(name = "Settings", route = "SettingsScreen")
    data object MessagesScreen: Routes(name = "Messages", route = "MessagesScreen")
    data object ChatBoxScreen: Routes(name = "ChatBox", route = "message_detail")
    data object SignupScreen: Routes(name = "Signup", route = "SignupScreen")
    data object LoginScreen: Routes(name = "Login", route = "LoginScreen")

    data object Items {
        val bottomNavItems = listOf(
            HomeScreen,
            SearchScreen,
            AddPostScreen,
            NotificationScreen,
            MyProfileScreen
        )

        val topNavItems = listOf(
            MessagesScreen
        )
    }
}