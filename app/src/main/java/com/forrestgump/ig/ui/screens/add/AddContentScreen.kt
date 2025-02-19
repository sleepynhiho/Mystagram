package com.forrestgump.ig.ui.screens.add

import android.Manifest
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun AddContentScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val imageUris = remember { mutableStateListOf<Uri>() }

    // Launcher để chọn nhiều ảnh
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        imageUris.clear()
        imageUris.addAll(uris)
    }

    // Launcher yêu cầu quyền truy cập ảnh
    val requestPermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val granted = results.values.all { it }
        if (granted) {
            imagePickerLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Bạn cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (imageUris.isNotEmpty()) {
            LazyRow(modifier = Modifier.padding(16.dp)) {
                items(imageUris.size) { index ->
                    Image(
                        painter = rememberAsyncImagePainter(imageUris[index]),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .size(100.dp)
                            .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
                            .padding(4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                    imagePickerLauncher.launch("image/*")
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    requestPermissions.launch(
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                    )
                }
                else -> {
                    requestPermissions.launch(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    )
                }
            }
        }) {
            Text("Chọn ảnh")
        }
    }

    BackHandler {
        onBackClick()
    }
}

@Preview
@Composable
private fun AddContentScreenPreview() {
    AddContentScreen(
        onBackClick = { }
    )
}
