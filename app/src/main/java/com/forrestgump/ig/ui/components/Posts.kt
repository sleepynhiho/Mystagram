package com.forrestgump.ig.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.forrestgump.ig.data.models.Post
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.navigation.NavController
import com.forrestgump.ig.ui.screens.home.components.EndOfFeedMessage

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Posts(
    contentPadding: PaddingValues = PaddingValues(),
    state: LazyListState = rememberLazyListState(),
    showTopContent: Boolean = true,
    topContent: @Composable () -> Unit = { },
    posts: List<Post>,
    isRefreshing: Boolean,
    isLoadingMore: Boolean,
    hasMore: Boolean,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    currentUserID: String,
    navController: NavController,
) {
    // Tạo pull refresh state mới của Material
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )
    Box(modifier = Modifier
        .fillMaxSize()
        .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            contentPadding = contentPadding,
            state = state
        ) {
            if (showTopContent) {
                item(key = "topContent") {
                    Column { topContent() }
                }
            }
            itemsIndexed(posts) { index, post ->
                PostItem(
                    post = post,
                    onCommentClicked = {},
                    currentUserID = currentUserID,
                    navController = navController,
                )
                // Khi hiển thị post cuối cùng, gọi load thêm
                if (index == posts.lastIndex) {
                    onLoadMore()
                }
            }
            // Item footer: loading hoặc thông báo hết post
            item {
                if (isLoadingMore) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                    }
                } else if (!hasMore) {
                    EndOfFeedMessage()
                }
            }
        }
        // Hiển thị indicator pull refresh ở top
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

}


//@UnstableApi
//@Preview
//@Composable
//private fun PostsPreview() {
//    Posts(
//        topContent = {
//            androidx.compose.material3.Text("This is top content")
//        }
//    )
//}
