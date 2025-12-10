package com.example.errenteriaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.errenteriaapp.navigation.screens.BertsoJolasaScreen
import com.example.errenteriaapp.navigation.screens.BertsoJolasaScreen2
import com.example.errenteriaapp.navigation.screens.HomeScreen
import com.example.errenteriaapp.navigation.screens.LoginScreen
import com.example.errenteriaapp.navigation.screens.MapaScreen
import com.example.errenteriaapp.navigation.screens.PuzleScreen


import com.example.errenteriaapp.database.viewModel.ConversacionViewModel
import com.example.errenteriaapp.database.viewModel.LoginViewModel
import com.example.errenteriaapp.navigation.screens.OrdenatuJolasaScreen


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
        composable(Routes.BERTSOJOLASA_SCREEN) {
            BertsoJolasaScreen(
                navController = navController
            )
        }
        composable(Routes.BERTSOJOLASA2_SCREEN) {
            BertsoJolasaScreen2(
                navController = navController
            ) }

        composable(Routes.PUZLE_SCREEN) {
            PuzleScreen(
                navController = navController
            )
        }
        composable(Routes.ORDENATUJOLASA_SCREEN) {
            OrdenatuJolasaScreen(
                navController = navController,
                modifier = Modifier
            )
        }
    }
}