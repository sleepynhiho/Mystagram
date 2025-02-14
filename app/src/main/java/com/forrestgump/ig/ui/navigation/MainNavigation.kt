package com.forrestgump.ig.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.forrestgump.ig.ui.screens.splash.SplashScreen
import com.forrestgump.ig.ui.screens.auth.AuthViewModel
import kotlinx.coroutines.delay

@UnstableApi
@Composable
fun MainNavigation(viewModel: AuthViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    var startDestination by rememberSaveable { mutableStateOf(Routes.SplashScreen.route) }


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            route = Routes.SplashScreen.route,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(350)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(350)
                )
            }
        ) {

            LaunchedEffect(key1 = Unit) {
                startDestination = Routes.InnerContainer.route
                delay(1000L)
                navController.popBackStack()
                navController.navigate(Routes.InnerContainer.route)
            }
            SplashScreen()
        }
        composable(
            route = Routes.InnerContainer.route,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(350)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(350)
                )
            }
        ) {
            LaunchedEffect(key1 = Unit) {
                startDestination = Routes.InnerContainer.route
            }

            InnerContainer()
        }
    }
}