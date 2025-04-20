package com.forrestgump.ig.ui.screens.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.forrestgump.ig.R
import com.forrestgump.ig.ui.navigation.Routes
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.forrestgump.ig.utils.constants.Utils.onSurface
import com.forrestgump.ig.utils.constants.changeAppLanguage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val englishText = stringResource(id = R.string.english_language)
    val vietnamText = stringResource(id = R.string.vietnamese_language)
    val errorMessageTemplate = stringResource(id = R.string.error_message)
    val emailResetTemplate = stringResource(id = R.string.email_reset_sent)
    var selectedLanguage by remember { mutableStateOf("") }
    selectedLanguage = stringResource(id = R.string.language_default)

    var isLoading by remember { mutableStateOf(false) }
    var isGoogleLoading by remember { mutableStateOf(false) }
    var isResetLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var resetMessage by remember { mutableStateOf("") }

    // Configure Google Sign In
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    
    val googleSignInClient = remember {
        try {
            GoogleSignIn.getClient(context, gso)
        } catch (e: Exception) {
            // Log the error for debugging
            Toast.makeText(context, "Google Sign-In config error: ${e.message}", Toast.LENGTH_LONG).show()
            // Return a default empty client that won't be used
            null
        }
    }
    
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleGoogleSignInResult(task, authViewModel, navController) { isLoading, errorMsg ->
                isGoogleLoading = isLoading
                if (errorMsg != null) {
                    message = errorMessageTemplate.format(errorMsg)
                }
            }
        } else {
            isGoogleLoading = false
            message = errorMessageTemplate.format("Google sign-in was cancelled")
        }
    }

    // Function to perform Google sign-in
    fun startGoogleSignIn() {
        if (!isGoogleLoading) {
            isGoogleLoading = true
            googleSignInClient?.let { client ->
                // Sign out from Google first to ensure account picker is shown
                client.signOut().addOnCompleteListener {
                    // Create a new sign-in intent with account picker forced
                    val signInIntent = client.signInIntent.apply {
                        // Force account picker dialog
                        putExtra("account_selection_required", true)
                        putExtra("prompt", "select_account")
                    }
                    googleSignInLauncher.launch(signInIntent)
                }
            } ?: run {
                isGoogleLoading = false
                message = errorMessageTemplate.format("Google Sign-In not configured properly")
                Toast.makeText(context, "Google Sign-In not configured properly", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status bar space
        Spacer(modifier = Modifier.height(48.dp))

        // Language selector
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.clickable { expanded = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedLanguage,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Icon(
                    painter = painterResource(id = R.drawable.next),
                    contentDescription = stringResource(id = R.string.language_dropdown),
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .rotate(90F)
                        .height(12.dp)
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                DropdownMenuItem(
                    text = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = englishText)
                        }
                    },
                    onClick = {
                        selectedLanguage = englishText
                        changeAppLanguage(context, "en")
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = vietnamText)
                        }
                    },
                    onClick = {
                        selectedLanguage = vietnamText
                        changeAppLanguage(context, "vi")
                        expanded = false
                    }
                )
            }

        }

        Spacer(modifier = Modifier.height(4.dp))
        Image(
            painter = painterResource(id = R.drawable.my_logo),
            contentDescription = stringResource(id = R.string.app_logo),
            modifier = Modifier
                .height(96.dp)
                .padding(vertical = 8.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text(text = stringResource(id = R.string.email)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text(text = stringResource(id = R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.trim().isNotBlank() && password.isNotBlank()) {
                    isLoading = true
                    authViewModel.login(email, password) { success, errorMsg ->
                        isLoading = false
                        if (success) {
                            navController.navigate(Routes.InnerContainer.route) {
                                popUpTo(Routes.LoginScreen.route) { inclusive = true }
                            }
                        } else {
                            message = errorMessageTemplate.format(errorMsg)
                        }
                    }
                }
            },
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0095F6)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp)
                )
            } else {
                Text(
                    text = stringResource(id = R.string.login),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (message.isNotEmpty()) {
            Text(text = message, color = Color.Red, fontSize = 14.sp)
        }

        // Google Login Button
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = { startGoogleSignIn() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            enabled = !isGoogleLoading
        ) {
            if (isGoogleLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp)
                )
            } else {
                // Icon Google
                Icon(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = stringResource(id = R.string.google_logo_desc),
                    tint = Color.Unspecified,
                    modifier = Modifier.height(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.login_with_google),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Forgot password
        Text(
            text = stringResource(id = R.string.forgot_password),
            fontSize = 14.sp,
            color = Color(0xFF29649B),
            modifier = Modifier.clickable {
                showDialog = true
            }
        )
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(stringResource(id = R.string.forgot_password)) },
                text = {
                    Column {
                        Text(stringResource(id = R.string.enter_email_password_reset))
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            placeholder = { Text(stringResource(id = R.string.email)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (resetMessage.isNotEmpty()) {
                            Text(text = resetMessage, color = Color.Red, fontSize = 12.sp)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (resetEmail.isNotBlank() && !isResetLoading) {
                            isResetLoading = true
                            authViewModel.forgotPassword(context, resetEmail) { success, errorMsg ->
                                isResetLoading = false
                                resetMessage = if (success) emailResetTemplate else errorMsg
                                    ?: "Error occurred."
                            }
                        }
                    }) {
                        if (isResetLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(24.dp)
                            )
                        } else {
                            Text(stringResource(id = R.string.sent_request))
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(stringResource(id = R.string.cancel))
                    }
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Create new account button
        OutlinedButton(
            onClick = { navController.navigate(Routes.SignupScreen.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(1.dp, Color(0xFF0095F6)),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent
            )
        ) {
            Text(
                text = stringResource(id = R.string.create_new_account),
                fontSize = 14.sp,
                color = Color(0xFF0095F6)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Meta logo
        Image(
            painter = painterResource(id = R.drawable.meta_colored),
            contentDescription = stringResource(id = R.string.meta_logo),
            modifier = Modifier.padding(vertical = 8.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(48.dp))
    }
}

private fun handleGoogleSignInResult(
    completedTask: Task<GoogleSignInAccount>,
    authViewModel: AuthViewModel,
    navController: NavController,
    callback: (Boolean, String?) -> Unit
) {
    try {
        val account = completedTask.getResult(ApiException::class.java)
        
        // Google Sign In was successful, authenticate with Firebase
        val idToken = account.idToken
        if (idToken == null) {
            callback(false, "Failed to get ID token from Google")
            return
        }
        
        authViewModel.signInWithGoogle(idToken) { success, errorMsg ->
            if (success) {
                navController.navigate(Routes.InnerContainer.route) {
                    popUpTo(Routes.LoginScreen.route) { inclusive = true }
                }
                callback(false, null) // Set isLoading to false
            } else {
                callback(false, errorMsg ?: "Unknown error during Firebase authentication")
            }
        }
    } catch (e: ApiException) {
        // Get detailed error message based on status code
        val errorMessage = when (e.statusCode) {
            12500 -> "Current Google Play Services version is too old"
            12501 -> "User cancelled sign-in flow"
            12502 -> "Google Play Services installation cancelled by user"
            7 -> "Network error occurred"
            8 -> "Internal error occurred"
            16 -> "API key is invalid"
            5 -> "Invalid account"
            else -> "Google sign in failed: ${e.statusCode} - ${e.message}"
        }
        callback(false, errorMessage)
    } catch (e: Exception) {
        callback(false, "Unexpected error: ${e.message}")
    }
}
