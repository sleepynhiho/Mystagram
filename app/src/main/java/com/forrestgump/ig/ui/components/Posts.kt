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
    contentPadding: PaddingValues = PaddingValues(),
    posts: List<Post>,
    state: LazyListState  = rememberLazyListState(),
    showTopContent: Boolean = true,
    topContent: @Composable () -> Unit = { } // nhận nội dung bên trong {} làm topcontent
) {

    LazyColumn(
        contentPadding = contentPadding,
        state = state,
        content = {
            if (showTopContent) {
                item(key = "topContent") {
                    Column {
                        topContent()
                    }
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
        topContent = {
            androidx.compose.material3.Text("This is top content")
        }
    )
}

