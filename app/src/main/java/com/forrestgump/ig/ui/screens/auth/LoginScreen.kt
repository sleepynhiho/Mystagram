package com.forrestgump.ig.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forrestgump.ig.R

@Composable
fun LoginScreen() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status bar space
        Spacer(modifier = Modifier.height(48.dp))

        // Language selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "English (US)",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Icon(
                painter = painterResource(id = R.drawable.next),
                contentDescription = "Language dropdown",
                tint = Color.Gray,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .rotate(90F)
                    .height(12.dp)
            )
        }

        // Instagram logo
        Spacer(modifier = Modifier.height(64.dp))
        Image(
            painter = painterResource(id = R.drawable.my_logo),
            contentDescription = "Meta logo",
            modifier = Modifier
                .height(96.dp)
                .padding(vertical = 8.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(64.dp))

        // Username field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            placeholder = { Text("Username, email or mobile number") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
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

        // Nút Log in
        Button(
            onClick = { /* Login logic */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0095F6)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Log in",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
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
                contentDescription = "Google Logo",
                tint = Color.Unspecified, // để giữ nguyên màu gốc icon
                modifier = Modifier.height(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Login with Google",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
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
                contentDescription = "Facebook Logo",
                tint = Color.Unspecified,
                modifier = Modifier.height(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Login with Facebook",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Forgot password
        Text(
            text = "Forgot password?",
            fontSize = 14.sp,
            color = Color(0xFF00376B),
            modifier = Modifier.clickable { /* Forgot password logic */ }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Create new account button
        OutlinedButton(
            onClick = { /* Create account logic */ },
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
                text = "Create new account",
                fontSize = 14.sp,
                color = Color(0xFF0095F6)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Meta logo
        Image(
            painter = painterResource(id = R.drawable.meta_colored),
            contentDescription = "Meta logo",
            modifier = Modifier
                .padding(vertical = 8.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(48.dp))
    }
}
