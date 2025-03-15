package com.forrestgump.ig.ui.screens.addPost

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import androidx.core.content.FileProvider
import androidx.compose.foundation.shape.CircleShape
import com.forrestgump.ig.R
import java.io.File
import android.widget.Toast
import com.forrestgump.ig.ui.navigation.Routes


@Composable
fun AddPostScreen(navHostController: NavHostController) {
    val context = LocalContext.current

    // State lưu các ảnh được chọn (từ thư viện hoặc camera)
    val selectedImages = remember { mutableStateListOf<Uri>() }
    // State lưu danh sách ảnh trong thư viện (load sẵn khi mở màn hình)
    val galleryImages = remember { mutableStateListOf<Uri>() }
    var permissionGranted by remember { mutableStateOf(false) }

    // Launcher yêu cầu quyền truy cập ảnh từ thư viện
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (isGranted) {
            galleryImages.clear()
            galleryImages.addAll(loadGalleryImages(context))
        }
    }

    // Kiểm tra và yêu cầu quyền khi mở màn hình
    LaunchedEffect(Unit) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE
        requestPermissionLauncher.launch(permission)
    }

    // Launcher cho chọn nhiều ảnh từ thư viện
    val multipleImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.size > 10) {
            Toast.makeText(context, "Chỉ được chọn tối đa 10 ảnh", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        selectedImages.clear()
        selectedImages.addAll(uris)
    }


    // Launcher cho chọn 1 ảnh từ thư viện (khi nhấn vào ảnh trong lưới)
    val singleImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImages.clear()
            selectedImages.add(it)
        }
    }

    // Launcher cho chụp ảnh từ camera
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && photoUri != null) {
            selectedImages.clear()
            selectedImages.add(photoUri!!)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar: bên trái nút "X", giữa chữ "Bài viết mới", bên phải nút "Tiếp"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navHostController.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close), // icon dấu X
                    contentDescription = "Back"
                )
            }
            Text(text = "Bài viết mới", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = {
                if (selectedImages.isEmpty()) {
                    Toast.makeText(context, "Vui lòng chọn hoặc chụp ảnh", Toast.LENGTH_SHORT).show()
                    return@TextButton
                }
                navHostController.navigate(Routes.AddPostDetailScreen.route)
            }) {
                Text(text = "Tiếp")
            }

        }

        // Phần hiển thị ảnh (50% màn hình trên)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .background(Color.LightGray)
        ) {
            if (selectedImages.isEmpty()) {
                // Hiển thị placeholder nếu chưa có ảnh được chọn
                Text("Chưa chọn ảnh", modifier = Modifier.align(Alignment.Center))
            } else {
                // Dùng HorizontalPager cho trải nghiệm lướt mượt
                val pagerState = rememberPagerState(initialPage = 0) { selectedImages.size }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    AsyncImage(
                        model = selectedImages[page],
                        contentDescription = "Ảnh đã chọn",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                // Dot indicator hiển thị vị trí ảnh hiện tại (ví dụ: 1/3)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until pagerState.pageCount) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (i == pagerState.currentPage) Color.White else Color.Gray
                                )
                        )
                    }
                }
            }
        }

        // Phần dưới (50% màn hình dưới)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
        ) {
            // Dòng chứa nút "Chọn nhiều" và icon camera (căn bên phải)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { multipleImagePickerLauncher.launch("image/*") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_multiple), // icon cho "Chọn nhiều"
                        contentDescription = "Chọn nhiều",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Chọn nhiều")
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    // Tạo file ảnh tạm và sử dụng FileProvider để lấy URI
                    val file = createImageFile(context)
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    photoUri = uri
                    cameraLauncher.launch(uri)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera), // icon camera
                        contentDescription = "Chụp ảnh",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Lưới ảnh từ thư viện (load sẵn khi mở màn hình)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(galleryImages) { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Ảnh thư viện",
                        modifier = Modifier
                            .padding(4.dp)
                            .aspectRatio(1f)
                            .clickable {
                                // Khi nhấn vào ảnh trong lưới, chọn ảnh đó làm ảnh hiển thị ở trên
                                selectedImages.clear()
                                selectedImages.add(uri)
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

// Hàm load danh sách ảnh từ thư viện sử dụng MediaStore
fun loadGalleryImages(context: Context): List<Uri> {
    val imageList = mutableListOf<Uri>()
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
    context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        sortOrder
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val contentUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
            imageList.add(contentUri)
        }
    }
    return imageList
}

// Hàm tạo file ảnh tạm thời cho camera sử dụng FileProvider
fun createImageFile(context: Context): File {
    val storageDir = context.cacheDir
    return File.createTempFile("temp_image_", ".jpg", storageDir)
}
