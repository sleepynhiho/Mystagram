package com.forrestgump.ig.ui.components

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.forrestgump.ig.utils.models.Story
import com.forrestgump.ig.utils.models.UserStory

@Composable
fun StoryList(
    profileImage: String,
    currentUsername: String,
    onAddStoryClicked: () -> Unit,
    onViewMyStoryClick: () -> Unit,
    onStoryClick: (storyIndex: Int) -> Unit,
    userStories: List<UserStory>,
    myStories: List<UserStory>
) {
    LazyRow(
        content = {
            item(key = "addStory") {
                if (myStories.isNotEmpty() && myStories.first().stories.isNotEmpty()) {
                    UserStoryCard(
                        userStory = myStories.first(),
                        currentUsername = currentUsername,
                        onClick = onViewMyStoryClick
                    )
                } else {
                    AddStoryCard(
                        myProfileImage = profileImage,
                        onClick = onAddStoryClicked
                    )
                }
            }

            if (userStories.isNotEmpty()) {
                items(
                    items = userStories,
                    key = { userStory -> userStory.username }
                ) { story ->
                    UserStoryCard(
                        userStory = story,
                        currentUsername = currentUsername,
                        onClick = { onStoryClick(userStories.indexOf(story)) }
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun StoryListPreview() {
    StoryList(
        profileImage = "",
        currentUsername = "",
        onAddStoryClicked = { },
        onViewMyStoryClick = { },
        onStoryClick = { },
        userStories = listOf(
            UserStory(
                stories = listOf(
                    Story(
                        username = "132"
                    ),
                    Story(
                        username = "dfg"
                    ),
                    Story(
                        username = "hli"
                    ),
                    Story(
                        username = "mus"
                    ),
                    Story(
                        username = "google"
                    )
                )
            )
        ),
        myStories = listOf(
            UserStory(
                stories = listOf(
                    Story(
                        username = "dsd"
                    ),
                    Story(
                        username = "avc"
                    )
                )
            )
        )
    )
}