package com.forrestgump.ig.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.forrestgump.ig.R
import com.forrestgump.ig.ui.navigation.Routes
import com.forrestgump.ig.ui.theme.ThemeManager
import com.forrestgump.ig.utils.constants.changeAppLanguage
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    // Truy cập ThemeManager qua Hilt EntryPoint
    val themeManager =
        EntryPointAccessors.fromApplication(context, ThemeManagerEntryPoint::class.java)
            .themeManager()
    val currentTheme by themeManager.currentTheme.collectAsState("system") // Quan sát trạng thái chủ đề
    val isPremium by remember { mutableStateOf(false) }
    val savedText = stringResource(id = R.string.saved)
    val notificationsText = stringResource(id = R.string.notifications)
    val accountPrivacyText = stringResource(id = R.string.account_privacy)
    val privateText = stringResource(id = R.string.privates)
    val closeFriendsText = stringResource(id = R.string.close_friends)
    val blockedText = stringResource(id = R.string.blocked)
    val favoritesText = stringResource(id = R.string.favorites)
    val mutedAccountsText = stringResource(id = R.string.muted_accounts)
    val multiLanguageText = stringResource(id = R.string.multi_language)
    val englishText = stringResource(id = R.string.english_language)
    val vietnamText = stringResource(id = R.string.vietnamese_language)
    val chooseLanguageText = stringResource(id = R.string.choose_language)
    val darkModeText = stringResource(id = R.string.dark_mode)
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showDarkModeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings_title),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            item { SectionHeader(title = stringResource(id = R.string.section_how_you_use)) }
            items(
                listOf(
                    SettingsItemData(icon = Icons.Default.Bookmark, title = savedText),
                    SettingsItemData(icon = Icons.Default.Notifications, title = notificationsText)
                )
            ) { itemData -> SettingsRow(itemData = itemData) }

            item { SectionHeader(title = stringResource(id = R.string.section_who_can_see)) }
            items(
                listOf(
                    SettingsItemData(
                        icon = Icons.Default.Lock,
                        title = accountPrivacyText,
                        subtitle = privateText
                    ),
                    SettingsItemData(
                        icon = Icons.Default.Group,
                        title = closeFriendsText,
                        subtitle = "0"
                    ),
                    SettingsItemData(
                        icon = Icons.Default.Block,
                        title = blockedText,
                        subtitle = "0"
                    )
                )
            ) { itemData -> SettingsRow(itemData = itemData) }

            item { SectionHeader(title = stringResource(id = R.string.section_what_you_see)) }
            items(
                listOf(
                    SettingsItemData(
                        icon = Icons.Default.Favorite,
                        title = favoritesText,
                        subtitle = "0"
                    ),
                    SettingsItemData(
                        icon = Icons.Default.VolumeOff,
                        title = mutedAccountsText,
                        subtitle = "0"
                    )
                )
            ) { itemData -> SettingsRow(itemData = itemData) }

            item { SectionHeader(title = stringResource(id = R.string.section_your_app_media)) }
            items(
                listOf(
                    SettingsItemData(
                        icon = Icons.Default.Language,
                        title = multiLanguageText
                    )
                )
            ) { itemData ->
                SettingsRow(itemData = itemData, onClick = { showLanguageDialog = true })
            }

            items(
                listOf(
                    SettingsItemData(icon = Icons.Default.DarkMode, title = darkModeText)
                )
            ) { itemData ->
                SettingsRow(itemData = itemData, onClick = { showDarkModeDialog = true })
            }

            item {
                SettingsRow(
                    itemData = SettingsItemData(
                        icon = Icons.Default.Star,
                        title = stringResource(id = R.string.account_status),
                        subtitle = if (isPremium) stringResource(id = R.string.premium) else stringResource(
                            id = R.string.basic
                        )
                    )
                )
            }

            item { SectionHeader(title = stringResource(id = R.string.section_orders_fundraisers)) }
            item {
                SettingsRow(
                    itemData = SettingsItemData(
                        icon = Icons.Default.Payment,
                        title = stringResource(id = R.string.orders_payments)
                    )
                )
            }

            item { SectionHeader(title = stringResource(id = R.string.section_login)) }
            item {
                SettingsRow(
                    itemData = SettingsItemData(
                        icon = Icons.Default.PersonAdd,
                        title = stringResource(id = R.string.add_account)
                    )
                )
            }

            item {
                SettingsRow(
                    itemData = SettingsItemData(
                        icon = Icons.Default.ExitToApp,
                        title = stringResource(id = R.string.log_out)
                    ),
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Routes.LoginScreen.route) { popUpTo(0) }
                    }
                )
            }
        }
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Text(
                    text = multiLanguageText,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            text = {
                Text(
                    text = chooseLanguageText,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    changeAppLanguage(context, "vi")
                    showLanguageDialog = false
                }) {
                    Text(
                        text = vietnamText,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    changeAppLanguage(context, "en")
                    showLanguageDialog = false
                }) {
                    Text(
                        text = englishText,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        )
    }

    if (showDarkModeDialog) {
        ThemeSelectionDialog(
            currentTheme = currentTheme,
            themeManager = themeManager,
            onDismiss = { showDarkModeDialog = false }
        )
    }
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: String,
    themeManager: ThemeManager,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.dark_mode)) },
        text = {
            Column {
                ThemeOption(
                    text = stringResource(id = R.string.dark_mode_system),
                    selected = currentTheme == "system",
                    icon = Icons.Default.Settings,
                    onClick = {
                        themeManager.saveThemePreference("system") // Chỉ lưu qua ThemeManager
                        onDismiss()
                    }
                )
                ThemeOption(
                    text = stringResource(id = R.string.dark_mode_light),
                    selected = currentTheme == "light",
                    icon = Icons.Default.LightMode,
                    onClick = {
                        themeManager.saveThemePreference("light") // Chỉ lưu qua ThemeManager
                        onDismiss()
                    }
                )
                ThemeOption(
                    text = stringResource(id = R.string.dark_mode_dark),
                    selected = currentTheme == "dark",
                    icon = Icons.Default.DarkMode,
                    onClick = {
                        themeManager.saveThemePreference("dark") // Chỉ lưu qua ThemeManager
                        onDismiss()
                    }
                )
            }
        },
        confirmButton = {}, // Không cần nút xác nhận
        dismissButton = {}
    )
}

@Composable
fun ThemeOption(
    text: String,
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = text)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text)
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
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsRow(itemData: SettingsItemData, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically


    ) {
        Icon(
            imageVector = itemData.icon,
            contentDescription = itemData.title,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = itemData.title,
            color = MaterialTheme.colorScheme.onBackground,
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
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

data class SettingsItemData(
    val icon: ImageVector,
    val title: String,
    val subtitle: String? = null,
)

// Định nghĩa EntryPoint để truy cập ThemeManager trong Composable
@dagger.hilt.EntryPoint
@InstallIn(dagger.hilt.components.SingletonComponent::class)
interface ThemeManagerEntryPoint {
    fun themeManager(): ThemeManager
}