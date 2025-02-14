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
import com.forrestgump.ig.ui.screens.home.HomeViewModel

@UnstableApi
@Composable
fun InnerContainer(
    viewModelProfile: ProfileViewModel = hiltViewModel(),
    viewModelHome: HomeViewModel = hiltViewModel() // Truyền vào để dễ test hơn
) {
    val navHostController = rememberNavController()
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentScreen = navBackStackEntry?.destination?.route

    val uiStateProfile by viewModelProfile.uiState.collectAsState()
    val uiStateHome by viewModelHome.uiState.collectAsState()

    val showBottomNavBar = when(currentScreen) {
        Routes.HomeScreen.route -> !uiStateHome.showStoryScreen
        Routes.SearchScreen.route -> true
        Routes.NotificationScreen.route -> true
        Routes.MyProfileScreen.route -> true
        else -> false
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MainBackground,
        bottomBar = {
            if (showBottomNavBar) {
                BottomNavBar(
                    profileImage = uiStateProfile.profileImage,
                    navHostController = navHostController
                )
            }
        },
        content = { contentPadding ->
            InnerNavigation(
                contentPadding = contentPadding,
                viewModelProfile = viewModelProfile,
                navHostController = navHostController,
            )
        }
    )
}