package com.forrestgump.ig.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.forrestgump.ig.ui.components.Loading
import com.forrestgump.ig.ui.components.Posts
import com.forrestgump.ig.ui.components.StoryList
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.models.Story
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.data.models.UserStory
import com.forrestgump.ig.ui.components.PostItem
import java.util.Date

@UnstableApi
@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    uiState: UiState,
    currentUser: User,
    onAddStoryClicked: () -> Unit,
    onStoryScreenClicked: (Boolean, Int) -> Unit,
    onChatScreenClicked: () -> Unit,
) {
    var userStoryIndex by remember { mutableIntStateOf(0) }
    var isMyStory by remember { mutableStateOf(true) }

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
                ) {
                    TopNavBar(onChatScreenClicked)

                    StoryList(
                        currentUser = currentUser,
                        onAddStoryClicked = onAddStoryClicked,
                        onViewStoryClicked = { index, myStory ->
                            userStoryIndex = index
                            onStoryScreenClicked(true, userStoryIndex)
                            isMyStory = myStory
                        },
                        userStories = uiState.userStories,
                        myStories = uiState.myStories
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(top = 8.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                    )

                    PostItem(
                        post = Post(
                            postId = "1",
                            userId = "user_123",
                            username = "tinh_ngu_chua",
                            profileImageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQUyAfXfniYfSTZ7Z2HjW2COSyC8WTH3TgkGw&s",
                            mediaUrls = listOf(
                                "https://media.istockphoto.com/id/610041376/photo/beautiful-sunrise-over-the-sea.jpg?s=612x612&w=0&k=20&c=R3Tcc6HKc1ixPrBc7qXvXFCicm8jLMMlT99MfmchLNA=",
                                "https://wallpapers.com/images/hd/beautiful-nature-pictures-2c29nke7owomq7la.jpg",
                                "https://thumbs.dreamstime.com/b/beautiful-rain-forest-ang-ka-nature-trail-doi-inthanon-national-park-thailand-36703721.jpg"
                            ),
                            caption = "Hôm nay đi ăn kem nè!",
                            commentsCount = 5,
                            mimeType = "image/png",
                            timestamp = Date()
                        ),
                        onLikeClicked = {},
                        onCommentClicked = {},
                    )
                }
            }
        } else {
            Loading()
        }
    }

    StoryScreen(
        visible = uiState.showStoryScreen,
        currentUser = currentUser,
        onDismiss = { onStoryScreenClicked(false, 0) },
        userStories = { if(isMyStory) uiState.myStories else uiState.userStories },
        onUserStoryIndexChanged = { newIndex -> userStoryIndex = newIndex },
        userStoryIndex = userStoryIndex
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
        posts = listOf(
            Post(
                username = "cab"
            )
        ),
        isLoading = false,
        showStoryScreen = false
    )

    HomeScreen(
        contentPadding = PaddingValues(),
        uiState = uiState,
        onAddStoryClicked = {},
        onChatScreenClicked = {},
        currentUser = TODO(),
        onStoryScreenClicked = TODO(),
    )
}