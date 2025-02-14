package com.forrestgump.ig.ui.screens.add

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi

@UnstableApi
@Composable
fun AddContentScreen(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) { }

    BackHandler {
        onBackClick()
    }
}

@UnstableApi
@Preview
@Composable
private fun AddContentScreenPreview() {
    AddContentScreen(
        onBackClick = { }
    )
}