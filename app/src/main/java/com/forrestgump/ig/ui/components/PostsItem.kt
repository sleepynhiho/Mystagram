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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.R
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.ui.screens.addPost.AddPostViewModel
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
import com.google.firebase.auth.FirebaseAuth
import com.forrestgump.ig.ui.navigation.Routes


@Composable
fun PostItem(
    post: Post,
    onCommentClicked: () -> Unit,
    navController: NavController?,
    currentUser: User,
    addPostViewModel: AddPostViewModel = hiltViewModel()
) {
    Log.d("PostItem", "Rendering post: ${post.postId}")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MainBackground)
    ) {
        PostHeader(post, navController)
        PostMedia(post)
        PostActions(post, onCommentClicked, addPostViewModel, currentUser)
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
fun PostHeader(post: Post, navController: NavController? = null) {
    // Get the current user ID
    val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val painterImage = if (post.profileImageUrl.startsWith("http://") || post.profileImageUrl.startsWith("https://")) {
            rememberAsyncImagePainter(model = post.profileImageUrl)
        } else {
            val resId = R.drawable.default_profile_img
            painterResource(id = resId)
        }
//        // Ảnh đại diện
//        Image(
//            painter = painterImage,
//            contentDescription = "Profile Image",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .size(80.dp)
//                .fillMaxSize()
//                .clip(CircleShape)
//        )
        Image(
            painter = painterImage,
            contentDescription = "User Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .fillMaxSize()
                .clip(CircleShape)
                .clickable {
                    // Check if the post belongs to the current user
                    if (post.userId == currentUserID) {
                        // Navigate to MyProfileScreen for the current user
                        navController?.navigate(Routes.MyProfileScreen.route)
                    } else {
                        // Navigate to UserProfileScreen for other users
                        navController?.navigate("UserProfileScreen/${post.userId}")
                    }
                }
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
    val mediaUrls = post.mediaUrls.ifEmpty { listOf(post.mediaUrls) }
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
fun PostActions(
    post: Post,
    onCommentClicked: () -> Unit,
    addPostViewModel: AddPostViewModel,
    currentUser: User,
) {
    key(post.postId) {
        var showReactions by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        var job by remember { mutableStateOf<Job?>(null) }

        var selectedReaction by remember {
            mutableStateOf(post.reactions.entries.find { it.value.contains(currentUser.userId) }?.key)
        }


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
                                    job?.cancel()
                                    job = coroutineScope.launch {
                                        delay(300)
                                        showReactions = true
                                    }
                                    tryAwaitRelease()
                                    job?.cancel() // Hủy job nếu thả tay trước 300ms
                                },
                                onTap = {
                                    Log.d("NHII", "onTap")
                                    selectedReaction?.let { it1 -> Log.d("NHII", it1) }
                                    val newReaction = if (selectedReaction == null) "love" else null
                                    post.let { it1 ->
                                        addPostViewModel.updateReaction(
                                            it1,
                                            currentUser,
                                            selectedReaction,
                                            newReaction
                                        )
                                    }
                                    selectedReaction = newReaction
                                    showReactions = false
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    selectedReaction?.let { Log.d("NHII reaction", it) }
                    Log.d("NHII POST: ", post.caption)
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

                Spacer(modifier = Modifier.width(5.dp))

                if (post.reactions.entries.isNotEmpty()) {
                    Text(
                        text = post.reactions.entries.size.toString(),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }


                IconButton(onClick = onCommentClicked) {
                    Icon(
                        painter = painterResource(id = R.drawable.comment),
                        contentDescription = "Comment",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(2.dp))

                if ((post.commentsCount) > 0) {
                    Text(
                        text = post.commentsCount.toString(),
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
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
                                        val newReaction = if (selectedReaction == key) null else key
                                        post.let {
                                            addPostViewModel.updateReaction(
                                                post,
                                                currentUser,
                                                selectedReaction,
                                                newReaction
                                            )
                                        }
                                        selectedReaction = newReaction
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
}

@Composable
fun PostDetails(post: Post) {
    // Số reactions
    if (post.reactions.isNotEmpty()) {
        val reactionCounts = post.reactions.mapValues { it.value.size }

        val sortedReactions = reactionCounts.entries.sortedByDescending { it.value }

        val paddingStart = when (sortedReactions.size) {
            1 -> 15.dp  // 1 reaction, padding 10dp
            2 -> 30.dp  // 2 reactions, padding 20dp
            3 -> 40.dp  // 3 reactions, padding 30dp
            else -> 40.dp  // Nhiều hơn 3 reactions, padding 40dp
        }


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(start = paddingStart)
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
            val totalReactions = reactionCounts.values.sum()
            Text(
                text = "$totalReactions",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
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
                "love" to List(43800) { "user_$it" }, // 43800 users reacted with "love"
                "sad" to List(8000) { "user_$it" },   // 8000 users reacted with "sad"
                "angry" to List(9000) { "user_$it" }  // 9000 users reacted with "angry"
            ),
        ),
        onCommentClicked = {},
        currentUser = User(),
        navController = rememberNavController(),
    )
}


