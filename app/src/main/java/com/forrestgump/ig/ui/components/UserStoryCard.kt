package com.forrestgump.ig.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.forrestgump.ig.R
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.forrestgump.ig.data.models.Story
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.data.models.UserStory
import com.forrestgump.ig.utils.constants.Utils.LightBlue

@Composable
fun UserStoryCard(
    userStory: UserStory,
    currentUser: User,
    onViewStoryClicked: () -> Unit,
    onAddStoryClicked: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(200),
        label = "storyCardAnimation"
    )

    val isAllStoryViewed = userStory.stories.last().views.contains(currentUser.username)
    Box(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .height(100.dp)
            .width(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.weight(1f),
            ) {
                Surface(
                    shape = CircleShape,
                    modifier = Modifier
                        .scale(animatedScale)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    scale = 0.9f
                                    awaitRelease()
                                    onViewStoryClicked()
                                    scale = 1f
                                },
                                onLongPress = { scale = 0.9f }
                            )
                        }
                        .size(80.dp)
                        .border(
                            brush = Brush.linearGradient(
                                start = Offset(x = 0.0f, y = 50.0f),
                                end = Offset(x = 200.0f, y = 250.0f),
                                colors = if (isAllStoryViewed) {
                                    listOf(
                                        Color.DarkGray,
                                        Color.DarkGray
                                    )
                                } else {
                                    listOf(
                                        Color(0xFFDC00B4),
                                        Color(0xFFEf018A),
                                        Color(0xFFFF4B29),
                                        Color(0xFFffa400),
                                        Color(0xFFff8b00),
                                        Color(0xFFff1d50),
                                        Color(0xFFd300c5)
                                    )
                                }
                            ),
                            shape = CircleShape,
                            width = if (isAllStoryViewed) 1.dp else 2.dp
                        )
                        .border(width = 5.dp, color = MaterialTheme.colorScheme.background, shape = CircleShape),
                    color = Color.LightGray
                ) {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = userStory.profileImage,
                        contentScale = ContentScale.Crop,
                        filterQuality = FilterQuality.None,
                        contentDescription = stringResource(
                            R.string.story_placeholder,
                            userStory.username
                        )
                    )
                }
                if (currentUser.userId == userStory.userId) {
                    Surface(
                        modifier = Modifier
                            .size(28.dp)
                            .offset(x = (-2).dp)
                            .align(Alignment.BottomEnd)
                            .clickable { onAddStoryClicked() },
                        shape = CircleShape,
                        color = LightBlue,
                        border = BorderStroke(width = 2.dp, color = Color.White),

                        ) {
                        Icon(
                            modifier = Modifier.padding(5.dp),
                            imageVector = Icons.Filled.Add,
                            tint = Color.White,
                            contentDescription = stringResource(R.string.add_story)
                        )
                    }
                }
            }

            Text(
                modifier = Modifier.padding(vertical = 2.dp),
                text = if (userStory.username != currentUser.username) userStory.username
                else stringResource(id = R.string.your_story),
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    }
}

@Preview
@Composable
fun UserStoryCardPreview() {
    UserStoryCard(
        userStory = UserStory(
            stories = listOf(
                Story(
                    username = "abc",
                    views = listOf("1")
                )
            )
        ),
        currentUser = TODO(),
        onViewStoryClicked = TODO(),
        onAddStoryClicked = TODO()
    )
}