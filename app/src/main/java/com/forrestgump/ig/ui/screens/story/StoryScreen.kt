package com.forrestgump.ig.ui.screens.story

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
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
    innerPadding: PaddingValues,
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
        val state = rememberPagerState(
            initialPage = 0,
            pageCount = { userStories().size }
        )

        var currentStoryIndex by remember { mutableIntStateOf(0) }

        LaunchedEffect(key1 = storyIndex) {
            state.scrollToPage(page = storyIndex)
        }

        LaunchedEffect(key1 = state.settledPage, key2 = currentStoryIndex) {
            updateViews(userStories()[state.currentPage].stories[currentStoryIndex])
        }
        
        if (userStories().isNotEmpty()) {

        }

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
                            timeStamp = 1719840723950L
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
        innerPadding = PaddingValues(),
        updateViews = { },
        onDismiss = { }
    )
}