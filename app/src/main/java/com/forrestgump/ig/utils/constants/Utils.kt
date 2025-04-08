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
        get() = if (isSystemInDarkTheme()) Color(0xFF121212) else Color(0xFFFFFFFF)

    val Primary: Color
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) Color(0xFFBB86FC) else Color(0xFF6200EE)

    val surfaceColor: Color
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) Color(0xFF000000) else Color(0xFFFFFFFF)

    val onSurface: Color
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) Color(0xFFFFFFFF) else Color(0xFF000000)

    val LightBlue = Color(0xFF2196F3)
}
