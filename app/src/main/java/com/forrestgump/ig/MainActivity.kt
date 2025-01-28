package com.forrestgump.ig

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
}