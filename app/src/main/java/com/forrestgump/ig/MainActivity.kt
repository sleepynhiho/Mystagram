package com.forrestgump.ig

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.forrestgump.ig.ui.navigation.MainNavigation
import com.forrestgump.ig.ui.theme.MystagramTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import android.content.res.Configuration
import java.util.Locale

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.R)
class MainActivity : ComponentActivity() {
    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MystagramTheme {
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

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}