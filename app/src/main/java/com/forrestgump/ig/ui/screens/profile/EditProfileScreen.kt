package com.forrestgump.ig.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    navController: NavController,
) {
    // State quản lý giá trị nhập vào, khi tích hợp backend bạn sẽ update theo dữ liệu thực
    val uiState by viewModel.uiState.collectAsState()
    var newProfileImage by remember { mutableStateOf(uiState.curUser.profileImage) }
    var newFullName by remember { mutableStateOf(uiState.curUser.fullName) }
    var newUsername by remember { mutableStateOf(uiState.curUser.username) }
    var newBio by remember { mutableStateOf(uiState.curUser.bio) }
    var newStateOfPremium by remember { mutableStateOf(uiState.curUser.isPremium) }
    var newAccountPrivacy by remember { mutableStateOf(uiState.curUser.isPrivate) }
    val focusManager = LocalFocusManager.current
    val selectedImageUri = remember { mutableStateOf<String?>(null) }
    val showChooseImageDialog = remember { mutableStateOf(false) }
    val photoFile = remember { mutableStateOf<File?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri.value = uri?.toString()
    }
    val context = LocalContext.current
    // Chụp ảnh từ camera
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoFile.value?.let { file ->
                selectedImageUri.value = file.toUri().toString()
            }
        }
    }

    // Đây là launcher xin quyền
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Được quyền - tiến hành chụp ảnh
            val newFile = createImageFile(context)
            photoFile.value = newFile
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                newFile
            )
            takePictureLauncher.launch(uri)
        } else {
            // User từ chối quyền
            Toast.makeText(context, "Bạn cần cấp quyền Camera để chụp ảnh mới.", Toast.LENGTH_LONG).show()
        }
    }

    // Đồng bộ newProfileImage khi người dùng chọn ảnh mới
    LaunchedEffect(selectedImageUri.value) {
        selectedImageUri.value?.let { uri ->
            newProfileImage = uri
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Chỉnh sửa trang cá nhân",
                        fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Button "Lưu" màu xanh, hình chữ nhật bo tròn 4 góc
            Button(
                onClick = {
                    focusManager.clearFocus() // Loại bỏ focus khi nhấn nút lưu
                    Log.d("EditProfileScreen", "${newProfileImage}")
                    viewModel.updateUserProfile(
                        context = context,
                        newProfileImage = newProfileImage,
                        newFullName = newFullName,
                        newUsername = newUsername,
                        newBio = newBio,
                        newAccountPrivacy = newAccountPrivacy,
                        onSuccess = {
                            // Sau khi cập nhật thành công, có thể navigate back hoặc show thông báo
                            navController.popBackStack()
                        },
                        onFailure = { exception ->
                            // Hiển thị thông báo lỗi
                            Log.e("EditProfileScreen", "Error updating profile", exception)
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(4.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF00BCD4), Color(0xFF2196F3))
                        ),
                        shape = RoundedCornerShape(40.dp)
                    ),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Text(
                    text = "Lưu",
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar: căn giữa và có thể nhấn để đổi avatar
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.LightGray, CircleShape) // viền
                        .shadow(4.dp, CircleShape)                   // đổ bóng nhẹ
                        .clickable { showChooseImageDialog.value = true },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = selectedImageUri.value
                                ?: newProfileImage // Nếu chưa chọn ảnh mới, dùng ảnh từ newProfileImage
                        ),

                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }


                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Chỉnh sửa ảnh hoặc avatar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Blue,
                    fontSize = 16.sp,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Ô nhập liệu cho "Tên"
                EditField(
                    label = "Tên",
                    text = newFullName,
                    onTextChange = { newFullName = it }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Ô nhập liệu cho "Tên người dùng"
                EditField(
                    label = "Tên người dùng",
                    text = newUsername,
                    onTextChange = { newUsername = it }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Ô nhập liệu cho "Tiểu sử"
                EditField(
                    label = "Tiểu sử",
                    text = newBio,
                    onTextChange = { newBio = it }
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Premium Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                        .clickable { /* TODO: Handle premium click */ }
                ) {
                    Text(
                        text = if (newStateOfPremium) "Hủy gói premium" else "Chuyển thành tài khoản premium",
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Privacy Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Account privacy",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "Account privacy", color = Color.Black, fontSize = 15.sp)
                        }
                        Switch(
                            checked = newAccountPrivacy,
                            onCheckedChange = { newValue -> newAccountPrivacy = newValue },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color.Gray,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.DarkGray
                            )
                        )
                    }
                }

                // Spacer tạo khoảng cách
                Spacer(modifier = Modifier.height(24.dp))

                // Location Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                        .clickable { /* TODO: Handle location click */ }
                ) {
                    Text(
                        text = "Chỉnh sửa vị trí",
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }


                if (showChooseImageDialog.value) {
                    AlertDialog(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onDismissRequest = { showChooseImageDialog.value = false },
                        title = {
                            Text(
                                text = "Chọn ảnh đại diện",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        text = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally  // căn giữa nội dung
                            ) {
                                TextButton(onClick = {
                                    showChooseImageDialog.value = false
                                    pickImageLauncher.launch("image/*")
                                }) {
                                    Text("Chọn từ thư viện")
                                }
                                Spacer(modifier = Modifier.height(18.dp))
                                TextButton(onClick = {
                                    showChooseImageDialog.value = false
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                        val newFile = createImageFile(context)
                                        photoFile.value = newFile
                                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", newFile)
                                        takePictureLauncher.launch(uri)
                                    } else {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }) {
                                    Text("Chụp ảnh mới")
                                }
                            }
                        },
                        confirmButton = {}
                    )
                }

            }
        }
    }
}

@Composable
fun EditField(
    label: String,
    text: String,
    onTextChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Label phía trên ô nhập liệu
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        // Ô nhập liệu với viền màu xám nhẹ và bo tròn 4 góc
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)),
            shape = RoundedCornerShape(4.dp)
        )
    }
}

fun createImageFile(context: Context): File {
    val storageDir = context.cacheDir
    return File.createTempFile(
        "avatar_${System.currentTimeMillis()}",
        ".jpg",
        storageDir
    )
}
