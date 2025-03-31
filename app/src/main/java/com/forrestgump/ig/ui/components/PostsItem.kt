package com.forrestgump.ig.ui.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.R
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.google.accompanist.pager.ExperimentalPagerApi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun PostItem(
    post: Post,
    onLikeClicked: () -> Unit,
    onCommentClicked: () -> Unit,
) {
    Log.d("PostItem", "Rendering post: ${post.postId}")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MainBackground)
    ) {
        PostHeader(post)
        PostMedia(post)
        PostActions(post, onCommentClicked)
        PostDetails(post)
    }
}

@Composable
fun LottieAnimationView(assetName: String) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(assetName))

    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier.size(30.dp)
    )
}

@Composable
fun PostHeader(post: Post) {
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
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PostMedia(post: Post) {
    // Phần media: hiển thị nhiều ảnh nếu có
    // Giả sử Post có thuộc tính mediaUrls: List<String>
    // Nếu không có thì dùng 1 ảnh duy nhất từ mediaUrl
    val mediaUrls = if (post.mediaUrls.isNotEmpty()) post.mediaUrls else listOf(post.mediaUrls)
    var showFullScreenImage by remember { mutableStateOf(false) }
    var currentImageIndex by remember { mutableStateOf(0) }

    if (mediaUrls.size > 1) {
        // Sử dụng HorizontalPager để hiển thị danh sách ảnh
        val pagerState = rememberPagerState()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 250.dp, max = 400.dp)
        ) {
            HorizontalPager(
                count = mediaUrls.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) { page ->
                AsyncImage(
                    model = mediaUrls[page],
                    contentDescription = "Post Media",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            currentImageIndex = page
                            showFullScreenImage = true
                        }
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
        // Nếu chỉ có 1 ảnh, hiển thị ảnh trong container cố định và click để xem full
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp) // chiều cao cố định cho Post
                .clickable {
                    currentImageIndex = 0
                    showFullScreenImage = true
                }
        ) {
            AsyncImage(
                model = mediaUrls.first(),
                contentDescription = "Post Media",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
            )
        }
    }
    // Dialog hiển thị ảnh full screen
    if (showFullScreenImage) {
        FullScreenImageDialog(
            imageUrl = mediaUrls[currentImageIndex].toString(),
            onDismissRequest = { showFullScreenImage = false }
        )
    }
}

val reactions = mapOf(
    "love" to "love.json",
    "haha" to "haha.json",
    "wow" to "wow.json",
    "hiding_eyes" to "hiding_eyes.json",
    "send_flower" to "send_flower.json",
    "sad" to "sad.json",
    "angry" to "angry.json"
)

val reactionDrawables = mapOf(
    "love" to R.drawable.love,
    "haha" to R.drawable.haha,
    "wow" to R.drawable.wow,
    "hiding_eyes" to R.drawable.hiding_eyes,
    "send_flower" to R.drawable.rose,
    "sad" to R.drawable.sad,
    "angry" to R.drawable.angry
)

@Composable
fun PostActions(post: Post, onCommentClicked: () -> Unit) {
    var selectedReaction by remember { mutableStateOf<String?>(null) }
    var showReactions by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var job by remember { mutableStateOf<Job?>(null) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .clickable { showReactions = false }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                job?.cancel() // Hủy coroutine cũ (nếu có)
                                job = coroutineScope.launch {
                                    delay(300)
                                    showReactions = true
                                }
                                tryAwaitRelease()
                                job?.cancel() // Hủy job nếu thả tay trước 300ms
                            },
                            onTap = {
                                selectedReaction = if (selectedReaction == null) {
                                    "love"
                                } else {
                                    null
                                }
                                showReactions = false
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (selectedReaction == null) {
                    Icon(
                        painter = painterResource(id = R.drawable.heart_outlined),
                        contentDescription = "love",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = reactionDrawables[selectedReaction]!!),
                        contentDescription = "react",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }


            IconButton(onClick = onCommentClicked) {
                Icon(
                    painter = painterResource(id = R.drawable.comment),
                    contentDescription = "Comment",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Lựa chọn reactions
        Column {
            AnimatedVisibility(
                visible = showReactions,
                enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f),
                exit = fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.8f)
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .shadow(2.dp, shape = RoundedCornerShape(20.dp))
                        .background(MainBackground, shape = RoundedCornerShape(20.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    reactions.forEach { (key, asset) ->
                        Box(
                            modifier = Modifier
                                .size(35.dp)
                                .clickable {
                                    selectedReaction = key
                                    showReactions = false
                                }
                        ) {
                            LottieAnimationView(assetName = asset)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostDetails(post: Post) {
    // Số reactions
    val sortedReactions = post.reactions.entries.sortedBy { it.value }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 40.dp)
    ) {
        Box {
            sortedReactions.take(3).forEachIndexed { index, (reaction, _) ->
                reactionDrawables[reaction]?.let { drawableId ->
                    Image(
                        painter = painterResource(id = drawableId),
                        contentDescription = reaction,
                        modifier = Modifier
                            .size(20.dp)
                            .zIndex(index.toFloat())
                            .offset(x = (-15 * index).dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Hiển thị tổng số reactions
        Text(
            text = "${post.reactions.values.sum()} reactions",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }


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


private fun formatDate(date: Date): String {
    val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    return sdf.format(date)
}


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
            commentsCount = 5,
            mimeType = "image/png",
            timestamp = Date(),
            reactions = mapOf(
                "love" to 43800,
                "sad" to 8000,
                "angry" to 9000
            ),
        ),
        onLikeClicked = {},
        onCommentClicked = {},
    )

}

