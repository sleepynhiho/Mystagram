package com.forrestgump.ig.ui.screens.story

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.tooling.preview.Preview
import com.forrestgump.ig.data.models.Story
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.data.models.UserStory
import com.forrestgump.ig.ui.screens.story.components.UserStoryDetail
import java.util.Date

@Composable
fun StoryScreen(
    visible: Boolean,
    currentUser: User,
    onDismiss: () -> Unit,
    userStories: () -> List<UserStory>,
    userStoryIndex: Int,
    onUserStoryIndexChanged: (Int) -> Unit
) {

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(
            animationSpec = tween(),
            transformOrigin = TransformOrigin(0.5f, 0.1f)
        ),
        exit = scaleOut(
            animationSpec = tween(),
            transformOrigin = TransformOrigin(0.5f, 0.1f)
        ) + fadeOut(animationSpec = tween(durationMillis = 600))
    ) {
        var currentStoryIndex by remember { mutableIntStateOf(0) }

        val currentUserStory = userStories().getOrNull(userStoryIndex) ?: return@AnimatedVisibility
        val stories = currentUserStory.stories


        UserStoryDetail(
            currentUser = currentUser,
            userStory = currentUserStory,
            currentStoryIndex = currentStoryIndex,
            modifier = Modifier,
            isStoryActive = true,
            isPaused = false,
            onProgressComplete = {
                if (currentStoryIndex < stories.size - 1) {
                    // Move to next story inside the same UserStory
                    currentStoryIndex++
                } else {
                    // Move to next UserStory
                    if (userStoryIndex < userStories().size - 1) {
                        onUserStoryIndexChanged(userStoryIndex + 1)
                        currentStoryIndex = 0 // Reset story index
                    } else {
                        onDismiss()
                    }
                }
            }
        )

        BackHandler(onBack = onDismiss)
    }
}

@Preview(showBackground = true)
@Composable
fun StoryScreenPreview() {
    val sampleUser = User(
        userId = "123",
        username = "john_doe",
        fullName = "John Doe",
        email = "john@example.com",
        profileImage = "https://randomuser.me/api/portraits/men/1.jpg",
        bio = "Love coding ❤️",
        followers = listOf("user1", "user2"),
        following = listOf("user3", "user4")
    )

    val sampleStories = listOf(
        UserStory(
            userId = "123",
            username = "john_doe",
            profileImage = "https://randomuser.me/api/portraits/men/1.jpg",
            stories = listOf(
                Story(
                    storyId = "1",
                    username = "john_doe",
                    media = "https://source.unsplash.com/random/1080x1920?nature",
                    views = listOf("user1", "user2"),
                    mimeType = "image/jpeg",
                    timestamp = Date(System.currentTimeMillis() - 5 * 60 * 1000) // 5 mins ago
                ),
                Story(
                    storyId = "2",
                    username = "john_doe",
                    media = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4",
                    views = listOf("user3", "user4"),
                    mimeType = "video/mp4",
                    timestamp = Date(System.currentTimeMillis() - 30 * 60 * 1000) // 30 mins ago
                )
            )
        ),
        UserStory(
            userId = "456",
            username = "jane_smith",
            profileImage = "https://randomuser.me/api/portraits/women/2.jpg",
            stories = listOf(
                Story(
                    storyId = "3",
                    username = "jane_smith",
                    media = "https://source.unsplash.com/random/1080x1920?city",
                    views = listOf("user5"),
                    mimeType = "image/jpeg",
                    timestamp = Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000) // 2 hours ago
                ),
                Story(
                    storyId = "4",
                    username = "jane_smith",
                    media = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_5mb.mp4",
                    views = listOf(),
                    mimeType = "video/mp4",
                    timestamp = Date(System.currentTimeMillis() - 6 * 60 * 60 * 1000) // 6 hours ago
                )
            )
        )
    )

    StoryScreen(
        visible = true,
        currentUser = sampleUser,
        onDismiss = {},
        userStories = { sampleStories },
        userStoryIndex = 0,
        onUserStoryIndexChanged = {}
    )
}