package com.example.errenteriaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.errenteriaapp.navigation.screens.BertsoJolasaScreen
import com.example.errenteriaapp.navigation.screens.GameScreen
import com.example.errenteriaapp.navigation.screens.HomeScreen
import com.example.errenteriaapp.viewModel.ConversacionViewModel

@Composable
fun AppNavigation(
    conversacionViewModel: ConversacionViewModel,
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
        composable(Routes.BERTSOJOLASA_SCREEN) {
            BertsoJolasaScreen(
                navController = navController
            )
        }
    }
}