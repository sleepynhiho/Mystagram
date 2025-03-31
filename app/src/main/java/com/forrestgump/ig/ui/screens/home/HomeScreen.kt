package com.forrestgump.ig.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
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
import com.forrestgump.ig.ui.screens.home.components.PostList
import java.util.Date

@UnstableApi
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
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
                    showTopContent = true,
                    topContent = {
                        TopNavBar(onChatScreenClicked)
                        StoryList(
                            currentUser = currentUser,
                            onAddStoryClicked = onAddStoryClicked,
                            onViewStoryClicked = { index, myStory ->
                                onStoryScreenClicked(true, index)
                            },
                            userStories = uiState.userStories,
                            myStories = uiState.myStories
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 8.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                        )
                    },
                    posts = uiState.posts,
                    onLoadMore = { viewModel.loadNextPosts() }
                )
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

