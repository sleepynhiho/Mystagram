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
    data object NotificationScreen: Routes(iconOutlined = R.drawable.heart_outlined, iconFilled = R.drawable.heart_filled, name = "Notification", route = "NotificationScreen")
    data object AddStoryScreen: Routes(name = "AddStory", route = "AddStoryScreen")
    data object MyProfileScreen: Routes(name = "MyProfile", route = "MyProfileScreen")
    data object UserProfileScreen: Routes(name = "UserProfile", route = "UserProfileScreen/{userId}") // New route for viewing other users profiles
    data object SettingsScreen: Routes(name = "Settings", route = "SettingsScreen")
    data object ChatScreen: Routes(name = "Chat", route = "ChatScreen")
    data object ChatBoxScreen: Routes(name = "ChatBox", route = "ChatBoxScreen")
    data object NewChatScreen: Routes(name = "NewChat", route = "NewChat")
    data object SignupScreen: Routes(name = "Signup", route = "SignupScreen")
    data object LoginScreen: Routes(name = "Login", route = "LoginScreen")
    data object FollowerScreen: Routes(name = "Follower", route = "FollowerScreen")
    data object FollowingScreen: Routes(name = "Following", route = "FollowingScreen")
    data object EditProfileScreen: Routes(name = "EditProfile", route="EditProfileScreen")
    data object EditLocationScreen: Routes(name = "EditLocation", route="EditLocationScreen") // New route
    data object AddPostScreen: Routes(iconOutlined = R.drawable.add_outlined, name = "AddPost", route = "AddPostScreen")
    data object AddPostDetailScreen : Routes(name = "AddPostDetail", route =  "AddPostDetailScreen")
    data object PostDetailScreen: Routes(name = "PostDetail", route = "PostDetailScreen/{postId}")

    data object Items {
        val bottomNavItems = listOf(
            HomeScreen,
            SearchScreen,
            AddPostScreen,
            NotificationScreen,
            MyProfileScreen
        )
    }
}