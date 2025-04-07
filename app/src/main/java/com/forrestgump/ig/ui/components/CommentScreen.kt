package com.forrestgump.ig.ui.components

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.forrestgump.ig.R
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.forrestgump.ig.data.models.Post

@Composable
fun CommentScreen(
    post: Post,
    currentUser: User,
    showCommentScreen: Boolean,
    onDismiss: () -> Unit,
    viewModel: PostViewModel = hiltViewModel()
) {
    var commentText by remember { mutableStateOf("") }

    AnimatedVisibility(
        visible = showCommentScreen,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 800)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 800)
        )
    )
    {
        var offsetY by remember { mutableFloatStateOf(0f) }

        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                contentAlignment = Alignment.BottomCenter
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    tonalElevation = 8.dp,
                    color = MainBackground,
                    modifier = Modifier
                        .fillMaxSize()
                        .offset { IntOffset(x = 0, y = offsetY.toInt()) }
                        .pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onVerticalDrag = { _, dragAmount ->
                                    offsetY += dragAmount
                                },
                                onDragEnd = {
                                    if (offsetY > 100) {
                                        onDismiss()
                                    } else {
                                        offsetY = 0f
                                    }
                                }
                            )
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MainBackground)
                    ) {
                        CommentTopBar()

                        CommentList(
                            innerPadding = PaddingValues(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = 8.dp),
                            viewModel = viewModel,
                            postId = post.postId,
                        )

                        CommentBottomBar(
                            currentUser = currentUser,
                            commentText = commentText,
                            onCommentChange = { commentText = it },
                            onSendCommentClicked = {
                                viewModel.sendComment(
                                    post = post,
                                    commentText = commentText,
                                    currentUser = currentUser
                                )
                                commentText = ""
                            }
                        )
                    }
                }
            }
        }
    }
}


@OptIn(UnstableApi::class)
@Composable
fun CommentBottomBar(
    currentUser: User,
    commentText: String,
    onCommentChange: (String) -> Unit,
    onSendCommentClicked: () -> Unit
) {
    val isSendButtonEnabled = commentText.trim().isNotEmpty()

    Log.d("NHII", "issendbutton $isSendButtonEnabled")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MainBackground)
            .padding(vertical = 20.dp)
            .padding(bottom = 10.dp)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = strokeWidth
                )
            }
            .heightIn(min = 56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        // User profile image
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            AsyncImage(
                model = currentUser.profileImage,
                contentDescription = currentUser.username,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = MainBackground,
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            BasicTextField(
                value = commentText,
                onValueChange = onCommentChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = { if (isSendButtonEnabled) onSendCommentClicked() }
                ),
                decorationBox = { innerTextField ->
                    if (commentText.isEmpty()) {
                        Text(
                            "Add a comment...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            )
        }

        // Send button
        IconButton(
            onClick = { if (isSendButtonEnabled) onSendCommentClicked() },
            enabled = isSendButtonEnabled // Disable button if not valid
        ) {
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(32.dp)
                    .background(
                        color = Color(0xff0195f5),
                        shape = RoundedCornerShape(15.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.send_comment),
                    contentDescription = "Send comment",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))
    }
}



@Composable
fun CommentTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(MainBackground)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .padding(15.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .height(2.dp)
                    .background(color = Color.Gray)
            )

            Text(
                text = stringResource(R.string.comment_header),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun CommentList(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: PostViewModel,
    postId: String,
) {
    val comments by viewModel.comments.collectAsState()

    LaunchedEffect(postId) {
        viewModel.loadComments(postId)
    }

    Log.d("NHII", "$comments")

    LazyColumn(
        contentPadding = innerPadding,
        modifier = modifier
            .fillMaxWidth()
            .background(MainBackground)
    ) {
        items(comments) { comment ->
            CommentItem(comment = comment)
        }
    }
}



@Preview(showBackground = true)
@Composable
fun CommentScreenPreview() {
    val currentUser = User(
        username = "john_doe",
        profileImage = "https://randomuser.me/api/portraits/women/1.jpg"
    )
    CommentScreen(
        post = Post(),
        currentUser = currentUser,
        showCommentScreen = true,
        onDismiss = {}
    )
}
