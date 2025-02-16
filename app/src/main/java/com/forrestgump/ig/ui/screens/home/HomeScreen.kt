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
import com.forrestgump.ig.ui.components.StoryList
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.forrestgump.ig.utils.models.Post
import com.forrestgump.ig.utils.models.Story
import com.forrestgump.ig.utils.models.UserStory

@UnstableApi
@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    uiState: UiState,
    userProfileImage: String,
    currentUsername: String,
    onAddStoryClicked: () -> Unit,
    onStoryScreenClicked: (Boolean) -> Unit,
    onMessagesScreenClicked: () -> Unit
) {

    var userStoryIndex by remember { mutableIntStateOf(0) }
    var selectedStoryType by remember { mutableStateOf(Stories.USER_STORY) }

    // Thêm mock data nếu userStories rỗng
    val mockUserStories = listOf(
        UserStory(
            username = "johndoe",
            profileImage = "https://randomuser.me/api/portraits/men/1.jpg",
            stories = listOf(
                Story(username = "johndoe", image = "https://via.placeholder.com/300", timestamp = System.currentTimeMillis())
            )
        )
    )

    val finalUiState = if (uiState.userStories.isEmpty()) {
        uiState.copy(userStories = mockUserStories, myStories = mockUserStories.take(1))
    } else {
        uiState
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MainBackground
    ) {
        uiState.isLoading = false
        if (!uiState.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Posts(
                    contentPadding = contentPadding,
                    posts = uiState.posts
                ) {
                    TopNavBar(onMessagesScreenClicked)

                    StoryList(
                        profileImage = userProfileImage,
                        currentUsername = currentUsername,
                        onAddStoryClicked = onAddStoryClicked,
                        onViewMyStoryClick = {
                            userStoryIndex = 0
                            selectedStoryType = Stories.MY_STORY
                            onStoryScreenClicked(true)
                        },
                        onStoryClick = { storyIndex ->
                            userStoryIndex = storyIndex
                            selectedStoryType = Stories.USER_STORY
                            onStoryScreenClicked(true)
                        },
                        userStories = finalUiState.userStories,
                        myStories = finalUiState.myStories
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
        visible = finalUiState.showStoryScreen,
        userStories = {
            if (selectedStoryType == Stories.MY_STORY) finalUiState.myStories else finalUiState.userStories
        },
        currentUsername = currentUsername,
        onDismiss = { onStoryScreenClicked(false) }
    )
    
    
}

@UnstableApi
@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
fun HomeScreenPreview() {
    val userStories = listOf(
        UserStory(
            username = "abc",
            profileImage = "",
            stories = listOf(Story(username = "abc"))
        )
    )

    val uiState = UiState(
        userStories = userStories,
        myStories = userStories,
        posts = listOf(Post(mediaList = listOf("https://via.placeholder.com/150"), username = "cab")),
        isLoading = false,
        showStoryScreen = false
    )

    HomeScreen(
        contentPadding = PaddingValues(),
        uiState = uiState,
        userProfileImage = "https://static.vecteezy.com/system/resources/previews/004/899/680/non_2x/beautiful-blonde-woman-with-makeup-avatar-for-a-beauty-salon-illustration-in-the-cartoon-style-vector.jpg",
        currentUsername = "123",
        onAddStoryClicked = {},
        onStoryScreenClicked = {},
        onMessagesScreenClicked = {},
    )
}