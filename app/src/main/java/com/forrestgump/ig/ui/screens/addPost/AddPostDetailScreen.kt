package com.forrestgump.ig.ui.screens.addPost

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Divider
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
import com.forrestgump.ig.R
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalFocusManager
import android.content.Context
import android.net.Uri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import java.io.File
import java.io.FileOutputStream
import com.forrestgump.ig.data.models.Post
import com.forrestgump.ig.ui.navigation.Routes
import com.google.firebase.firestore.FirebaseFirestore
import com.forrestgump.ig.ui.viewmodels.UserViewModel
import android.os.Handler
import android.os.Looper
import com.forrestgump.ig.BuildConfig

@Composable
fun AddPostDetailScreen(
    navHostController: NavHostController,
    userViewModel: UserViewModel = hiltViewModel()  // Inject UserViewModel
) {
    val currentUser = userViewModel.user.collectAsState().value
    val context = LocalContext.current

    // State cho caption: ban đầu hiển thị placeholder, khi nhấn chuyển thành TextField
    var caption by remember { mutableStateOf("") }
    var isEditingCaption by remember { mutableStateOf(false) }

    // Cách tốt hơn
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val parentEntry = remember(navBackStackEntry) {
        navHostController.getBackStackEntry(Routes.AddPostScreen.route)
    }
    // Sử dụng cùng một instance của AddPostViewModel
    val addPostViewModel: AddPostViewModel = hiltViewModel(parentEntry)

    // Lấy danh sách ảnh từ ViewModel
    val selectedImages = addPostViewModel.selectedImages.collectAsState().value

    // Pager state cho HorizontalPager (sử dụng thư viện compose foundation pager)
    val pagerState = rememberPagerState(initialPage = 0) { selectedImages.size }
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier
        .fillMaxSize()
        .clickable { focusManager.clearFocus() }
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navHostController.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close), // icon dấu X (đảm bảo có resource này)
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Bài viết mới",
                style = MaterialTheme.typography.titleMedium
            )
            // Để cân bằng layout, nếu không có nút bên phải có thể dùng Spacer
            Spacer(modifier = Modifier.width(48.dp))
        }

        // Phần hiển thị ảnh (chiếm khoảng 30% màn hình)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(10.0f)
                .background(Color.LightGray)
        ) {
            if (selectedImages.isEmpty()) {
                Text("Chưa chọn ảnh", modifier = Modifier.align(Alignment.Center))
            } else {
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
                        contentScale = ContentScale.Fit  // Sử dụng ContentScale.Fit để hiển thị đầy đủ ảnh
                    )
                }
                // Dot indicator
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

        // Row hiển thị "Thêm chú thích...
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(120.dp) // Chiều cao cố định
                .verticalScroll(rememberScrollState())
                .clickable { /* Không làm gì, giữ focus cho TextField */ }
        ) {
            TextField(
                value = caption,
                onValueChange = { caption = it },
                placeholder = { Text("Thêm chú thích...") },
                modifier = Modifier.fillMaxSize(),
                maxLines = 5 // Giới hạn số dòng hiển thị
            )
        }

        Divider(color = Color.LightGray, thickness = 1.dp)

        // Row "Gắn thẻ người khác"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_tag), // icon dành cho "Gắn thẻ người khác"
                    contentDescription = "Tag Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gắn thẻ người khác")
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right), // icon (hoặc mũi tên) bên phải
                contentDescription = "Next Icon",
                modifier = Modifier.size(24.dp)
            )
        }

        // Row "Thêm vị trí"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_location), // icon vị trí
                    contentDescription = "Location Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thêm vị trí")
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = "Next Icon",
                modifier = Modifier.size(24.dp)
            )
        }

        // Row "Đối tượng" với bên phải hiển thị "Người theo dõi" và icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_audience), // icon đối tượng (placeholder)
                    contentDescription = "Audience Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đối tượng")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Người theo dõi")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "Next Icon",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Nút "Chia sẻ"
        Button(
            onClick = {
                // Xử lý chia sẻ bài viết (ở đây ví dụ hiển thị Toast)
                if (selectedImages.isEmpty()) {
                    Toast.makeText(context, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (currentUser == null) {
                    Toast.makeText(context, "User chưa đăng nhập", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Gọi hàm upload post sử dụng Cloudinary (hoặc Firebase Storage nếu thay đổi)
                uploadPostToFirebaseUsingCloudinary(
                    context = context,
                    selectedImages = selectedImages,
                    caption = caption,
                    userId = currentUser.userId,
                    username = currentUser.username,
                    profileImageUrl = currentUser.profileImage,
                    onSuccess = {
                        Toast.makeText(context, "Đăng bài thành công", Toast.LENGTH_SHORT).show()
                        navHostController.navigate(Routes.HomeScreen.route) {
                            popUpTo(Routes.HomeScreen.route) { inclusive = true }
                        }
                    },
                    onFailure = { exception ->
                        Toast.makeText(context, "Lỗi: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(48.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4))
        ) {
            Text("Chia sẻ", color = Color.White)
        }
    }
}

fun getFileFromUri(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
    inputStream.use { input ->
        FileOutputStream(tempFile).use { output ->
            input?.copyTo(output)
        }
    }
    return tempFile
}

fun uploadImageToCloudinary(
    context: Context,
    fileUri: Uri,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    // Cấu hình Cloudinary dùng unsigned upload
    val config = hashMapOf(
        "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
        "api_key" to BuildConfig.CLOUDINARY_API_KEY,
        "upload_preset" to BuildConfig.CLOUDINARY_UPLOAD_PRESET // hoặc nếu bạn định nghĩa qua BuildConfig: BuildConfig.CLOUDINARY_UPLOAD_PRESET
    )
    val cloudinary = Cloudinary(config)
    val file = getFileFromUri(context, fileUri)

    // Chạy upload trong background (có thể dùng coroutine thay cho Thread)
    Thread {
        try {
            // Upload ảnh và nhận về kết quả dưới dạng Map
            val result = cloudinary.uploader().unsignedUpload(
                file,
                BuildConfig.CLOUDINARY_UPLOAD_PRESET,
                ObjectUtils.emptyMap()
            )
            val secureUrl = result["secure_url"] as String
            onSuccess(secureUrl)
        } catch (e: Exception) {
            // Chuyển sang Main thread để hiển thị Toast
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                onFailure(e)
            }
        }
    }.start()
}

fun uploadPostToFirebaseUsingCloudinary(
    context: Context,
    selectedImages: List<Uri>,
    caption: String,
    userId: String,
    username: String,
    profileImageUrl: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val mediaUrls = mutableListOf<String>()
    var uploadCount = 0

    if (selectedImages.isEmpty()) {
        onFailure(Exception("Chưa có ảnh để upload"))
        return
    }

    // Upload từng ảnh lên Cloudinary
    for (uri in selectedImages) {
        uploadImageToCloudinary(context, uri, { imageUrl ->
            synchronized(mediaUrls) {
                mediaUrls.add(imageUrl)
                uploadCount++
                // Sau khi upload hết tất cả ảnh
                if (uploadCount == selectedImages.size) {
                    // Tạo đối tượng Post và lưu vào Firestore
                    val db = FirebaseFirestore.getInstance()
                    val postId = db.collection("posts").document().id
                    val post = Post(
                        postId = postId,
                        userId = userId,
                        username = username,
                        profileImageUrl = profileImageUrl,
                        mediaUrls = mediaUrls,
                        caption = caption,
                        reactions = emptyMap(),
                        commentsCount = 0,
                        mimeType = "image",
                        timestamp = null  // @ServerTimestamp sẽ được Firestore set tự động
                    )
                    db.collection("posts").document(postId)
                        .set(post)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onFailure(e) }
                }
            }
        }, { exception ->
            onFailure(exception)
        })
    }
}



