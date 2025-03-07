package com.forrestgump.ig.ui.screens.addStory


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
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.forrestgump.ig.R
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.ui.screens.addStory.components.AddStoryTopBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


@Composable
fun AddStoryScreen(
    currentUser: User,
    navHostController: NavHostController,
    viewModel: AddStoryViewModel = hiltViewModel()
) {
    val mediaUris = remember { mutableStateListOf<Uri>() }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var permissionGranted by remember { mutableStateOf(false) }
    val context = LocalContext.current


    // Chọn 1 ảnh
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        mediaUris.clear()
        uri?.let { mediaUris.add(it) }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            cameraImageUri?.let { mediaUris.add(it) }
        }
    }

    // Yêu cầu quyền
    val requestPermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        permissionGranted = results.values.all { it }
    }

    LaunchedEffect(Unit) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                pickMediaLauncher.launch("image/*")
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                requestPermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.CAMERA
                    )
                )
            }

            else -> {
                requestPermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    )
                )
            }
        }
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { pickMediaLauncher.launch("image/*") }) {
            Text("Chọn ảnh từ thư viện")
        }
        Button(onClick = {
            if (permissionGranted) { // Kiểm tra quyền
                cameraImageUri = createImageUri(context)
                cameraImageUri?.let { takePictureLauncher.launch(it) }
            } else {
                requestPermissions.launch(
                    arrayOf(Manifest.permission.CAMERA) // Nếu chưa có quyền, yêu cầu trước
                )
            }
        }) {
            Text("Chụp ảnh từ camera")
        }

    }


    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var textScale by remember { mutableFloatStateOf(1f) }
    var textOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var textRotationAngle by remember { mutableFloatStateOf(0f) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var userInputText by remember { mutableStateOf("") }
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    var addTextClicked by remember { mutableStateOf(false) }
    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }

    if (mediaUris.isNotEmpty()) {
        val uri = mediaUris[0]
        Box(modifier = Modifier.fillMaxSize()) {
            TransformableImageBox(
                uri = uri.toString(),
                onBitmapReady = { loadedBitmap ->
                    bitmap = loadedBitmap
                },
                onTransformChange = { newScale, newOffset, newRotation ->
                    scale = newScale
                    offset = newOffset
                    rotationAngle = newRotation
                },
                addTextClicked = addTextClicked,
                onTextTransformChange = { newScale, newOffset, newRotation ->
                    textScale = newScale
                    textOffset = newOffset
                    textRotationAngle = newRotation
                },
                userInputText = userInputText,
                onUserTextChange = { newText -> userInputText = newText }
            )

            AddStoryTopBar(
                onBackClicked = {
                    mediaUris.clear()
                    userInputText = ""
                    addTextClicked = false
                    pickMediaLauncher.launch("image/*")
                },
                onAddTextClicked = { addTextClicked = true }
            )

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
                            screenWidth.toInt(), screenHeight.toInt(),
                            userText = userInputText,
                            textOffset = textOffset,
                            textScale = textScale,
                            textRotationAngle = textRotationAngle,
                            density = density
                        )
                        val scope = rememberCoroutineScope()
                        scope.launch(Dispatchers.IO) { // Đảm bảo đang chạy trong coroutine
                            try {
                                // Chuyển bitmap thành URI tạm thời
                                val tempUri = saveBitmapToCache(context, transformedBitmap)

                                // Gọi suspend function trong coroutine
                                withContext(Dispatchers.IO) {
                                    viewModel.uploadStoryImage(
                                        imageUri = tempUri,
                                        onSuccess = {
                                            scope.launch(Dispatchers.Main) { // Quay về Main Thread để update UI
                                                if (mediaUris.isNotEmpty()) {
                                                    mediaUris.clear()
                                                }
                                                navHostController.navigate("HomeScreen") {
                                                    popUpTo(navHostController.graph.startDestinationId) {
                                                        inclusive = true
                                                    }
                                                }
                                            }
                                        },
                                        onError = { error ->
                                            scope.launch(Dispatchers.Main) {
                                                Toast.makeText(
                                                    context,
                                                    "Lỗi1: $error",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                Log.d("NHII", error)
                                            }
                                        },
                                        context = context,
                                        user = currentUser
                                    )
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Lỗi2: ${e.localizedMessage}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }


            }

        }
    }

    BackHandler {
        if (mediaUris.isNotEmpty()) {
            mediaUris.clear()
            userInputText = ""
            addTextClicked = false
            pickMediaLauncher.launch("image/*")
        } else {
            navHostController.popBackStack()
        }
    }
}

fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val cachePath = File(context.cacheDir, "images") // Tạo thư mục cache
    cachePath.mkdirs()

    val file = File(cachePath, "story_image.png") // Lưu ảnh vào file
    val stream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    stream.flush()
    stream.close()

    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}



@Composable
fun TransformableImageBox(
    uri: String,
    onBitmapReady: (Bitmap?) -> Unit,
    onTransformChange: (Float, Offset, Float) -> Unit,
    addTextClicked: Boolean,
    onTextTransformChange: (Float, Offset, Float) -> Unit,
    userInputText: String,
    onUserTextChange: (String) -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var dominantColor by remember { mutableStateOf(Color.Black) }

    var textScale by remember { mutableFloatStateOf(1f) }
    var textOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var textRotationAngle by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
                            dominantColor = Color(it.getDominantColor(Color.Black.toArgb()))
                        }
                    }
                }
            )
        }

        val density = LocalDensity.current
        val textMeasurer = rememberTextMeasurer()

        val measuredTextSize = if (userInputText.isNotEmpty()) {
            textMeasurer.measure(
                text = userInputText,
                style = TextStyle(
                    fontSize = (20f * textScale).sp,
                    textAlign = TextAlign.Center
                )
            ).size
        } else {
            IntSize(15, 60) // ✅ Set aa reasonable default size when text is empty
        }

        if (addTextClicked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, rotation ->
                            textScale *= zoom
                            textOffset = Offset(textOffset.x + pan.x, textOffset.y + pan.y)
                            textRotationAngle += rotation
                            onTextTransformChange(textScale, textOffset, textRotationAngle)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = userInputText,
                    onValueChange = onUserTextChange,
                    textStyle = TextStyle(
                        fontSize = (20f * textScale).sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .graphicsLayer(
                            translationX = textOffset.x,
                            translationY = textOffset.y,
                            scaleX = textScale,
                            scaleY = textScale,
                            rotationZ = textRotationAngle
                        )
                        .background(
                            Color.White.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(8.dp * textScale)
                        )
                        .padding(horizontal = 8.dp * textScale, vertical = 4.dp * textScale)
                        .size(
                            width = with(density) { measuredTextSize.width.toDp() } + 8.dp,
                            height = with(density) { measuredTextSize.height.toDp() } + 4.dp
                        )
                )
            }

            LaunchedEffect(textScale) {
                Log.d("NHII", "TextScale: $textScale")
                val resolvedTextSizePx = with(density) { (20f * textScale).sp.toPx() }
                Log.d("NHII", "density11: $density ")
                Log.d("NHII", "fontsize: $resolvedTextSizePx ")
            }

        }
    }
}

fun createTransformedBitmap(
    originalBitmap: Bitmap,
    scale: Float,
    offset: Offset,
    rotationAngle: Float,
    screenWidth: Int,
    screenHeight: Int,
    userText: String,
    textOffset: Offset,
    textScale: Float,
    textRotationAngle: Float,
    density: Density
): Bitmap {
    val palette = Palette.from(originalBitmap).generate()
    val dominantColorInt = palette.getDominantColor(Color.Red.toArgb())

    val newBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(newBitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    canvas.drawColor(dominantColorInt)

    val matrix = Matrix().apply {
        postTranslate(-originalBitmap.width / 2f, -originalBitmap.height / 2f)
        postScale(scale, scale, 0f, 0f)
        postRotate(rotationAngle, 0f, 0f)
        postTranslate(screenWidth / 2f + offset.x, screenHeight / 2f + offset.y)
    }

    canvas.drawBitmap(originalBitmap, matrix, paint)

    if (userText.isNotEmpty()) {
        val textSizePx = with(density) { (20.sp * textScale).toPx() }
        paint.textSize = textSizePx

        Log.d("NHII", "TextScale: $textScale")
        Log.d("NHII", "density22: $density ")
        Log.d("NHII", "textSizePx:  $textSizePx ")


        val textWidth = paint.measureText(userText)
        val fontMetrics = paint.fontMetrics
        val textHeight = (fontMetrics.descent - fontMetrics.ascent)

        val paddingX = with(density) { 8.dp.toPx() } * textScale
        val paddingY = with(density) { 4.dp.toPx() } * textScale

        val bgWidth = textWidth + paddingX * 2
        val bgHeight = textHeight + paddingY * 2

        val textX = screenWidth / 2f + textOffset.x
        val textY = screenHeight / 2f + textOffset.y

        canvas.save()
        canvas.translate(textX, textY)
        canvas.rotate(textRotationAngle)

        val backgroundRadius = with(density) { 8.dp.toPx() } * textScale

        paint.color = Color.White.copy(alpha = 0.7f).toArgb()
        canvas.drawRoundRect(
            -bgWidth / 2, -bgHeight / 2, bgWidth / 2, bgHeight / 2,
            backgroundRadius, backgroundRadius, paint
        )

        paint.color = Color.Black.toArgb()
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(userText, 0f, -((fontMetrics.ascent + fontMetrics.descent) / 2), paint)

        canvas.restore()
    }
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

fun createImageUri(context: Context): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }
    return context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    )
}


@Preview
@Composable
private fun AddStoryScreenPreview() {
    AddStoryScreen(
        navHostController = rememberNavController(),
        currentUser = TODO(),
        viewModel = TODO()
    )
}
