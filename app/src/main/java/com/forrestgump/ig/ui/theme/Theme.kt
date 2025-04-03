package com.forrestgump.ig.ui.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.forrestgump.ig.utils.constants.Utils.MainBackground
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFFFFFFF),
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
)

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun MystagramTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = colorScheme.background, // Hoặc màu phù hợp khác
            darkIcons = !darkTheme
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

class ThemeManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    private val _currentTheme = MutableStateFlow(loadThemePreference())
    val currentTheme: Flow<String> = _currentTheme.asStateFlow()

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener { _, key ->
            if (key == "theme_preference") {
                _currentTheme.value = loadThemePreference()
            }
        }
    }

    fun saveThemePreference(theme: String) {
        sharedPreferences.edit().putString("theme_preference", theme).apply()
    }

    private fun loadThemePreference(): String {
        return sharedPreferences.getString("theme_preference", "system") ?: "system"
    }
}