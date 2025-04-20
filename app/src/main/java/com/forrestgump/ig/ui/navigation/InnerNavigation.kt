package com.forrestgump.ig.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.Log
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
import com.forrestgump.ig.data.models.Notification
import com.forrestgump.ig.data.models.NotificationType
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.ui.screens.addPost.AddPostDetailScreen
import com.forrestgump.ig.ui.screens.auth.LoginScreen
import com.forrestgump.ig.ui.screens.auth.SignupScreen
import com.forrestgump.ig.ui.screens.addPost.AddPostScreen
import com.forrestgump.ig.ui.screens.chat.NewChatScreen
import com.forrestgump.ig.ui.screens.addPost.AddPostViewModel
import com.forrestgump.ig.ui.screens.addPost.SelectLocationScreen
import com.forrestgump.ig.ui.screens.profile.EditProfileScreen
import com.forrestgump.ig.ui.screens.profile.FollowScreen
import com.forrestgump.ig.ui.screens.profile.PostDetailScreen
import java.util.Date
import com.forrestgump.ig.ui.screens.settings.SettingsScreen
import com.forrestgump.ig.ui.screens.story.StoryViewModel
import com.forrestgump.ig.ui.viewmodels.UserViewModel
import com.forrestgump.ig.ui.screens.search.SearchViewModel
import com.forrestgump.ig.ui.screens.profile.EditLocationScreen
import com.forrestgump.ig.ui.screens.userprofile.UserProfileScreen
import com.forrestgump.ig.ui.screens.userprofile.UserProfileViewModel
import com.forrestgump.ig.ui.screens.checkout.CheckoutScreen
import com.forrestgump.ig.ui.screens.profile.FollowViewModel
import com.forrestgump.ig.ui.screens.profile.PostOptionsViewModel
import com.google.firebase.firestore.FirebaseFirestore

@UnstableApi
@Composable
fun InnerNavigation(
    contentPadding: PaddingValues,
    navHostController: NavHostController,
    viewModelHome: HomeViewModel = hiltViewModel(),
    viewModelProfile: ProfileViewModel,
    userViewModel: UserViewModel,
    storyViewModel: StoryViewModel,
    searchViewModel: SearchViewModel,
    viewModelOtherUserProfile: UserProfileViewModel,
    viewModelFollow: FollowViewModel,
    optionsViewModel: PostOptionsViewModel,
    viewModelOfAddPost: AddPostViewModel,
) {
    val currentUser by userViewModel.user.collectAsState()
    // Trong Activity hoặc các composable cha
    NavHost(
        navController = navHostController, startDestination = Routes.HomeScreen.route
    ) {
        composable(route = Routes.HomeScreen.route, enterTransition = {
            fadeIn(animationSpec = tween(350))
        }, exitTransition = {
            fadeOut(animationSpec = tween(350))
        }) {
            val uiState by viewModelHome.uiState.collectAsState()
            val userStories by storyViewModel.userStories.observeAsState(emptyList())


            LaunchedEffect(Unit) {
                storyViewModel.fetchUserStories()
                Log.d("NHII", "Nhi is fetching story ")
            }

            LaunchedEffect(userStories) {
                currentUser?.let { it1 -> viewModelHome.updateUserStories(userStories, it1) }
            }

            LaunchedEffect(Unit) {
                viewModelHome.loadNextPosts()
            }


            currentUser?.let { it1 ->
                HomeScreen(
                    viewModel = viewModelHome,
                    contentPadding = contentPadding,
                    uiState = uiState,
                    currentUser = it1,
                    onAddStoryClicked = {
                        navHostController.navigate(Routes.AddStoryScreen.route)
                    },
                    onStoryScreenClicked = viewModelHome::onStoryScreenClicked,
                    onChatScreenClicked = {
                        navHostController.navigate(Routes.ChatScreen.route)
                    },
                    navController = navHostController,
                )
            }
        }

        composable(route = Routes.SearchScreen.route, enterTransition = {
            fadeIn(animationSpec = tween(350))
        }, exitTransition = {
            fadeOut(animationSpec = tween(350))
        }) {
            val uiState by searchViewModel.uiState.collectAsState()
            SearchScreen(
                uiState = uiState, 
                navController = navHostController,
                viewModel = searchViewModel
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
                type = NotificationType.REACT,
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

            currentUser?.let { it1 ->
                NotificationScreen(
                    navHostController = navHostController,
                    currentUserId = it1.userId
                )
            }
        }

        composable(route = Routes.MyProfileScreen.route, enterTransition = {
            fadeIn(animationSpec = tween(350))
        }, exitTransition = {
            fadeOut(animationSpec = tween(350))
        }) {
            val uiState by viewModelProfile.uiState.collectAsState()
            LaunchedEffect(Unit) {
                viewModelProfile.loadUserData()
            }
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
            currentUser?.let { it1 ->
                AddStoryScreen(
                    currentUser = it1,
                    navHostController = navHostController
                )
            }
        }

        composable(
            route = Routes.ChatScreen.route,
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
            currentUser?.let { it1 ->
                ChatScreen(
                    currentUser = it1,
                    navHostController = navHostController,
                    onNewChatClicked = { navHostController.navigate(Routes.NewChatScreen.route) }
                )
            }
        }


        composable(
            route = "${Routes.ChatBoxScreen.route}/{chatId}",
            arguments = listOf(navArgument("chatId") { type = NavType.StringType }),
            enterTransition = {
                slideInHorizontally(animationSpec = tween(), initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = tween(), targetOffsetX = { it })
            }) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable


            currentUser?.let {
                ChatBoxScreen(
                    currentUser = it,
                    chatId = chatId,
                    navHostController = navHostController
                )
            }
        }

        composable(
            route = Routes.NewChatScreen.route,
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
            currentUser?.let { it1 ->
                NewChatScreen(
                    currentUser = it1,
                    navHostController = navHostController,
                )
            }
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

        // Update FollowerScreen route
        composable(
            route = Routes.FollowerScreen.route,
            enterTransition = { fadeIn(animationSpec = tween(350)) },
            exitTransition = { fadeOut(animationSpec = tween(350)) }
        ) {
            FollowScreen(
                navController = navHostController,
                viewModel = viewModelFollow,
                isFollower = true
            )
        }

        // Update FollowingScreen route
        composable(
            route = Routes.FollowingScreen.route,
            enterTransition = { fadeIn(animationSpec = tween(350)) },
            exitTransition = { fadeOut(animationSpec = tween(350)) }
        ) {
            FollowScreen(
                navController = navHostController,
                viewModel = viewModelFollow,
                isFollower = false
            )
        }

        // Add routes for viewing other user's followers/following
        composable(
            route = Routes.UserFollowerScreen.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType }),
            enterTransition = { fadeIn(animationSpec = tween(350)) },
            exitTransition = { fadeOut(animationSpec = tween(350)) }
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            FollowScreen(
                navController = navHostController,
                viewModel = viewModelFollow,
                isFollower = true,
                targetUserId = userId
            )
        }

        composable(
            route = Routes.UserFollowingScreen.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType }),
            enterTransition = { fadeIn(animationSpec = tween(350)) },
            exitTransition = { fadeOut(animationSpec = tween(350)) }
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            FollowScreen(
                navController = navHostController,
                viewModel = viewModelFollow,
                isFollower = false,
                targetUserId = userId
            )
        }

        composable(
            route = Routes.EditProfileScreen.route,
            enterTransition = { fadeIn(animationSpec = tween(350)) },
            exitTransition = { fadeOut(animationSpec = tween(350)) }
        ) {
            LaunchedEffect(Unit) {
                viewModelProfile.loadUserData()
            }
            EditProfileScreen(
                navController = navHostController,
                viewModel = viewModelProfile,
            )
        }

        composable(route = Routes.AddPostScreen.route) {
            AddPostScreen(
                navHostController = navHostController,
                addPostViewModel = viewModelOfAddPost
            )
        }

        composable(route = Routes.AddPostDetailScreen.route) {
            AddPostDetailScreen(
                navHostController = navHostController,
                addPostViewModel = viewModelOfAddPost,
                userViewModel = userViewModel
            )
        }

        composable(
            route = Routes.PostDetailScreen.route,
            arguments = listOf(
                navArgument("postId") { type = NavType.StringType }
            ),
            enterTransition = {
                fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(350))
            }
        ) { navBackStackEntry ->
            val postId = navBackStackEntry.arguments?.getString("postId") ?: ""
            var isLoading by remember { mutableStateOf(true) }
            var post by remember { mutableStateOf<Post?>(null) }
            var errorMessage by remember { mutableStateOf("") }
            
            LaunchedEffect(postId) {
                // First try to get the post from ViewModels
                val postFromProfile = viewModelProfile.getPostById(postId)
                val postFromOtherProfile = viewModelOtherUserProfile.getPostById(postId)
                
                if (postFromProfile != null || postFromOtherProfile != null) {
                    // Post found in ViewModel cache
                    post = postFromProfile ?: postFromOtherProfile
                    isLoading = false
                } else {
                    // Post not found in cache, fetch directly from Firestore
                    FirebaseFirestore.getInstance().collection("posts").document(postId).get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                post = document.toObject(Post::class.java)
                                isLoading = false
                            } else {
                                errorMessage = "Post not found"
                                isLoading = false
                            }
                        }
                        .addOnFailureListener { e ->
                            errorMessage = "Error loading post: ${e.message}"
                            isLoading = false
                            Log.e("InnerNavigation", "Error fetching post", e)
                        }
                }
            }
            
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                post != null -> {
                    // Store both post and currentUser in local variables that can be smart cast
                    val postCopy = post
                    val userCopy = currentUser
                    if (postCopy != null && userCopy != null) {
                        PostDetailScreen(
                            post = postCopy,
                            onBackPressed = { navHostController.popBackStack() },
                            navController = navHostController,
                            currentUser = userCopy,
                            userViewModel = userViewModel,
                            optionsViewModel = optionsViewModel,
                        )
                    } else if (postCopy != null) {
                        // Post exists but user not logged in
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Please log in to view this post",
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { navHostController.popBackStack() }) {
                                    Text("Go Back")
                                }
                            }
                        }
                    }
                }
                else -> {
                    // Error state - no post found
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = errorMessage.ifEmpty { "Post not found" },
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { navHostController.popBackStack() }) {
                                Text("Go Back")
                            }
                        }
                    }
                }
            }
        }

        composable(route = Routes.EditLocationScreen.route) {
            EditLocationScreen(
                viewModel = viewModelProfile,
                navController = navHostController
            )
        }

        composable(
            route = Routes.UserProfileScreen.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType }
            ),
            enterTransition = {
                fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(350))
            }
        ) { navBackStackEntry ->
            val userId = navBackStackEntry.arguments?.getString("userId") ?: ""
            LaunchedEffect(userId) {
                viewModelOtherUserProfile.loadUserData(userId, forceReload = false)
            }
            currentUser?.let { currentUser ->
                UserProfileScreen(
                    navController = navHostController,
                    viewModel = viewModelOtherUserProfile
                )
            }
        }

        composable(
            route = Routes.CheckoutScreen.route,
            enterTransition = {
                fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(350))
            }
        ) {
            CheckoutScreen(
                viewModel = viewModelProfile,
                onBackClick = { navHostController.popBackStack() },
                onCheckoutComplete = {
                    // Update the user's premium status if needed
                    navHostController.popBackStack()
                }
            )
        }

        composable(route = Routes.SelectLocationScreen.route) {
            SelectLocationScreen(
                viewModel = viewModelOfAddPost,
                navController = navHostController
            )
        }
    }
}