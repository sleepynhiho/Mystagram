package com.forrestgump.ig.ui.screens.auth

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val englishText = stringResource(id = R.string.english_language)
    val vietnamText = stringResource(id = R.string.vietnamese_language)
    val errorMessageTemplate = stringResource(id = R.string.error_message)
    var selectedLanguage by remember { mutableStateOf("") }
    selectedLanguage = stringResource(id = R.string.language_default)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .background(MainBackground),
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
                    color = onSurface
                )
                Icon(
                    painter = painterResource(id = R.drawable.next),
                    contentDescription = stringResource(id = R.string.language_dropdown),
                    tint = onSurface,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .rotate(90F)
                        .height(12.dp)
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = englishText) },
                    onClick = {
                        selectedLanguage = englishText
                        changeAppLanguage(context, "en")
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = vietnamText) },
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
                authViewModel.login(email, password) { success, errorMsg ->
                    if (success) {
                        navController.navigate(Routes.InnerContainer.route) {
                            popUpTo(Routes.LoginScreen.route) { inclusive = true }
                        }
                    } else {
                        message = errorMessageTemplate.format(errorMsg)
                    }
                }
            },
            enabled = email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0095F6)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.login),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (message.isNotEmpty()) {
            Text(text = message, color = Color.Red, fontSize = 14.sp)
        }

        // *** Thêm nút Login with Google ***
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = { /* handle google login logic */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
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
                color = onSurface
            )
        }

        // *** Thêm nút Login with Facebook ***
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = { /* handle facebook login logic */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            // Icon Facebook
            Icon(
                painter = painterResource(id = R.drawable.facebook_logo),
                contentDescription = stringResource(id = R.string.facebook_logo_desc),
                tint = Color.Unspecified,
                modifier = Modifier.height(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.login_with_facebook),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Forgot password
        Text(
            text = stringResource(id = R.string.forgot_password),
            fontSize = 14.sp,
            color = Color(0xFF29649B),
            modifier = Modifier.clickable {
                // Xử lý logic forgot password nếu cần
            }
        )

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
