package com.forrestgump.ig.ui.components

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.forrestgump.ig.utils.models.Story
import com.forrestgump.ig.utils.models.UserStory

@Composable
fun Stories(
    profileImage: String,
    currentUserId: String,
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
                    StoryCard(
                        userStory = myStories.first(),
                        currentUserId = currentUserId,
                        onClick = onViewMyStoryClick
                    )
                } else {
                    AddStoryCard(
                        profileImage = profileImage,
                        onClick = onAddStoryClicked
                    )
                }
            }

            if (userStories.isNotEmpty()) {
                items(
                    items = userStories,
                    key = { userStory -> userStory.userId }
                ) { story ->
                    StoryCard(
                        userStory = story,
                        currentUserId = currentUserId,
                        onClick = { onStoryClick(userStories.indexOf(story)) }
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun StoriesPreview() {
    Stories(
        profileImage = "",
        currentUserId = "",
        onAddStoryClicked = { },
        onViewMyStoryClick = { },
        onStoryClick = { },
        userStories = listOf(
            UserStory(
                stories = listOf(
                    Story(
                        userId = "132"
                    ),
                    Story(
                        userId = "dfg"
                    ),
                    Story(
                        userId = "hli"
                    ),
                    Story(
                        userId = "mus"
                    ),
                    Story(
                        userId = "google"
                    )
                )
            )
        ),
        myStories = listOf(
            UserStory(
                stories = listOf(
                    Story(
                        userId = "dsd"
                    ),
                    Story(
                        userId = "avc"
                    )
                )
            )
        )
    )
}