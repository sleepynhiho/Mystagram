package com.forrestgump.ig.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.ui.components.PostItem
import com.forrestgump.ig.utils.constants.Utils.MainBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    post: Post,
    onBackPressed: () -> Unit,
    navController: NavController,
    currentUser: User
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MainBackground // Hoặc Color.Black, tuỳ bạn
    ) {
        Column {
            TopAppBar(
                title = {
                    Text(text = "Detail", color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
            PostItem(
                post = post,
                onCommentClicked = {},
                navController = navController,
                currentUser = currentUser
            )
        }
    }
}