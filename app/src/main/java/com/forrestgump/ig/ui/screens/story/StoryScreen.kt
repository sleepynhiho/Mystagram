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
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.data.models.UserStory
import com.forrestgump.ig.ui.screens.story.components.UserStoryDetail

@Composable
fun StoryScreen(
    visible: Boolean,
    currentUser: User,
    onDismiss: () -> Unit,
    userStories: () -> List<UserStory>,
    userStoryIndex: Int,
    onUserStoryIndexChanged: (Int) -> Unit
) {
    Log.d("NHII UserStories:", " ${userStories()}")


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

        Log.d("NHII currentUserStory", currentUserStory.toString())

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
