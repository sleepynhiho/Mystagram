package com.forrestgump.ig.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navigation
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
import com.forrestgump.ig.data.models.MessageType
import com.forrestgump.ig.data.models.Notification
import com.forrestgump.ig.data.models.NotificationType
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.ui.screens.addPost.AddPostDetailScreen
import com.forrestgump.ig.ui.screens.auth.LoginScreen
import com.forrestgump.ig.ui.screens.auth.SignupScreen
import com.forrestgump.ig.ui.screens.addPost.AddPostScreen
import com.forrestgump.ig.ui.screens.chat.NewChatScreen
import com.forrestgump.ig.ui.screens.addPost.AddPostViewModel
import com.forrestgump.ig.ui.screens.addStory.AddStoryViewModel
import com.forrestgump.ig.ui.screens.profile.EditProfileScreen
import com.forrestgump.ig.ui.screens.profile.FollowScreen
import com.forrestgump.ig.ui.screens.profile.PostDetailScreen
import java.util.Date
import com.forrestgump.ig.ui.screens.settings.SettingsScreen
import com.forrestgump.ig.ui.screens.story.StoryViewModel
import com.forrestgump.ig.ui.viewmodels.UserViewModel


@UnstableApi
@Composable
fun InnerNavigation(
    contentPadding: PaddingValues,
    navHostController: NavHostController,
    viewModelHome: HomeViewModel = hiltViewModel(),
    viewModelProfile: ProfileViewModel,
    userViewModel: UserViewModel,
    storyViewModel: StoryViewModel
) {
    val currentUser by userViewModel.user.collectAsState()
    // Trong Activity hoặc các composable cha
    val viewModelOfAddPost: AddPostViewModel = hiltViewModel()
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
                    })
            }
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

            LaunchedEffect (Unit) {
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

        composable(
            route = Routes.FollowerScreen.route,
            enterTransition = { fadeIn(animationSpec = tween(350)) },
            exitTransition = { fadeOut(animationSpec = tween(350)) }
        ) {
            // Sử dụng remember để tạo danh sách có khả năng thay đổi
            val dummyUsersState = remember {
                mutableStateListOf(
                    User(
                        userId = "1",
                        username = "johnDoe",
                        fullName = "John Doe",
                        email = "john.doe@example.com",
                        profileImage = "https://dtntbinhlong.edu.vn/wp-content/uploads/2024/10/anh-boy-pho-0904i1P.jpg",
                        bio = "Passionate about backend development.",
                        followers = listOf("janeDoe", "samSmith"),
                        following = listOf("aliceWonder")
                    ),
                    User(
                        userId = "2",
                        username = "janeDoe",
                        fullName = "Jane Doe",
                        email = "jane.doe@example.com",
                        profileImage = "https://gockienthuc.edu.vn/wp-content/uploads/2024/07/222-hinh-anh-avatar-ff-dep-chat-ngat-ai-cung-tram-tro_6690e71f7de2e.webp",
                        bio = "Frontend enthusiast and UI/UX lover.",
                        followers = listOf("johnDoe"),
                        following = listOf("samSmith", "aliceWonder")
                    ),
                    User(
                        userId = "3",
                        username = "samSmith",
                        fullName = "Sam Smith",
                        email = "sam.smith@example.com",
                        profileImage = "https://www.vietnamworks.com/hrinsider/wp-content/uploads/2023/12/anh-den-ngau.jpeg",
                        bio = "Loves to work on scalable backend systems.",
                        followers = listOf("johnDoe", "aliceWonder"),
                        following = listOf("janeDoe")
                    ),
                    User(
                        userId = "4",
                        username = "aliceWonder",
                        fullName = "Alice Wonderland",
                        email = "alice.wonder@example.com",
                        profileImage = "https://jbagy.me/wp-content/uploads/2025/03/anh-dai-dien-zalo-dep-1.jpg",
                        bio = "Exploring the world of software engineering.",
                        followers = listOf("bobBuilder", "charlieBrown"),
                        following = listOf("johnDoe", "janeDoe")
                    ),
                    User(
                        userId = "5",
                        username = "bobBuilder",
                        fullName = "Bob Builder",
                        email = "bob.builder@example.com",
                        profileImage = "https://moc247.com/wp-content/uploads/2023/12/loa-mat-voi-101-hinh-anh-avatar-meo-cute-dang-yeu-dep-mat_2.jpg",
                        bio = "Constructing code one brick at a time.",
                        followers = listOf("aliceWonder"),
                        following = listOf("davidKing")
                    )
                )
            }

            FollowScreen(
                navController = navHostController,
                userName = "__td.tung",
                followers = dummyUsersState,
                onBack = { navHostController.navigate(Routes.MyProfileScreen.route) },
                onDeleteFollower = { user ->
                    // Xóa user khỏi danh sách bằng cách sử dụng remove
                    dummyUsersState.remove(user)
                },
                headerText = "Người theo dõi"
            )
        }

        composable(
            route = Routes.FollowingScreen.route,
            enterTransition = { fadeIn(animationSpec = tween(350)) },
            exitTransition = { fadeOut(animationSpec = tween(350)) }
        ) {
            val dummyFollowingState = remember {
                mutableStateListOf(
                    User(
                        userId = "1",
                        username = "nghingoithoi",
                        fullName = "John Doe",
                        email = "john.doe@example.com",
                        profileImage = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQosdzHHSRWcqMHKkg27MixI72P_edEXIDXIg&s",
                        bio = "Passionate about backend development.",
                        followers = listOf("janeDoe", "samSmith"),
                        following = listOf("aliceWonder")
                    ),
                    User(
                        userId = "2",
                        username = "canhocthem",
                        fullName = "Jane Doe",
                        email = "jane.doe@example.com",
                        profileImage = "https://saigonbanme.vn/wp-content/uploads/2024/12/bo-99-anh-avatar-dep-cho-con-gai-ngau-chat-nhat-viet-nam-4.jpg",
                        bio = "Frontend enthusiast and UI/UX lover.",
                        followers = listOf("johnDoe"),
                        following = listOf("samSmith", "aliceWonder")
                    ),
                    User(
                        userId = "3",
                        username = "cungtamduoc",
                        fullName = "Sam Smith",
                        email = "sam.smith@example.com",
                        profileImage = "https://hinhnenpowerpoint.org/wp-content/uploads/2025/01/anh-avatar-capybara-3-2.jpg",
                        bio = "Loves to work on scalable backend systems.",
                        followers = listOf("johnDoe", "aliceWonder"),
                        following = listOf("janeDoe")
                    ),
                    User(
                        userId = "4",
                        username = "Vuaphaithoi",
                        fullName = "Alice Wonderland",
                        email = "alice.wonder@example.com",
                        profileImage = "https://anhdephd.vn/wp-content/uploads/2022/10/hinh-anh-avatar-minion.jpg",
                        bio = "Exploring the world of software engineering.",
                        followers = listOf("bobBuilder", "charlieBrown"),
                        following = listOf("johnDoe", "janeDoe")
                    ),
                    User(
                        userId = "5",
                        username = "takeiteasy",
                        fullName = "Bob Builder",
                        email = "bob.builder@example.com",
                        profileImage = "https://tft.edu.vn/public/upload/2024/12/avatar-qua-bo-cute-01.webp",
                        bio = "Constructing code one brick at a time.",
                        followers = listOf("aliceWonder"),
                        following = listOf("davidKing")
                    )
                )
            }

            FollowScreen(
                navController = navHostController,
                userName = "__td.tung", // Tên người dùng vẫn được truyền
                followers = dummyFollowingState, // Dữ liệu following
                headerText = "Đang theo dõi",     // Nhãn hiển thị khác
                onBack = { navHostController.navigate(Routes.MyProfileScreen.route) },
                onDeleteFollower = { user ->
                    dummyFollowingState.remove(user)
                }
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
                addPostViewModel = viewModelOfAddPost)
        }

        composable(route = Routes.AddPostDetailScreen.route) {
            AddPostDetailScreen(
                navHostController = navHostController,
                addPostViewModel = viewModelOfAddPost
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
            // Lấy post từ ViewModel dựa trên postId
            val post = viewModelProfile.getPostById(postId)

            post?.let {
                currentUser?.let { it1 ->
                    PostDetailScreen(
                        post = it,
                        onBackPressed = { navHostController.popBackStack() },
                        currentUser = it1
                    )
                }
            } ?: run {
                // Hiển thị màn hình lỗi nếu không tìm thấy post
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        "Không tìm thấy bài viết",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

    }
}