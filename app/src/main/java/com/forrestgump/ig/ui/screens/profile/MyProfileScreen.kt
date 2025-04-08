package com.forrestgump.ig.ui.screens.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.forrestgump.ig.ui.components.Loading
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import coil.compose.rememberAsyncImagePainter
import com.forrestgump.ig.R
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.platform.LocalContext
import com.forrestgump.ig.ui.navigation.Routes
import androidx.navigation.NavController
import com.forrestgump.ig.data.models.Post
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.FilterQuality
import com.forrestgump.ig.ui.components.PostItem


@Composable
fun MyProfileScreen(
    uiState: UiState,
    navController: NavController
) {

    if (!uiState.isLoading) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background // Hoặc Color.Black, tuỳ bạn
        ) {
            Column {
                // Thanh top bar
                ProfileTopBar(
                    title = uiState.curUser.username,
                    onBackClicked = { navController.popBackStack() },
                    onMoreClicked = {
                        navController.navigate(Routes.SettingsScreen.route)
                    }
                )

                // Khu vực hiển thị thông tin chính: Avatar, Thống kê, Tên, Bio,...
                ProfileInfoSection(
                    profileImage =  uiState.curUser.profileImage,
                    fullName = uiState.curUser.fullName,
                    bio = uiState.curUser.bio,
                    posts = uiState.postCount,
                    followers = uiState.curUser.followers.size,
                    following = uiState.curUser.following.size,
                    navController = navController
                )

                // Khu vực nút "Chỉnh sửa" và "Chia sẻ"
                ProfileActionButtons(navController = navController)
                Spacer(modifier = Modifier.height(16.dp))
                // Khu vực Tab (Bài viết, Reels, Thẻ gắn, v.v.)
                ProfileTabRow()

                // Khu vực hiển thị bài viết

                PostFeed(posts = uiState.posts, onPostClick = { post ->
                    // Chuyển đến màn hình chi tiết khi nhấn vào post
                    navController.navigate("PostDetailScreen/${post.postId}")
                })
            }
        }
    } else {
        Loading()
    }
}

/**
 * Phần hiển thị thông tin người dùng: Avatar, thống kê (bài viết, follower, following), tên, bio
 */
@Composable
fun ProfileInfoSection(
    profileImage: String,
    fullName: String,
    bio: String,
    posts: Int,
    followers: Int,
    following: Int,
    navController: NavController // Dùng để chuyển màn hình
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
            val painterImage = if (profileImage.startsWith("http://") || profileImage.startsWith("https://")) {
                rememberAsyncImagePainter(model = profileImage)
            } else {
                val resId = R.drawable.default_profile_image
                painterResource(id = resId)
            }
            // Ảnh đại diện
            AsyncImage(
                model = profileImage,
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .fillMaxSize()
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Thống kê (bài viết, follower, following)
            ProfileStatItem(number = posts, label = "Bài viết")
            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier.clickable {
                    navController.navigate(Routes.FollowerScreen.route)
                }) {
                ProfileStatItem(number = followers, label = "Người theo dõi")
            }
            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier.clickable {
                    navController.navigate(Routes.FollowingScreen.route)
                }) {
                ProfileStatItem(number = following, label = "Đang theo dõi")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tên
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

/**
 * Khu vực các nút: "Chỉnh sửa trang cá nhân", "Chia sẻ trang cá nhân"
 */
@Composable
fun ProfileActionButtons(
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Button(
            onClick = { navController.navigate(Routes.EditProfileScreen.route) },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,    // Màu nền nút là đen
                contentColor = Color.Black      // Màu chữ là đen
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground) // Viền trắng
        ) {
            Text(text = "Chỉnh sửa trang cá nhân")
        }
    }
}


/**
 * Tab hiển thị Bài viết, Reels, Thẻ gắn,... (ở Instagram)
 */
@Composable
fun ProfileTabRow() {
    // Demo một row đơn giản
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // Căn giữa các phần tử theo chiều ngang trong Column
        modifier = Modifier.padding(vertical = 8.dp) // Tùy chỉnh khoảng cách xung quanh nếu cần
    ) {
        Image(
            painter = painterResource(id = R.drawable.grid),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            contentDescription = "Mô tả ảnh",
            modifier = Modifier.size(25.dp) // Tùy chỉnh kích thước hoặc các modifier khác nếu cần
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Dòng gạch dưới màu trắng
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(), // Hoặc bạn có thể sử dụng kích thước cố định phù hợp
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/**
 * Khu vực danh sách bài viết. Ở đây demo khi chưa có bài viết.
 */
@Composable
fun PostFeed(posts: List<Post>, onPostClick: (Post) -> Unit) {
    // Lấy thông tin cấu hình màn hình hiện tại
    val configuration = LocalConfiguration.current
    // Tính 1/3 độ rộng màn hình (screenWidthDp là số nguyên, chuyển thành dp)
    val imageWidth = (configuration.screenWidthDp / 3).dp

    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // 3 cột, mỗi ảnh chiếm 1/3 chiều rộng màn hình
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 8.dp,
            top = 8.dp,
            end = 8.dp,
            bottom = 64.dp // 56.dp cho navigation bar + 8.dp khoảng cách
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(posts) { post ->
            Box(
                modifier = Modifier
                    .width(imageWidth)
                    .height(imageWidth)
                    .clickable { onPostClick(post) } // Xử lý sự kiện click vào ảnh
            ) {
                // Hiển thị ảnh chính
                AsyncImage(
                    model = post.mediaUrls[0],
                    contentDescription = "Ảnh bài viết",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Nếu có nhiều hơn 1 ảnh, hiển thị biểu tượng stack ở góc trên phải
                if (post.mediaUrls.size > 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape)
                            .padding(6.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_multiple), // Biểu tượng album (chồng ảnh)
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
