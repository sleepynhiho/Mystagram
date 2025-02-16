package com.forrestgump.ig.ui.screens.notification

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.forrestgump.ig.ui.components.Loading
import com.forrestgump.ig.ui.screens.profile.UiState
import com.forrestgump.ig.utils.constants.Utils.MainBackground

@Composable
fun NotificationScreen(
    uiState: UiState,
) {
    uiState.isLoading = false
    if (!uiState.isLoading) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MainBackground
        ) {

        }
    } else {
        Loading()
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0XFF000000
)
@Composable
private fun SearchScreenPreview() {

}