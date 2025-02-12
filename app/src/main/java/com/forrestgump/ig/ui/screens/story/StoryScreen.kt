package com.forrestgump.ig.ui.screens.story

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.tooling.preview.Preview
import com.forrestgump.ig.ui.components.StoryCard
import com.forrestgump.ig.ui.screens.story.components.UserStoryCard
import com.forrestgump.ig.utils.models.Story
import com.forrestgump.ig.utils.models.UserStory

enum class Stories {
    MY_STORY,
    USER_STORY
}

@Composable
fun StoryScreen(
    storyIndex: Int,
    visible: Boolean,
    userStories: () -> List<UserStory>,
    currentUserId: String,
    contentPadding: PaddingValues,
    updateViews: (Story) -> Unit,
    onDismiss: () -> Unit
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
        storyIndex = 0,
        visible = true,
        userStories = {
            listOf(
                UserStory(
                    username = "lnm",
                    stories = listOf(
                        Story(
                            timestamp = 1719840723950L
                        ),
                        Story()
                    )
                ),
                UserStory(
                    username = "ovc"
                )
            )
        },
        currentUserId = "",
        contentPadding = PaddingValues(),
        updateViews = { },
        onDismiss = { }
    )
}