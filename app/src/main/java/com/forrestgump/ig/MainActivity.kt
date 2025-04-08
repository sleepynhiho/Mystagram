package com.forrestgump.ig

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import com.forrestgump.ig.ui.navigation.MainNavigation
import com.forrestgump.ig.ui.theme.MystagramTheme
import com.forrestgump.ig.ui.theme.ThemeManager
import com.forrestgump.ig.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.R)
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themeManager: ThemeManager
    private val themeViewModel: ThemeViewModel by viewModels()

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themePreference by themeManager.currentTheme.collectAsState(initial = "system") // Add initial value
            val isSystemDark = isSystemInDarkTheme()
            val isDarkTheme = when (themePreference) {
                "system" -> isSystemDark
                "light" -> false
                "dark" -> true
                else -> isSystemDark
            }

            MystagramTheme(darkTheme = isDarkTheme) {
                MainNavigation()
            }
        }
    }

    override fun attachBaseContext(base: Context) {
        val prefs = base.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedLang = prefs.getString("language_code", "en") ?: "en"
        val context = updateBaseContextLocale(base, savedLang)
        applyOverrideConfiguration(context.resources.configuration)
        super.attachBaseContext(context)
    }

    private fun updateBaseContextLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = android.content.res.Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}