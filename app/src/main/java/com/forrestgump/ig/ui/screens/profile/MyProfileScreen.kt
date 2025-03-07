package com.forrestgump.ig.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.forrestgump.ig.ui.components.Loading
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import coil.compose.rememberAsyncImagePainter
import com.forrestgump.ig.R
import com.forrestgump.ig.ui.components.StoryList
import com.forrestgump.ig.data.models.UserStory
import com.forrestgump.ig.data.models.Story
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.platform.LocalContext
import com.forrestgump.ig.ui.navigation.Routes
import androidx.navigation.NavController
import com.forrestgump.ig.data.models.User
import com.google.firebase.auth.FirebaseAuth


@Composable
fun MyProfileScreen(
    uiState: UiState,
    navController: NavController
) {
    // Tạm thời set cứng để demo
    uiState.isLoading = false
    var user = FirebaseAuth.getInstance().currentUser
    if (user == null) return
    var name = user.displayName
    var email = user.email
    var photoUrl = user.photoUrl

    if (!uiState.isLoading) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MainBackground // Hoặc Color.Black, tuỳ bạn
        ) {
            Column {
                // Thanh top bar
                ProfileTopBar(
                    title = "__td.tung",
                    onBackClicked = { /* TODO */ },
                    onMoreClicked = {
                        navController.navigate(Routes.SettingsScreen.route)
                    }
                )

                // Khu vực hiển thị thông tin chính: Avatar, Thống kê, Tên, Bio,...
                ProfileInfoSection(
                    fullName = email.toString(),
                    bio = "Trò chơi quyền lực là sách hay, được viết bởi Ngô Di Lân, nhập môn tìm hiểu thế giới quan về địa chính trị",
                    posts = 0,
                    followers = 47,
                    following = 110
                )

                // (Tuỳ chọn) Khu vực Story Highlights, v.v. (Ở Instagram thường có một hàng story highlight)
                StoryHighlightsSection()

                // Khu vực nút "Chỉnh sửa" và "Chia sẻ"
                ProfileActionButtons()
                Spacer(modifier = Modifier.height(16.dp))
                // Khu vực Tab (Bài viết, Reels, Thẻ gắn, v.v.)
                ProfileTabRow()

                // Khu vực hiển thị bài viết
                // Ở đây demo khi chưa có bài viết

                val posts = listOf(
                    "https://storage.googleapis.com/blogvxr-uploads/2020/10/A%CC%89nh-de%CC%A3p-Vie%CC%A3%CC%82t-Nam.jpg",
                    "https://img.tripi.vn/cdn-cgi/image/width=700,height=700/https://gcs.tripi.vn/public-tripi/tripi-feed/img/474068sWa/anh-dep-cau-rong-da-nang-viet-nam_055418962.jpg",
                    "https://media.istockphoto.com/id/1401126607/vi/anh/khung-c%E1%BA%A3nh-tr%C3%AAn-kh%C3%B4ng-c%E1%BB%A7a-nh%E1%BB%AFng-t%C3%B2a-nh%C3%A0-ch%E1%BB%8Dc-tr%E1%BB%9Di-tuy%E1%BB%87t-%C4%91%E1%BA%B9p-d%E1%BB%8Dc-theo-d%C3%B2ng-s%C3%B4ng-tr%C3%AAn-b%E1%BA%A7u-tr%E1%BB%9Di.jpg?s=612x612&w=0&k=20&c=tKG0XCBB-k7AuUUNvH6VrCW7DjSojIGmrxJD6rcPabE=",
                    "https://wyndham-thanhthuy.com/wp-content/uploads/2024/01/dia-diem-chup-anh-dep-o-ha-noi-1.jpg",
                    "https://icdn.24h.com.vn/upload/4-2019/images/2019-12-17/1576580871-929bdf4f16b86295376a79e7a8a0b7fe.jpg",
                    "https://teky.edu.vn/blog/wp-content/uploads/2022/03/Hinh-nen-may-tinh-dep-chu-de-phong-canh.jpg",
                    "https://mia.vn/media/uploads/blog-du-lich/canh-dep-viet-nam-4-1710380274.jpg",
                    "https://vinhomegoldenriver.com/wp-content/uploads/2023/03/Vinhomes-Golden-River-Quan-1.jpg"
                )
                val context = LocalContext.current
                PostFeed(posts = posts, onPostClick = { postUrl ->
                    // Hiển thị Toast khi nhấn vào ảnh
                    Toast.makeText(context, "Clicked on post: $postUrl", Toast.LENGTH_SHORT).show()
                })
            }
        }
    } else {
        Loading()
    }
}

/**
 * Thanh top bar đơn giản, hiển thị username + icon back, icon menu
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(
    title: String,
    onBackClicked: () -> Unit,
    onMoreClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = title, color = Color.White)
        },
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = onMoreClicked) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black
        )
    )
}

/**
 * Phần hiển thị thông tin người dùng: Avatar, thống kê (bài viết, follower, following), tên, bio
 */
@Composable
fun ProfileInfoSection(
    fullName: String,
    bio: String,
    posts: Int,
    followers: Int,
    following: Int
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
            // Ảnh đại diện
            Image(
                painter = rememberAsyncImagePainter("https://ci3.googleusercontent.com/mail-sig/AIorK4zn5K76pJRVaezbkBYcJUCKTfDnFxB9SIyhdYuSW5UtQ4PVBfYHktxkIvuUlD2VossPsbTNEpbzKa7E"),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Thống kê (bài viết, follower, following)
            ProfileStatItem(number = posts, label = "Bài viết")
            Spacer(modifier = Modifier.width(16.dp))
            ProfileStatItem(number = followers, label = "Người theo dõi")
            Spacer(modifier = Modifier.width(16.dp))
            ProfileStatItem(number = following, label = "Đang theo dõi")
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
fun ProfileActionButtons() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Button(
            onClick = { /* TODO */ },
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
 * Khu vực Story Highlights (có thể là danh sách vòng tròn story)
 */
@Composable
fun StoryHighlightsSection() {
    // Demo đơn giản hiển thị một hàng text
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        StoryList(
            onAddStoryClicked = { /* Xử lý sự kiện khi nhấn vào "Add Story" */ },
            userStories = listOf(
                UserStory(
                    // Giả sử mỗi UserStory có thêm thuộc tính username nếu cần dùng làm key
                    stories = listOf(
                        Story(username = "story1"),
                        Story(username = "story2"),
                        Story(username = "story3")
                    )
                    // Nếu có thuộc tính username cho UserStory, bạn có thể truyền ở đây:
                    // username = "user1"
                )
            ),
            myStories = listOf(
                UserStory(
                    stories = listOf(
                        Story(username = "mystory1"),
                        Story(username = "mystory2")
                    )
                    // username = "usernameCuaToi"
                )
            ),
            currentUser = User(),
            onViewStoryClicked = {_, _ -> },
        )
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

@Composable
fun ProfileTabItem(label: String) {
    ClickableText(
        text = AnnotatedString(label),
        onClick = { /* TODO */ },
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 16.dp),
        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
    )
}

/**
 * Khu vực danh sách bài viết. Ở đây demo khi chưa có bài viết.
 */
@Composable
fun PostFeed(posts: List<String>, onPostClick: (String) -> Unit) {
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
            AsyncImage(
                model = post,
                contentDescription = "Ảnh bài viết",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(imageWidth)
                    .height(imageWidth)
                    .clickable { onPostClick(post) } // Xử lý sự kiện click vào ảnh
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun MyProfileScreenPreview() {
    MyProfileScreen(
        uiState = UiState(isLoading = false),
        navController = TODO()
    )
}