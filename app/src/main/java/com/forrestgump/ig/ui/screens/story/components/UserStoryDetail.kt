package com.forrestgump.ig.ui.screens.story.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.forrestgump.ig.R
import com.forrestgump.ig.utils.constants.formatAsElapsedTime
import com.forrestgump.ig.data.models.Story
import com.forrestgump.ig.data.models.UserStory
import java.util.Date


@Composable
fun UserStoryDetail(
    currentUsername: String,
    currentStoryIndex: Int,
    userStory: UserStory,
    modifier: Modifier = Modifier,
    isStoryActive: Boolean,
    isPaused: Boolean,
    onProgressComplete: () -> Unit

) {
    val alphaOnPress by animateFloatAsState(
        targetValue = if (isPaused) 0f else 1f,
        animationSpec = tween(durationMillis = 600),
        label = "alphaOnPress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier), // Thêm modifier từ bên ngoài
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Blue
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
//                    // fix: thêm nếu có ảnh thì...
//                    AsyncImage(
//                        model = ImageRequest.Builder(LocalContext.current)
//                            .data(userStory.stories[currentStoryIndex].image)
//                            .crossfade(true)
//                            .allowHardware(false)
//                            .listener(
//                                onStart = { isImageLoaded.value = false },
//                                onSuccess = { _, _ -> isImageLoaded.value = true }
//                            )
//                            .build(),
//                        contentScale = ContentScale.Fit,
//                        contentDescription = stringResource(id = R.string.user_story, userStory.username),
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .alpha(if (isImageLoaded.value) 1f else 0f) // Ẩn ảnh nếu chưa tải xong
//                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = "https://cdn.mos.cms.futurecdn.net/wtqqnkYDYi2ifsWZVW2MT4.jpg",
                            contentScale = ContentScale.Crop,
                            contentDescription = stringResource(id = R.string.profile_image)
                        )
                    }


                    // Top bar
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(alphaOnPress), // Hide top and bottom bar to view full content when long press
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        // StoryProgressTrack
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(15.dp)
                                .padding(horizontal = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            userStory.stories.forEach { story ->
                                StoryProgressTrack(
                                    modifier = Modifier.weight(1f),
                                    isStoryActive = isStoryActive && currentStoryIndex == userStory.stories.indexOf(
                                        story
                                    ),
                                    isPaused = isPaused,
                                    onProgressComplete = onProgressComplete
                                )
                            }
                        }

                        StoryHeader(
                            currentUsername = currentUsername,
                            userStory = userStory,
                            currentStoryIndex = currentStoryIndex,
                            onProgressComplete = onProgressComplete
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StoryHeader(
    currentUsername: String,
    userStory: UserStory,
    currentStoryIndex: Int,
    modifier: Modifier = Modifier,
    onProgressComplete: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .height(43.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        )
        {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape
            ) {
                // Author's profile image
                AsyncImage(
                    model = userStory.profileImage,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    contentDescription = userStory.username
                )
            }


            Spacer(modifier = Modifier.width(10.dp))

            // Author's username
            Text(
                text = if (userStory.username == currentUsername) stringResource(id = R.string.your_story) else userStory.username,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Story's timestamp
            userStory.stories[currentStoryIndex].timestamp?.let {
                Text(
                    text = it.formatAsElapsedTime(),
                    style = TextStyle(
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Normal
                    ),
                )
            }
        }

        // Close story button
        Icon(
            painter = painterResource(R.drawable.close_story),
            tint = Color.White,
            contentDescription = stringResource(R.string.close_story),
            modifier = Modifier.clickable {
                onProgressComplete()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UserStoryDetailPreview() {
    UserStoryDetail(
        userStory = UserStory(
            username = "johndoe",
            profileImage = R.drawable.default_profile_img.toString(),
            stories = listOf(
                Story(
                    timestamp = Date(),
                    media = R.drawable.default_profile_img.toString(),
                    mimeType = "image/jpg",
                ),
                Story(
                    timestamp = Date()
                )
            )
        ),
        currentUsername = "1",
        currentStoryIndex = 0,
        isPaused = false,
        modifier = Modifier,
        isStoryActive = true,
        onProgressComplete = { }
    )
}