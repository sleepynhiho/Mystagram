package com.forrestgump.ig.ui.navigation
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.forrestgump.ig.ui.screens.profile.ProfileViewModel
import com.forrestgump.ig.utils.constants.Utils.MainBackground

@UnstableApi
@Composable
fun InnerContainer(
    viewModelProfile: ProfileViewModel = hiltViewModel(),
) {
    val navHostController = rememberNavController()
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val CurrentScreen = navBackStackEntry?.destination?.route

    val uiState by viewModelProfile.uiState.collectAsState()

    val showBottomBar = when(CurrentScreen) {
        Routes.HomeScreen.route -> true
        Routes.SearchScreen.route -> true
        Routes.NotificationScreen.route -> true
        Routes.MyProfileScreen.route -> true
        else -> false
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MainBackground,
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    profileImage = uiState.profileImage,
                    navHostController = navHostController
                )
            }
        },
        content = { innerPadding ->
            InnerNavigation(
                innerPadding = innerPadding,
                viewModelProfile = viewModelProfile,
                navHostController = navHostController,
            )
        }
    )
}