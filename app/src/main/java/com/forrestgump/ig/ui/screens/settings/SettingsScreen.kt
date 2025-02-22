package com.forrestgump.ig.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    // State for Dark Mode and Account Status
    var isDarkMode by remember { mutableStateOf(false) }
    val isPremium by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings and activity",
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                ),
                navigationIcon = { // Thêm nút quay lại nếu cần
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Section: How you use Instagram
            item {
                SectionHeader(title = "How you use Instagram")
            }
            items(
                listOf(
                    SettingsItemData(icon = Icons.Default.Bookmark, title = "Saved"),
                    SettingsItemData(icon = Icons.Default.Notifications, title = "Notifications")
                )
            ) { itemData ->
                SettingsRow(itemData = itemData)
            }

            // Section: Who can see your content
            item {
                SectionHeader(title = "Who can see your content")
            }
            items(
                listOf(
                    SettingsItemData(
                        icon = Icons.Default.Lock,
                        title = "Account privacy",
                        subtitle = "Private"
                    ),
                    SettingsItemData(
                        icon = Icons.Default.Group,
                        title = "Close Friends",
                        subtitle = "0"
                    ),
                    SettingsItemData(icon = Icons.Default.Block, title = "Blocked", subtitle = "0")
                )
            ) { itemData ->
                SettingsRow(itemData = itemData)
            }

            // Section: What you see
            item {
                SectionHeader(title = "What you see")
            }
            items(
                listOf(
                    SettingsItemData(
                        icon = Icons.Default.Favorite,
                        title = "Favorites",
                        subtitle = "0"
                    ),
                    SettingsItemData(
                        icon = Icons.Default.VolumeOff,
                        title = "Muted accounts",
                        subtitle = "0"
                    )
                )
            ) { itemData ->
                SettingsRow(itemData = itemData)
            }

            // Section: Your app and media
            item {
                SectionHeader(title = "Your app and media")
            }
            items(
                listOf(
                    SettingsItemData(icon = Icons.Default.Language, title = "Multi-language")
                )
            ) { itemData ->
                SettingsRow(itemData = itemData)
            }

            item {
                SettingsRowWithSwitch(
                    icon = Icons.Default.Brightness6,
                    title = "Dark Mode",
                    checked = isDarkMode,
                    onCheckedChange = { isDarkMode = it }
                )
            }

            item {
                SettingsRow(
                    itemData = SettingsItemData(
                        icon = Icons.Default.Star,
                        title = "Account Status",
                        subtitle = if (isPremium) "Premium" else "Basic"
                    )
                )
            }

            // Section: Your orders and fundraisers
            item {
                SectionHeader(title = "Your orders and fundraisers")
            }
            item {
                SettingsRow(
                    itemData = SettingsItemData(
                        icon = Icons.Default.Payment,
                        title = "Orders and payments"
                    )
                )
            }

            // Section: Login
            item {
                SectionHeader(title = "Login")
            }
            items(
                listOf(
                    SettingsItemData(icon = Icons.Default.PersonAdd, title = "Add account"),
                    SettingsItemData(icon = Icons.Default.ExitToApp, title = "Log out"),
                    SettingsItemData(icon = Icons.Default.ExitToApp, title = "Log out all accounts")
                )
            ) { itemData ->
                SettingsRow(itemData = itemData)
            }
        }
    }
}
