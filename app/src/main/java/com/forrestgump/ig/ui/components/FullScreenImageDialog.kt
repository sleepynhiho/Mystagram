package com.forrestgump.ig.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.forrestgump.ig.R
import androidx.compose.ui.geometry.Offset

@Composable
fun FullScreenImageDialog(
    imageUrl: String,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        // Giới hạn cho scale và offset
        val minScale = 1f
        val maxScale = 3f
        val maxPanX = 300f
        val maxPanY = 300f

        var scale by remember { mutableStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                // Sử dụng pointerInput để xử lý gesture zoom và pan
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        // Giới hạn zoom
                        scale = (scale * zoom).coerceIn(minScale, maxScale)

                        // Tính toán offset mới và giới hạn nó
                        val newOffset = offset + pan
                        offset = Offset(
                            x = newOffset.x.coerceIn(-maxPanX, maxPanX),
                            y = newOffset.y.coerceIn(-maxPanY, maxPanY)
                        )
                    }
                }
        ) {
            // Hiển thị ảnh với hiệu ứng zoom và pan
            AsyncImage(
                model = imageUrl,
                contentDescription = "Full Screen Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }
            )

            // Nút đóng dialog ở góc trên bên phải
            IconButton(
                onClick = onDismissRequest,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close), // đảm bảo bạn có icon này trong resources
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }
}

