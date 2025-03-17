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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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


            currentUser?.let { it1 ->
                HomeScreen(contentPadding = contentPadding,
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
            currentUser?.let { it1 ->
                AddStoryScreen(
                    currentUser = it1,
                    navHostController = navHostController
                )
            }
        }

        val dummyChats = listOf(
            Chat(
                "chat1",
                "user1",
                "user2",
                "Alice",
                "Bob",
                "https://randomuser.me/api/portraits/women/1.jpg",
                "https://randomuser.me/api/portraits/men/1.jpg",
                "Hello!",
                MessageType.TEXT,
                true,
                false,
                Date()
            ),
            Chat(
                "chat2",
                "user3",
                "user4",
                "Charlie",
                "David",
                "https://randomuser.me/api/portraits/men/2.jpg",
                "https://randomuser.me/api/portraits/men/3.jpg",
                "See you soon!",
                MessageType.TEXT,
                false,
                true,
                Date()
            ),
            Chat(
                "chat3",
                "user5",
                "user6",
                "Eve",
                "Frank",
                "https://randomuser.me/api/portraits/women/3.jpg",
                "https://randomuser.me/api/portraits/men/4.jpg",
                "Great photo!",
                MessageType.IMAGE,
                true,
                true,
                Date()
            ),
            Chat(
                "chat4",
                "user7",
                "user8",
                "Grace",
                "Hank",
                "https://randomuser.me/api/portraits/women/4.jpg",
                "https://randomuser.me/api/portraits/men/5.jpg",
                "Good morning!",
                MessageType.TEXT,
                true,
                false,
                Date()
            ),
            Chat(
                "chat5",
                "user9",
                "user10",
                "Ivy",
                "Jack",
                "https://randomuser.me/api/portraits/women/5.jpg",
                "https://randomuser.me/api/portraits/men/6.jpg",
                "How was your trip?",
                MessageType.TEXT,
                false,
                true,
                Date()
            )
        )

        val dummyMessages = listOf(
            Message("msg1", "user1", MessageType.TEXT, "Hello!", null, true, Date()),
            Message(
                "msg2",
                "user2",
                MessageType.IMAGE,
                null,
                "https://picsum.photos/200",
                false,
                Date()
            ),
            Message("msg3", "user1", MessageType.TEXT, "What's up?", null, true, Date()),
            Message("msg4", "user2", MessageType.TEXT, "Not much, you?", null, false, Date()),
            Message(
                "msg5",
                "user1",
                MessageType.IMAGE,
                null,
                "https://picsum.photos/201",
                true,
                Date()
            ),
            Message("msg6", "user2", MessageType.TEXT, "Nice pic!", null, false, Date()),
            Message("msg7", "user1", MessageType.TEXT, "Thanks!", null, true, Date()),
            Message(
                "msg8",
                "user2",
                MessageType.IMAGE,
                null,
                "https://picsum.photos/202",
                false,
                Date()
            ),
            Message("msg9", "user1", MessageType.TEXT, "Where was that?", null, true, Date()),
            Message("msg10", "user2", MessageType.TEXT, "At the beach!", null, false, Date())
        )



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
                    chats = dummyChats,
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

            val chat = dummyChats.find { it.chatId == chatId } ?: return@composable

            currentUser?.let {
                ChatBoxScreen(
                    currentUser = it,
                    chat = chat,
                    messages = dummyMessages,
                    navHostController = navHostController
                )
            }
        }

        val dummyUsers = listOf(
            User(
                userId = "1",
                username = "john_doe",
                fullName = "John Doe",
                email = "john.doe@example.com",
                profileImage = "https://randomuser.me/api/portraits/men/1.jpg",
                bio = "Tech enthusiast. Love coding!",
                followers = listOf("2", "3"),
                following = listOf("4", "5")
            ),
            User(
                userId = "2",
                username = "emma_smith",
                fullName = "Emma Smith",
                email = "emma.smith@example.com",
                profileImage = "https://randomuser.me/api/portraits/women/2.jpg",
                bio = "Traveler & Photographer 📸",
                followers = listOf("1"),
                following = listOf("3", "6")
            ),
            User(
                userId = "3",
                username = "michael_j",
                fullName = "Michael Johnson",
                email = "michael.j@example.com",
                profileImage = "https://randomuser.me/api/portraits/men/3.jpg",
                bio = "AI & ML enthusiast 🤖",
                followers = listOf("1", "2"),
                following = listOf("7", "8")
            ),
            User(
                userId = "4",
                username = "sophia_w",
                fullName = "Sophia Williams",
                email = "sophia.w@example.com",
                profileImage = "https://randomuser.me/api/portraits/women/4.jpg",
                bio = "Writer & Blogger ✍️",
                followers = listOf("5", "6"),
                following = listOf("1", "2")
            ),
            User(
                userId = "5",
                username = "david_b",
                fullName = "David Brown",
                email = "david.b@example.com",
                profileImage = "https://randomuser.me/api/portraits/men/5.jpg",
                bio = "Software Engineer 💻",
                followers = listOf("4"),
                following = listOf("6", "7")
            ),
            User(
                userId = "6",
                username = "olivia_t",
                fullName = "Olivia Taylor",
                email = "olivia.t@example.com",
                profileImage = "https://randomuser.me/api/portraits/women/6.jpg",
                bio = "Music lover & Guitarist 🎸",
                followers = listOf("2", "5"),
                following = listOf("3", "8")
            ),
            User(
                userId = "7",
                username = "james_m",
                fullName = "James Miller",
                email = "james.m@example.com",
                profileImage = "https://randomuser.me/api/portraits/men/7.jpg",
                bio = "Foodie & Chef 🍕",
                followers = listOf("1", "6"),
                following = listOf("4", "9")
            ),
            User(
                userId = "8",
                username = "isabella_c",
                fullName = "Isabella Clark",
                email = "isabella.c@example.com",
                profileImage = "https://randomuser.me/api/portraits/women/8.jpg",
                bio = "Entrepreneur & Investor 💰",
                followers = listOf("3", "7"),
                following = listOf("5", "10")
            ),
            User(
                userId = "9",
                username = "ethan_w",
                fullName = "Ethan White",
                email = "ethan.w@example.com",
                profileImage = "https://randomuser.me/api/portraits/men/9.jpg",
                bio = "Fitness Coach & Trainer 🏋️",
                followers = listOf("2", "5"),
                following = listOf("1", "6")
            ),
            User(
                userId = "10",
                username = "mia_d",
                fullName = "Mia Davis",
                email = "mia.d@example.com",
                profileImage = "https://randomuser.me/api/portraits/women/10.jpg",
                bio = "Fashion Designer & Stylist 👗",
                followers = listOf("4", "7"),
                following = listOf("3", "9")
            )
        )


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
                    users = dummyUsers
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
            EditProfileScreen(
                navController = navHostController,
                // Những giá trị này có thể được thay thế bằng dữ liệu thực từ ViewModel khi tích hợp backend
                initialFullName = "John Doe",
                initialUserName = "john_doe",
                initialBio = "This is my bio",
                isPremium = false,
                onChangePremiumStatus = { /* Xử lý thay đổi gói premium */ },
                onAvatarClick = { /* Mở trình chọn ảnh để thay đổi avatar */ }
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

        composable(route = Routes.AddPostDetailScreen.route, enterTransition = {
            fadeIn(animationSpec = tween(350))
        }, exitTransition = {
            fadeOut(animationSpec = tween(350))
        }) {

            AddPostDetailScreen(
                navHostController = navHostController
            )
        }

    }
}