package com.forrestgump.ig.ui.screens.story.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun StoryProgressTrack(
    modifier: Modifier = Modifier,
    isStoryActive: Boolean,
    isPaused: Boolean,
    isStopped: Boolean = false,
    onProgressComplete: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val maxDuration = 5000
        val minDuration = 1000
        var lastElapsedTime by remember { mutableLongStateOf(0L) }
        val progressAnimation = remember { Animatable(0f) }

        LaunchedEffect(isStoryActive, isPaused, isStopped) {
            if (isStoryActive && !isPaused && !isStopped) {
                val currentTime = System.currentTimeMillis()
                if (lastElapsedTime == 0L) lastElapsedTime = currentTime
                val remainingDuration =
                    (maxDuration - (currentTime - lastElapsedTime).toInt()).coerceAtLeast(minDuration)

                progressAnimation.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = remainingDuration, easing = LinearEasing)
                ) {
                    if (value >= 1f) onProgressComplete()
                }
            } else if (!isStoryActive) {
                lastElapsedTime = 0L
                if (progressAnimation.value > 0f) progressAnimation.snapTo(1f)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 2.dp)
                    .background(Color(0x59FFFFFF))
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progressAnimation.value)
                    .padding(horizontal = 2.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(color = Color.White),
            )
        }
    }
}

@Preview
@Composable
private fun StoryProgressTrackPreview() {
    StoryProgressTrack(
        modifier = Modifier,
        isStoryActive = true,
        isPaused = false,
        isStopped = false,
        onProgressComplete = { }
    ) 
}