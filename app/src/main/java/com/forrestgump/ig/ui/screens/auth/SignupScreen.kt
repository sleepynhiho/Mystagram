package com.forrestgump.ig.ui.screens.auth

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { /* Xử lý quay lại */ }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Account registration",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Enter your email and password to create an account.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Email input field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color.LightGray,
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5)
                ),
                singleLine = true
            )

            // Password input field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color.LightGray,
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5)
                ),
                singleLine = true
            )

            // Confirm password input field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm the password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color.LightGray,
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5)
                ),
                singleLine = true
            )

            // Signup button
            Button(
                onClick = { /* Xử lý đăng ký */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0095F6)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Register",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            // Spacer đẩy liên kết "Tôi đã có tài khoản" xuống dưới
            Spacer(modifier = Modifier.weight(1f))

            // Already have account link
            TextButton(
                onClick = { /* Chuyển sang màn hình đăng nhập */ },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "I already have an account",
                    color = Color(0xFF0095F6),
                    fontSize = 14.sp
                )
            }
        }
    }
}
