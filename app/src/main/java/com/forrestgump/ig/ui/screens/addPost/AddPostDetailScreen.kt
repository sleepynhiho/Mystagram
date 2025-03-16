// File: AddPostDetailScreen.kt
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.forrestgump.ig.R
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource

@Composable
fun AddPostDetailScreen(
    navHostController: NavHostController,
    // Giả sử danh sách ảnh được truyền từ AddPostScreen (có thể dùng ViewModel hay NavArgs)
    selectedImages: List<android.net.Uri>,
) {
    val context = LocalContext.current

    // State cho caption: ban đầu hiển thị placeholder, khi nhấn chuyển thành TextField
    var caption by remember { mutableStateOf("") }
    var isEditingCaption by remember { mutableStateOf(false) }

    // Pager state cho HorizontalPager (sử dụng thư viện compose foundation pager)
    val pagerState = rememberPagerState(initialPage = 0) { selectedImages.size }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
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
                text = stringResource(id = R.string.new_post),
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
                Text(stringResource(id = R.string.no_select_photo), modifier = Modifier.align(Alignment.Center))
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
                Toast.makeText(context, "Đã chia sẻ", Toast.LENGTH_SHORT).show()
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
