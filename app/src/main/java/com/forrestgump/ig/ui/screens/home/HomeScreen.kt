package com.forrestgump.ig.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.forrestgump.ig.ui.screens.home.components.TopNavBar
import com.forrestgump.ig.ui.screens.story.StoryScreen
import com.forrestgump.ig.ui.screens.story.Stories
import com.forrestgump.ig.ui.components.Loading
import com.forrestgump.ig.ui.components.Posts
import com.forrestgump.ig.ui.components.Stories
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.forrestgump.ig.utils.models.Post
import com.forrestgump.ig.utils.models.Story
import com.forrestgump.ig.utils.models.UserStory

@UnstableApi
@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    uiState: UiState,
    following: List<String>,
    profileImage: String,
    currentUserId: String,
    onAddStoryClick: () -> Unit,
    updateViews: (Story) -> Unit,
    setShowStoryScreen: (Boolean) -> Unit
) {
    val state = rememberLazyListState()

    var userStoryIndex by remember { mutableIntStateOf(0) }
    var selectedStory by remember { mutableStateOf(Stories.USER_STORY) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MainBackground
    ) {
        uiState.isLoading = false
        if (!uiState.isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Posts(
                        innerPadding = innerPadding,
                        posts = uiState.posts,
                        state = state
                    ) {
                        TopNavBar()

                        Stories(
                            profileImage = profileImage,
                            currentUserId = currentUserId,
                            onAddStoryClick = onAddStoryClick,
                            onViewMyStoryClick = {
                                userStoryIndex = 0
                                selectedStory = Stories.MY_STORY
                                setShowStoryScreen(true)
                            },
                            onStoryClick = { storyIndex ->
                                userStoryIndex = storyIndex
                                selectedStory = Stories.USER_STORY
                                setShowStoryScreen(true)
                            },
                            userStories = uiState.userStories,
                            myStories = uiState.myStories
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(top = 8.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                        )
                    }
                }
        } else {
            Loading()
        }
    }

    StoryScreen(
        storyIndex = userStoryIndex,
        visible = uiState.showStoryScreen,
        userStories = {
            if (selectedStory == Stories.MY_STORY) uiState.myStories else uiState.userStories
        },
        currentUserId = currentUserId,
        innerPadding = innerPadding,
        updateViews = updateViews,
        onDismiss = { setShowStoryScreen(false) }
    )
}

@UnstableApi
@Preview(
    showBackground = true,
    backgroundColor = 0XFF000000
)
@Composable
fun HomeScreenPreview() {
    val userStories = listOf(
        UserStory(
            username = "android",
            profileImage = "",
            stories = listOf(
                Story(
                    userId = "android"
                )
            )
        ),
        UserStory(
            username = "abc",
            profileImage = "",
            stories = listOf(
                Story(
                    userId = "abc"
                )
            )
        ),
        UserStory(
            username = "abc",
            profileImage = "",
            stories = listOf(
                Story(
                    userId = "abc",
                )
            )
        ),
        UserStory(
            username = "abc",
            profileImage = "",
            stories = listOf(
                Story(
                    userId = "abc"
                )
            )
        )
    )

    HomeScreen(
        innerPadding = PaddingValues(),
        uiState = UiState(
            userStories = userStories,
            posts = listOf(
                Post(
                    mediaList = listOf(""),
                    username = "cab",
                )
            )
        ),
        following = listOf("1"),
        profileImage = "",
        currentUserId = "12345",
        onAddStoryClick = { },
        updateViews = { },
        setShowStoryScreen = { }
    )
}