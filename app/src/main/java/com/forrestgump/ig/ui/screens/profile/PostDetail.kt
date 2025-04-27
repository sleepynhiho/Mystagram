package com.forrestgump.ig.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.ui.components.CommentScreen
import com.forrestgump.ig.ui.components.PostItem
import com.forrestgump.ig.ui.navigation.Routes
import com.forrestgump.ig.ui.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    post: Post,
    onBackPressed: () -> Unit,
    navController: NavController,
    currentUser: User,
    userViewModel: UserViewModel, // Thêm tham số này
    optionsViewModel: PostOptionsViewModel,
    profileViewModel: ProfileViewModel? = null, // Add ProfileViewModel parameter
) {
    var showOptionsMenu by remember { mutableStateOf(false) }
    val optionsUiState by optionsViewModel.uiState.collectAsState()
    var showPremiumDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showCommentScreen by remember { mutableStateOf(false) }
    val user by userViewModel.user.collectAsState()

    // Check if it's the current user's post and if it's promoted
    val isMyPost = post.userId == currentUser.userId

    LaunchedEffect(post.postId) {
        optionsViewModel.checkIfPostIsPromoted(post.postId)
    }

    LaunchedEffect(post.postId) {
        userViewModel.fetchCurrentUser()
    }

    // Navigate back if post was deleted
    LaunchedEffect(optionsUiState.isPostDeleted) {
        if (optionsUiState.isPostDeleted) {
            onBackPressed()
            optionsViewModel.resetDeleteStatus()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopAppBar(
                title = {
                    Text(text = "Detail", color = MaterialTheme.colorScheme.onBackground)
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    // Only show options menu if it's the user's own post
                    if (isMyPost) {
                        Box {
                            IconButton(onClick = { showOptionsMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More options",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            DropdownMenu(
                                expanded = showOptionsMenu,
                                onDismissRequest = { showOptionsMenu = false }
                            ) {
                                if (optionsUiState.isPostPromoted) {
                                    // If post is already promoted, show option to remove promotion
                                    DropdownMenuItem(
                                        text = { Text("Hủy quảng cáo") },
                                        onClick = {
                                            optionsViewModel.removePromotion(post.postId)
                                            showOptionsMenu = false
                                        }
                                    )
                                } else {
                                    // If post is not promoted, show option to promote
                                    DropdownMenuItem(
                                        text = { Text("Đăng bài quảng cáo") },
                                        onClick = {
                                            if (user?.isPremium == true) {
                                                optionsViewModel.promotePost(
                                                    post.postId,
                                                    user?.userId ?: "", // Sử dụng user thay vì currentUser
                                                    true
                                                )
                                            } else {
                                                showPremiumDialog = true
                                            }
                                            showOptionsMenu = false
                                        }
                                    )
                                }
                                // Add delete option
                                DropdownMenuItem(
                                    text = { Text("Xóa bài viết") },
                                    onClick = {
                                        showDeleteConfirmDialog = true
                                        showOptionsMenu = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )

            PostItem(
                post = post,
                onCommentClicked = {
                    showCommentScreen = true
                },
                navController = navController,
                currentUser = currentUser
            )

            // Show success or error messages
            optionsUiState.errorMessage?.let { errorMsg ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        Text(
                            text = "Đóng",
                            modifier = Modifier.clickable { optionsViewModel.clearMessages() }
                        )
                    }
                ) {
                    Text(text = errorMsg)
                }
            }

            optionsUiState.successMessage?.let { successMsg ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        Text(
                            text = "Đóng",
                            modifier = Modifier.clickable { optionsViewModel.clearMessages() }
                        )
                    }
                ) {
                    Text(text = successMsg)
                }
            }
        }
    }

    // Premium upgrade dialog
    if (showPremiumDialog) {
        AlertDialog(
            onDismissRequest = { showPremiumDialog = false },
            title = { Text("Cần nâng cấp tài khoản") },
            text = {
                Text("Để sử dụng tính năng đăng quảng cáo, bạn cần nâng cấp lên tài khoản Premium.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        navController.navigate(Routes.CheckoutScreen.route)
                        showPremiumDialog = false
                    }
                ) {
                    Text("Nâng cấp ngay")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showPremiumDialog = false }
                ) {
                    Text("Để sau")
                }
            }
        )
    }

    // Delete confirmation dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Xác nhận xóa") },
            text = {
                Text("Bạn có chắc chắn muốn xóa bài viết này? Hành động này không thể hoàn tác.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        optionsViewModel.deletePost(post.postId, profileViewModel)
                        showDeleteConfirmDialog = false
                    }
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteConfirmDialog = false }
                ) {
                    Text("Hủy")
                }
            }
        )
    }
    if (showCommentScreen) {
        CommentScreen(
            post = post,
            currentUser = currentUser,
            showCommentScreen = showCommentScreen,
            onDismiss = { showCommentScreen = false }
        )
    }
}