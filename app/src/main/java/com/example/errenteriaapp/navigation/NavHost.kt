package com.example.errenteriaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.errenteriaapp.navigation.screens.GameScreen
import com.example.errenteriaapp.navigation.screens.HomeScreen
import com.example.errenteriaapp.navigation.screens.LoginScreen
import com.example.errenteriaapp.navigation.screens.MapaScreen


import com.example.errenteriaapp.viewModel.ConversacionViewModel
import com.example.errenteriaapp.viewModel.LoginViewModel


@Composable
fun AppNavigation(
    conversacionViewModel: ConversacionViewModel,
    loginViewModel: LoginViewModel,
    navController: NavHostController,
) {

    NavHost(
        navController = navController,
        startDestination = Routes.HOME_SCREEN,
        modifier = Modifier
    ) {
        composable(Routes.HOME_SCREEN) {
            HomeScreen(
                navController = navController
            )

        }
        composable(Routes.GAME_SCREEN) {
            GameScreen(
                conversacionViewModel = conversacionViewModel,
                navController = navController
            )
        }
        composable(Routes.LOGIN_SCREEN) {
            LoginScreen(
                        loginViewModel = loginViewModel,
                        navController = navController,

                    )
                }
        composable(Routes.MAPA_SCREEN) {
                    MapaScreen(
                        navController = navController
                    )
                }
    }
}