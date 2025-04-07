package com.forrestgump.ig.ui.screens.home.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.ui.components.PostItem

@Composable
fun PostList(
    posts: List<Post>,
    onLoadMore: () -> Unit,
    currentUserID: String
) {
    LazyColumn {
        itemsIndexed(posts) { index, post ->
            // Comment lại vì hiện tại PostItem có truyền thêm tham số navController.
//            PostItem(
//                post = post,
//                onCommentClicked = {},
//                currentUserID = currentUserID
//            )
            // Khi hiển thị bài viết cuối cùng, gọi load thêm
            if (index == posts.lastIndex) {
                onLoadMore()
            }
        }
    }
}