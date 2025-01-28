package com.forrestgump.ig.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi
import com.forrestgump.ig.utils.models.Post

@Composable
fun Posts(
    innerPadding: PaddingValues = PaddingValues(),
    posts: List<Post>,
    enableHeader: Boolean = true,
    state: LazyListState,
    topContent: @Composable () -> Unit = { }
) {

    LazyColumn(
        contentPadding = innerPadding,
        state = state,
        content = {
            if (enableHeader) {
                item(key = "topContent") {
                    Column {
                        topContent()
                    }
                }
            }

            if (posts.isNotEmpty()) {
                items(
                    items = posts,
                    key = { post -> "${post.userId}${post.timeStamp}" }
                ) {
                }
            }
        }
    )

}

@UnstableApi
@Preview
@Composable
private fun PostsPreview() {
    Posts(
        posts = listOf(
            Post(
                mediaList = listOf("a"),
                username = "abc",
            ),
            Post(
                mediaList = listOf("b"),
                username = "def",
            ),
        ),
        state = rememberLazyListState()
    )
}