package com.forrestgump.ig.ui.screens.userprofile

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.forrestgump.ig.R
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.ui.components.Loading
import com.forrestgump.ig.utils.constants.Utils.MainBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userId: String,
    navController: NavController,
    viewModel: UserProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadUserData(userId)
    }

    if (uiState.isLoading) {
        Loading()
    } else {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MainBackground
        ) {
            Column {
                // Top bar
                TopAppBar(
                    title = { Text(text = uiState.user.username, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black
                    )
                )

                // Profile info section
                UserProfileInfoSection(
                    profileImage = uiState.user.profileImage,
                    fullName = uiState.user.fullName,
                    bio = uiState.user.bio,
                    posts = uiState.posts.size,
                    followers = uiState.user.followers.size,
                    following = uiState.user.following.size,
                    isPrivate = uiState.user.isPrivate,
                    isFollowing = uiState.isCurrentUserFollowingThisUser,
                    navController = navController,
                    canViewFollowers = !uiState.user.isPrivate || uiState.isCurrentUserFollowingThisUser
                )

                // Action buttons
                UserProfileActionButtons(
                    isFollowing = uiState.isCurrentUserFollowingThisUser,
                    onFollowClick = { viewModel.followUser() },
                    onUnfollowClick = { viewModel.unfollowUser() }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tab row (posts grid)
                ProfileTabRow()

                // Posts section
                if (uiState.user.isPrivate && !uiState.isCurrentUserFollowingThisUser) {
                    // Private account that we don't follow
                    PrivateAccountMessage()
                } else {
                    // We can see posts
                    PostFeed(posts = uiState.posts, onPostClick = { post ->
                        Log.d("UserProfileScreen", "${post.postId}")
                        navController.navigate("PostDetailScreen/${post.postId}")
                    })
                }
            }
        }
    }
}

@Composable
fun UserProfileInfoSection(
    profileImage: String,
    fullName: String,
    bio: String,
    posts: Int,
    followers: Int,
    following: Int,
    isPrivate: Boolean,
    isFollowing: Boolean,
    navController: NavController,
    canViewFollowers: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile image
            val painterImage = if (profileImage.startsWith("http://") || profileImage.startsWith("https://")) {
                rememberAsyncImagePainter(model = profileImage)
            } else {
                painterResource(id = R.drawable.default_profile_img)
            }

            Image(
                painter = painterImage,
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .fillMaxSize()
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Stats
            ProfileStatItem(number = posts, label = "Bài viết")
            Spacer(modifier = Modifier.width(16.dp))

            if (canViewFollowers) {
                Box(
                    modifier = Modifier.clickable {
                        navController.navigate("FollowerScreen")
                    }
                ) {
                    ProfileStatItem(number = followers, label = "Người theo dõi")
                }
                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier.clickable {
                        navController.navigate("FollowingScreen")
                    }
                ) {
                    ProfileStatItem(number = following, label = "Đang theo dõi")
                }
            } else {
                ProfileStatItem(number = followers, label = "Người theo dõi")
                Spacer(modifier = Modifier.width(16.dp))
                ProfileStatItem(number = following, label = "Đang theo dõi")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Full name
        Text(
            text = fullName,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        // Bio
        Text(
            text = bio,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium
        )

        if (isPrivate) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.close_story),
                    contentDescription = "Private Account",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Tài khoản riêng tư",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun UserProfileActionButtons(
    isFollowing: Boolean,
    onFollowClick: () -> Unit,
    onUnfollowClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = {
                if (isFollowing) onUnfollowClick() else onFollowClick()
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFollowing) Color.White else MaterialTheme.colorScheme.primary,
                contentColor = if (isFollowing) Color.Black else Color.White
            ),
            border = if (isFollowing) BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground) else null
        ) {
            Text(text = if (isFollowing) "Đang theo dõi" else "Theo dõi")
        }

        Button(
            onClick = { /* Add navigation to chat screen */ },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
        ) {
            Text(text = "Nhắn tin")
        }
    }
}

@Composable
fun PrivateAccountMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.close_story),
                contentDescription = "Private Account",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(48.dp)
            )

            Text(
                text = "Đây là tài khoản riêng tư",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Hãy theo dõi tài khoản này để xem các bài viết của họ",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProfileStatItem(number: Int, label: String) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = number.toString(),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProfileTabRow() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.grid),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            contentDescription = "Grid View",
            modifier = Modifier.size(25.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun PostFeed(posts: List<Post>, onPostClick: (Post) -> Unit) {
    // Get screen configuration
    val configuration = LocalConfiguration.current
    val imageWidth = (configuration.screenWidthDp / 3).dp

    if (posts.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Chưa có bài viết nào",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 8.dp,
                top = 8.dp,
                end = 8.dp,
                bottom = 64.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(posts) { post ->
                Box(
                    modifier = Modifier
                        .width(imageWidth)
                        .height(imageWidth)
                        .clickable { onPostClick(post) }
                ) {
                    AsyncImage(
                        model = post.mediaUrls.firstOrNull() ?: "",
                        contentDescription = "Post Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (post.mediaUrls.size > 1) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape)
                                .padding(6.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_multiple),
                                contentDescription = "Multiple media",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}