package com.forrestgump.ig.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.forrestgump.ig.R
import com.forrestgump.ig.ui.navigation.Routes
import com.forrestgump.ig.ui.theme.ThemeManager
import com.forrestgump.ig.utils.constants.changeAppLanguage
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    // Truy cập ThemeManager qua Hilt EntryPoint
    val themeManager =
        EntryPointAccessors.fromApplication(context, ThemeManagerEntryPoint::class.java)
            .themeManager()
    val currentTheme by themeManager.currentTheme.collectAsState("system") // Quan sát trạng thái chủ đề
    val multiLanguageText = stringResource(id = R.string.multi_language)
    val englishText = stringResource(id = R.string.english_language)
    val vietnamText = stringResource(id = R.string.vietnamese_language)
    val chooseLanguageText = stringResource(id = R.string.choose_language)
    val darkModeText = stringResource(id = R.string.dark_mode)
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showDarkModeDialog by remember { mutableStateOf(false) }

    // Change Password states
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var isChangingPassword by remember { mutableStateOf(false) }
    
    // Store string resources for use in callbacks
    val passwordChangedText = "Password successfully changed"
    val passwordMismatchText = "Password does not match"
    
    // Verify Account states
    var showVerifyAccountDialog by remember { mutableStateOf(false) }
    var isVerifyingEmail by remember { mutableStateOf(false) }
    var isRefreshingVerification by remember { mutableStateOf(false) }
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Function to refresh the user verification status
    val refreshVerificationStatus = {
        isRefreshingVerification = true
        currentUser?.reload()?.addOnCompleteListener {
            isRefreshingVerification = false
            // No need to reassign currentUser, we'll get fresh data when we check
        }
    }
    
    // Check verification status when dialog is shown
    LaunchedEffect(showVerifyAccountDialog) {
        if (showVerifyAccountDialog) {
            refreshVerificationStatus()
        }
    }

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

            item { SectionHeader(title = stringResource(id = R.string.section_login)) }

            // Account Settings
            items(
                listOf(
                    SettingsItemData(
                        icon = Icons.Default.Lock,
                        title = "Change Password"
                    )
                )
            ) { itemData ->
                SettingsRow(itemData = itemData, onClick = { showChangePasswordDialog = true })
            }

            items(
                listOf(
                    SettingsItemData(
                        icon = Icons.Default.Verified,
                        title = "Verify Account",
                        subtitle = if (FirebaseAuth.getInstance().currentUser?.isEmailVerified == true)
                            "Account Verified"
                        else
                            "Account Not Verified"
                    )
                )
            ) { itemData ->
                SettingsRow(itemData = itemData, onClick = { 
                    refreshVerificationStatus()
                    showVerifyAccountDialog = true 
                })
            }

            item {
                SettingsRow(
                    itemData = SettingsItemData(
                        icon = Icons.Default.ExitToApp,
                        title = stringResource(id = R.string.log_out)
                    ),
                    onClick = {
                        // Sign out from Firebase
                        FirebaseAuth.getInstance().signOut()

                        // Sign out from Google to ensure account picker is shown next time
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(context.getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()

                        try {
                            val googleSignInClient = GoogleSignIn.getClient(context, gso)
                            googleSignInClient.signOut().addOnCompleteListener {
                                // Navigate back to login screen
                                navController.navigate(Routes.LoginScreen.route) {
                                    popUpTo(0)
                                }
                            }
                        } catch (e: Exception) {
                            // If Google sign-out fails, still navigate to login screen
                            navController.navigate(Routes.LoginScreen.route) {
                                popUpTo(0)
                            }
                        }
                    }
                )
            }
        }
    }

    // Language Dialog
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

    // Dark Mode Dialog
    if (showDarkModeDialog) {
        ThemeSelectionDialog(
            currentTheme = currentTheme,
            themeManager = themeManager,
            onDismiss = { showDarkModeDialog = false },
            onThemeChanged = { themeManager.applyTheme() } // Apply theme immediately
        )
    }

    // Change Password Dialog
    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { 
                showChangePasswordDialog = false
                currentPassword = ""
                newPassword = ""
                confirmNewPassword = ""
            },
            title = {
                Text(text = "Change Password")
            },
            text = {
                Column {
                    // Current password
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                    
                    // New password
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                    
                    // Confirm new password
                    OutlinedTextField(
                        value = confirmNewPassword,
                        onValueChange = { confirmNewPassword = it },
                        label = { Text("Confirm New Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPassword != confirmNewPassword) {
                            Toast.makeText(context, passwordMismatchText, Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        if (currentPassword.isNotEmpty() && newPassword.isNotEmpty() && !isChangingPassword) {
                            isChangingPassword = true
                            
                            val user = FirebaseAuth.getInstance().currentUser
                            user?.let { firebaseUser ->
                                val email = firebaseUser.email
                                
                                if (email != null) {
                                    // Re-authenticate user
                                    val credential =
                                        EmailAuthProvider.getCredential(email, currentPassword)
                                    firebaseUser.reauthenticate(credential)
                                        .addOnCompleteListener { reauthTask ->
                                            if (reauthTask.isSuccessful) {
                                                // Change password
                                                firebaseUser.updatePassword(newPassword)
                                                    .addOnCompleteListener { updateTask ->
                                                        isChangingPassword = false
                                                        if (updateTask.isSuccessful) {
                                                            Toast.makeText(context, passwordChangedText, Toast.LENGTH_SHORT).show()
                                                            // Clear fields and close dialog after success
                                                            currentPassword = ""
                                                            newPassword = ""
                                                            confirmNewPassword = ""
                                                            showChangePasswordDialog = false
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                "Error changing password: ${updateTask.exception?.message ?: ""}",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    }
                                            } else {
                                                isChangingPassword = false
                                                Toast.makeText(
                                                    context,
                                                    "Error changing password: ${reauthTask.exception?.message ?: ""}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                }
                            } ?: run {
                                isChangingPassword = false
                                Toast.makeText(context, "Error: User is not logged in", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = currentPassword.isNotEmpty() && newPassword.isNotEmpty() && 
                              confirmNewPassword.isNotEmpty() && !isChangingPassword
                ) {
                    if (isChangingPassword) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(text = "Confirm")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showChangePasswordDialog = false
                    currentPassword = ""
                    newPassword = ""
                    confirmNewPassword = ""
                }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    // Verify Account Dialog
    if (showVerifyAccountDialog) {
        AlertDialog(
            onDismissRequest = { 
                showVerifyAccountDialog = false 
            },
            title = {
                Text(text = "Verify Account")
            },
            text = {
                Column {
                    if (isRefreshingVerification) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    } else {
                        if (FirebaseAuth.getInstance().currentUser?.isEmailVerified == true) {
                            Text(text = "Account Verified")
                        } else {
                            Text(text = "Account Not Verified")
                        }
                    }
                }
            },
            confirmButton = {
                val isVerified = FirebaseAuth.getInstance().currentUser?.isEmailVerified == true
                
                if (!isVerified) {
                    Button(
                        onClick = {
                            if (!isVerifyingEmail) {
                                isVerifyingEmail = true
                                
                                FirebaseAuth.getInstance().currentUser?.let { user ->
                                    user.sendEmailVerification()
                                        .addOnCompleteListener { task ->
                                            isVerifyingEmail = false
                                            if (task.isSuccessful) {
                                                Toast.makeText(context, "Verification email sent", Toast.LENGTH_SHORT).show()
                                                showVerifyAccountDialog = false
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Error sending verification email: ${task.exception?.message ?: ""}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                } ?: run {
                                    isVerifyingEmail = false
                                    Toast.makeText(context, "Error: User is not logged in", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        enabled = !isVerifyingEmail && !isRefreshingVerification
                    ) {
                        if (isVerifyingEmail) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(text = "Send Verification Email")
                        }
                    }
                } else {
                    TextButton(onClick = { showVerifyAccountDialog = false }) {
                        Text(text = "Close")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showVerifyAccountDialog = false 
                }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: String,
    themeManager: ThemeManager,
    onDismiss: () -> Unit,
    onThemeChanged: () -> Unit // Add callback for immediate theme change
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
                        themeManager.saveThemePreference("system")
                        onThemeChanged() // Trigger theme change
                        onDismiss()
                    }
                )
                ThemeOption(
                    text = stringResource(id = R.string.dark_mode_light),
                    selected = currentTheme == "light",
                    icon = Icons.Default.LightMode,
                    onClick = {
                        themeManager.saveThemePreference("light")
                        onThemeChanged() // Trigger theme change
                        onDismiss()
                    }
                )
                ThemeOption(
                    text = stringResource(id = R.string.dark_mode_dark),
                    selected = currentTheme == "dark",
                    icon = Icons.Default.DarkMode,
                    onClick = {
                        themeManager.saveThemePreference("dark")
                        onThemeChanged() // Trigger theme change
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