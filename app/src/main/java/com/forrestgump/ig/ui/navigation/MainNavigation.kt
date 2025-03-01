package com.forrestgump.ig.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
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
import com.forrestgump.ig.ui.screens.auth.LoginScreen
import com.forrestgump.ig.ui.screens.auth.SignupScreen
import kotlinx.coroutines.delay
import com.google.firebase.auth.FirebaseAuth

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
                fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(350))
            }
        ) {
            LaunchedEffect(key1 = Unit) {
                delay(1000L)
                // Kiểm tra trạng thái đăng nhập
                val currentUser = FirebaseAuth.getInstance().currentUser
                startDestination = if (currentUser != null) {
                    Routes.InnerContainer.route
                } else {
                    Routes.LoginScreen.route
                }
                navController.popBackStack()
                navController.navigate(startDestination)
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

        composable(
            route = Routes.LoginScreen.route,
            enterTransition = {
                fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(350))
            }
        ) {
            LoginScreen(
                navController = navController,
                authViewModel = hiltViewModel(),
            )
        }

        composable(
            route = Routes.SignupScreen.route,
            enterTransition = {
                fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(350))
            }
        ) {
            SignupScreen(
                navController = navController,
                authViewModel = hiltViewModel(),
            )
        }
    }
}