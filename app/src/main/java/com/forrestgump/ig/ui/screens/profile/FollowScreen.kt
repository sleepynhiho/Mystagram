package com.forrestgump.ig.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.ui.components.Loading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowScreen(
    navController: NavController,
    viewModel: FollowViewModel,
    isFollower: Boolean, // true for followers, false for following
    targetUserId: String? = null // if null, it's the current user's profile
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserData(isFollower = isFollower, targetUserId = targetUserId)
    }

    if (uiState.isLoading) {
        Loading()
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = uiState.username) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // No followers/following message
                if (uiState.users.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isFollower)
                                "Không có người theo dõi nào"
                            else
                                "Không theo dõi ai",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    // Header
                    Text(
                        text = "${uiState.headerText}: ${uiState.users.size}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp,
                        color = Color.LightGray
                    )

                    // List of users
                    LazyColumn {
                        items(items = uiState.users) { user ->
                            FollowerRow(
                                follower = user,
                                onDelete = {
                                    if (uiState.isMyProfile) {
                                        if (isFollower) {
                                            viewModel.removeFollower(user)
                                        } else {
                                            viewModel.unfollowUser(user)
                                        }
                                    }
                                },
                                showDeleteButton = uiState.isMyProfile
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FollowerRow(
    follower: User,
    onDelete: () -> Unit,
    showDeleteButton: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        AsyncImage(
            model = follower.profileImage,
            contentDescription = follower.username,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // User info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = follower.fullName,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = follower.username,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Delete button (only shown for current user's profile)
        if (showDeleteButton) {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete follower"
                )
            }
        }
    }
}