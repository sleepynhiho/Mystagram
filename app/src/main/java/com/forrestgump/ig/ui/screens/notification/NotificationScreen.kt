package com.forrestgump.ig.ui.screens.notification

import androidx.compose.foundation.background
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.forrestgump.ig.ui.screens.notification.components.NotificationList
import com.forrestgump.ig.ui.screens.notification.components.NotificationTopBar
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController


@Composable
fun NotificationScreen(
    navHostController: NavHostController,
    currentUserId: String,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()

    LaunchedEffect(currentUserId) {
        viewModel.observeNotifications(currentUserId)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 10.dp),
        topBar = {
            NotificationTopBar(navHostController)
        }
    ) { innerPadding ->
        NotificationList(
            notifications = notifications,
            innerPadding = innerPadding,
            navHostController = navHostController
        )
    }
}

@Preview
@Composable
private fun NotificationScreenPreview() {
    NotificationScreen(
        navHostController = rememberNavController(),
        currentUserId = "1"
    )
}
