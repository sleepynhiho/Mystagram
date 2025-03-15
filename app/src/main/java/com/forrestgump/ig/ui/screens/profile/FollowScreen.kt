package com.forrestgump.ig.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.forrestgump.ig.data.models.User


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowScreen(
    navController: NavController,
    userName: String,                // Tên của người dùng được hiển thị ở top bar
    followers: List<User>,       // Danh sách người theo dõi, sắp xếp từ mới nhất đến cũ nhất
    onBack: () -> Unit,              // Callback khi nhấn nút back
    onDeleteFollower: (User) -> Unit,  // Callback khi nhấn nút xóa của một người theo dõi
    headerText: String = "Người theo dõi", // Nhãn hiển thị cho danh sách (có thể thay đổi thành "Đang theo dõi")
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = userName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Hiển thị thông tin "Người theo dõi: <số lượng>"
            Text(
                text = "${headerText}: ${followers.size}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color.LightGray
            )
            // Hiển thị danh sách người theo dõi
            LazyColumn {
                items(items = followers) { follower ->
                    FollowerRow(
                        follower = follower,
                        onDelete = { onDeleteFollower(follower) }
                    )
                }
            }
        }
    }
}

@Composable
fun FollowerRow(
    follower: User,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar hình tròn
        Image(
            painter = rememberAsyncImagePainter(model = follower.profileImage),
            contentDescription = follower.username,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Cột chứa tên và nickname (tên ở trên, nickname ở dưới) cùng căn chỉnh theo hàng ngang với avatar
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = follower.fullName,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = follower.username,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Nút xóa bên phải cho phép xóa người theo dõi
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete follower"
            )
        }
    }
}
