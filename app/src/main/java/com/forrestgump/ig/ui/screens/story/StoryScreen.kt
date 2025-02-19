package com.forrestgump.ig.ui.screens.story

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.tooling.preview.Preview
import com.forrestgump.ig.ui.screens.story.components.UserStoryDetail
import com.forrestgump.ig.data.models.Story
import com.forrestgump.ig.data.models.UserStory

enum class Stories {
    MY_STORY,
    USER_STORY
}

@Composable
fun StoryScreen(
    visible: Boolean,
    userStories: () -> List<UserStory>,
    currentUsername: String,
    onDismiss: () -> Unit,
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

        UserStoryDetail(
            currentUsername = currentUsername,
            currentStoryIndex = 0,
            userStory = userStories()[0],
            modifier = Modifier,
            isStoryActive = true,
            isPaused = false,
            onProgressComplete = {
                onDismiss()
            }
        )

        BackHandler(onBack = onDismiss)
    }
}


@Preview(
    showBackground = true,
    backgroundColor = 0XFF000000
)
@Composable
private fun StoryScreenPreview() {
    StoryScreen(
        visible = true,
        userStories = {
            listOf(
                UserStory(
                    username = "lnm",
                    stories = listOf(
                        Story(
                        ),
                        Story()
                    )
                ),
                UserStory(
                    username = "ovc"
                )
            )
        },
        currentUsername = "",
        onDismiss = { }
    )
}