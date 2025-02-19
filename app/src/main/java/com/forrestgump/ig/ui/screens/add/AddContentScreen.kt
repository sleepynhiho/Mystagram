package com.forrestgump.ig.ui.screens.add

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.forrestgump.ig.R
import com.forrestgump.ig.ui.screens.add.components.AddStoryTopBar

@Composable
fun AddContentScreen(navHostController: NavHostController) {
    val imageUris = remember { mutableStateListOf<Uri>() }
    var permissionGranted by remember { mutableStateOf(false) }

    // Chọn 1 ảnh
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUris.clear()
        uri?.let { imageUris.add(it) }
    }

    // Yêu cầu quyền
    val requestPermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        permissionGranted = results.values.all { it }
    }

    // Kiểm tra quyền khi mở màn hình
    LaunchedEffect(Unit) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                imagePickerLauncher.launch("image/*")
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                requestPermissions.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
            }

            else -> {
                requestPermissions.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            }
        }
    }

    // Khi có quyền thì mở chọn ảnh
    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            imagePickerLauncher.launch("image/*")
        }
    }

    if (imageUris.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            AddStoryTopBar(onBackClicked = {
                imageUris.clear()
                imagePickerLauncher.launch("image/*")
            })

            AsyncImage(
                model = imageUris[0],
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxSize()
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(38.dp)
                        .padding(5.dp)
                        .shadow(2.dp, shape = CircleShape)
                        .background(color = Color.White, shape = CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.next),
                        tint = Color.Black,
                        contentDescription = "Post",
                        modifier = Modifier
                            .padding(5.dp)
                            .background(color = Color.White, shape = CircleShape)
                    )
                }
            }

        }
    }

    BackHandler {
        if (imageUris.isNotEmpty()) {
            imageUris.clear()
            imagePickerLauncher.launch("image/*")
        } else {
            navHostController.popBackStack()
        }
    }
}

@Preview
@Composable
private fun AddContentScreenPreview() {
    AddContentScreen(navHostController = rememberNavController())
}
