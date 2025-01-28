package com.forrestgump.ig.utils.constants

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

object Utils {
    /**
     * Adaptive background color for Dark and Light mode.
     */
    val MainBackground: Color
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) Color(0XFF000000) else Color(0xFFFFFFFF)

    val LightBlue = Color(0xFF2196F3)
}