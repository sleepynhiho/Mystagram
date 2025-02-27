package com.forrestgump.ig.ui.screens.add


import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.forrestgump.ig.R
import com.forrestgump.ig.ui.screens.add.components.AddStoryTopBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@Composable
fun AddContentScreen(navHostController: NavHostController) {
    val imageUris = remember { mutableStateListOf<Uri>() }
    var permissionGranted by remember { mutableStateOf(false) }
    val context = LocalContext.current

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

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }

    if (imageUris.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            TransformableImageBox(
                uri = imageUris[0].toString(),
                onBitmapReady = { loadedBitmap ->
                    bitmap = loadedBitmap
                },
                onTransformChange = { newScale, newOffset, newRotation ->
                    scale = newScale
                    offset = newOffset
                    rotationAngle = newRotation
                }
            )

            AddStoryTopBar(onBackClicked = {
                imageUris.clear()
                imagePickerLauncher.launch("image/*")
            })

            var showSaveScreen by remember { mutableStateOf(false) }
            // Bottom bar
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { showSaveScreen = true },
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

                if (showSaveScreen) {
                    bitmap?.let { originalBitmap ->
                        val transformedBitmap = createTransformedBitmap(
                            originalBitmap, scale, offset, rotationAngle,
                            screenWidth.toInt(), screenHeight.toInt()
                        )

                        val scope = rememberCoroutineScope()
                        scope.launch(Dispatchers.IO) {
                            saveBitmapToStorage(context, transformedBitmap)

                            withContext(Dispatchers.Main) {
                                if (imageUris.isNotEmpty()) {
                                    imageUris.clear()
                                }
                                navHostController.navigate("HomeScreen") {
                                    popUpTo(navHostController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }
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

@Composable
fun TransformableImageBox(
    uri: String,
    onBitmapReady: (Bitmap?) -> Unit,
    onTransformChange: (Float, Offset, Float) -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var dominantColor by remember { mutableStateOf(Color.Blue) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dominantColor)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    scale *= zoom
                    offset = Offset(offset.x + pan.x, offset.y + pan.y)
                    rotationAngle += rotation
                    onTransformChange(scale, offset, rotationAngle)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .allowHardware(false)
                .build(),
            contentDescription = "Transformable Image",
            modifier = Modifier.graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y,
                rotationZ = rotationAngle
            ),
            onSuccess = { result ->
                val bitmap = (result.result.drawable as BitmapDrawable).bitmap
                onBitmapReady(bitmap)

                // Extract dominant color of the image
                Palette.from(bitmap).generate { palette ->
                    palette?.let {
                        dominantColor = Color(it.getDominantColor(Color.Blue.toArgb()))
                    }
                }

            }
        )
    }
}

fun createTransformedBitmap(
    originalBitmap: Bitmap,
    scale: Float,
    offset: Offset,
    rotationAngle: Float,
    screenWidth: Int,
    screenHeight: Int
): Bitmap {
    // Extract dominant color of the image
    val palette = Palette.from(originalBitmap).generate()
    val dominantColorInt = palette.getDominantColor(Color.Red.toArgb())

    val newBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(newBitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Set the background color
    canvas.drawColor(dominantColorInt)

    val matrix = Matrix().apply {
        postTranslate(-originalBitmap.width / 2f, -originalBitmap.height / 2f) // Đưa ảnh về tâm
        postScale(scale, scale, 0f, 0f)
        postRotate(rotationAngle, 0f, 0f)
        postTranslate(screenWidth / 2f + offset.x, screenHeight / 2f + offset.y)
    }

    canvas.drawBitmap(originalBitmap, matrix, paint)
    return newBitmap
}


fun saveBitmapToStorage(context: Context, bitmap: Bitmap) {
    val filename = "box_image_${System.currentTimeMillis()}.png"
    val fos: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/BoxScreenshots")
        }
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        imageUri?.let { resolver.openOutputStream(it) }
    } else {
        val imagesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val image = File(imagesDir, filename)
        FileOutputStream(image)
    }
    fos?.use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
    }
}


@Preview
@Composable
private fun AddContentScreenPreview() {
    AddContentScreen(navHostController = rememberNavController())
}
