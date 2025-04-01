package com.forrestgump.ig.ui.screens.addStory.components

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.request.ImageRequest


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
            IntSize(15, 60)
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