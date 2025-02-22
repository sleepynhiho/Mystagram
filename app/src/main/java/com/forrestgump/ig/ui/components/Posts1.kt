package com.forrestgump.ig.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.R
import com.google.accompanist.pager.ExperimentalPagerApi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Accompanist Pager imports
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PostItem(
    post: Post,
    onLikeClicked: () -> Unit,
    onCommentClicked: () -> Unit,
    onShareClicked: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Header: avatar, username và nút more
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = post.profileImageUrl,
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = post.username,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { /* TODO: xử lý sự kiện more */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.more2),
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Phần media: hiển thị nhiều ảnh nếu có
        // Giả sử Post có thuộc tính mediaUrls: List<String>
        // Nếu không có thì dùng 1 ảnh duy nhất từ mediaUrl
        val mediaUrls = if (post.mediaUrls.isNotEmpty()) post.mediaUrls else listOf(post.mediaUrls)
        if (mediaUrls.size > 1) {
            // Sử dụng HorizontalPager để hiển thị danh sách ảnh
            val pagerState = rememberPagerState()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 250.dp, max = 400.dp) // Giới hạn chiều cao của Box
            ) {
                HorizontalPager(
                    count = mediaUrls.size,
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight() // Chiếm toàn bộ chiều cao của Box đã giới hạn
                ) { page ->
                    AsyncImage(
                        model = mediaUrls[page],
                        contentDescription = "Post Media",
                        contentScale = ContentScale.Crop, // Cắt xén hoặc scale ảnh cho vừa khung
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray)
                    )
                }
                // Hiển thị chỉ số trang, ví dụ "1/3"
                Text(
                    text = "${pagerState.currentPage + 1}/${mediaUrls.size}",
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .background(
                            color = Color.Gray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                )
            }
        } else {
            // Nếu chỉ có 1 ảnh thì hiển thị thông thường
            AsyncImage(
                model = mediaUrls.first(),
                contentDescription = "Post Media",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 250.dp)
                    .background(Color.LightGray)
            )
        }

        // Thanh icon: like, comment
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onLikeClicked) {
                Icon(
                    painter = painterResource(id = R.drawable.heart_outlined),
                    contentDescription = "Like",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(onClick = onCommentClicked) {
                Icon(
                    painter = painterResource(id = R.drawable.comment),
                    contentDescription = "Comment",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            // Bạn có thể mở rộng thêm nút share, bookmark nếu cần
        }

        // Số lượt like
        Text(
            text = "${post.likes.size} lượt thích",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Caption: hiển thị username và nội dung caption
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(post.username)
                }
                append(" ")
                append(post.caption)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )

        // Số bình luận
        if (post.commentsCount > 0) {
            Text(
                text = "Xem tất cả ${post.commentsCount} bình luận",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Thời gian post
        val timeString = post.timestamp?.let { formatDate(it) } ?: ""
        if (timeString.isNotEmpty()) {
            Text(
                text = timeString,
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

private fun formatDate(date: Date): String {
    val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    return sdf.format(date)
}

@UnstableApi
@Preview
@Composable
fun PostItemPreview() {
    // Ví dụ mock data cho preview.
    PostItem(
        post = Post(
            postId = "1",
            userId = "user_123",
            username = "hcmusgang",
            profileImageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQUyAfXfniYfSTZ7Z2HjW2COSyC8WTH3TgkGw&s",
            // Nếu có nhiều ảnh, bạn có thể truyền vào một danh sách URL
            mediaUrls = listOf(
                "https://static.vecteezy.com/system/resources/thumbnails/046/366/986/small_2x/beautiful-white-water-lily-and-pink-water-lily-flowers-on-rock-in-mountain-river-photo.jpg",
                "https://via.placeholder.com/300",
                "https://picsum.photos/300/200"
            ),
            caption = "Hôm nay đi ăn kem nè!",
            likes = listOf("userA", "userB", "userC"),
            commentsCount = 5,
            mimeType = "image/png",
            timestamp = Date()
        ),
        onLikeClicked = {},
        onCommentClicked = {},
        onShareClicked = {}
    )
}