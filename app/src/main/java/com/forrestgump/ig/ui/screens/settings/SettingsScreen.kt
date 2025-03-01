package com.forrestgump.ig.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.forrestgump.ig.utils.constants.Utils.MainBackground

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
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainBackground
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
                .background(MainBackground)
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

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        color = Color.Gray,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .background(MainBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun AccountsCenterRow() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: điều hướng sang Accounts Center */ }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Accounts Center",
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Meta",
                color = Color.Gray,
                fontSize = 12.sp
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Password, security, personal details, ad preferences",
            color = Color.Gray,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Manage your connected experiences and account settings\nacross Meta technologies. Learn more",
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun SettingsRow(itemData: SettingsItemData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: xử lý nhấn (nếu cần) */ }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = itemData.icon,
            contentDescription = itemData.title,
            tint = Color.Black,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = itemData.title,
            color = Color.Black,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
        if (itemData.subtitle != null) {
            Text(
                text = itemData.subtitle,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun SettingsRowWithSwitch(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.Black,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = Color.Black,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color.Gray,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.DarkGray
            )
        )
    }
}

data class SettingsItemData(
    val icon: ImageVector,
    val title: String,
    val subtitle: String? = null
)
