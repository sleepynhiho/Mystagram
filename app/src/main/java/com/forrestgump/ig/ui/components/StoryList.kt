package com.forrestgump.ig.ui.components

import androidx.annotation.OptIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.forrestgump.ig.R
import com.forrestgump.ig.ui.screens.addStory.components.AddStoryCard
import com.forrestgump.ig.data.models.Story
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.data.models.UserStory

@OptIn(UnstableApi::class)
@Composable
fun StoryList(
    currentUser: User,
    onAddStoryClicked: () -> Unit,
    onViewStoryClicked: (storyIndex: Int, isMyStory: Boolean) -> Unit,
    userStories: List<UserStory>,
    myStories: List<UserStory>
) {
    LazyRow(
        content = {
            item(key = "addStory") {
                if (myStories.isNotEmpty() && myStories.first().stories.isNotEmpty()) {
                    UserStoryCard(
                        userStory = myStories.first(),
                        currentUser = currentUser,
                        onViewStoryClicked = { onViewStoryClicked(0, true) },
                        onAddStoryClicked = onAddStoryClicked
                    )
                } else {
                    AddStoryCard(
                        myProfileImage = (currentUser.profileImage.ifEmpty { R.drawable.default_profile_image }).toString(),
                        onClick = onAddStoryClicked
                    )
                }
            }
            Log.d("NHII", "HEREEEE")
            Log.d("NHII user stories: ", userStories.toString())

            if (userStories.isNotEmpty()) {

                items(
                    items = userStories,
                    key = { userStory -> userStory.username }
                ) { story ->
                    UserStoryCard(
                        userStory = story,
                        currentUser = currentUser,
                        onViewStoryClicked = { onViewStoryClicked(userStories.indexOf(story), false) },
                        onAddStoryClicked = onAddStoryClicked
                    )
                    Log.d("NHII userstorycard", story.toString())
                    Log.d("NHII index clicked: ", userStories.indexOf(story).toString())
                }
            }
        }
    )
}

@Preview
@Composable
fun StoryListPreview() {
    StoryList(
        onAddStoryClicked = { },
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
        ),
        currentUser = TODO(),
        onViewStoryClicked = TODO()
    )
}