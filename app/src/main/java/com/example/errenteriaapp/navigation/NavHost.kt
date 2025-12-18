package com.example.errenteriaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.errenteriaapp.navigation.screens.BertsoJolasaScreen
import com.example.errenteriaapp.navigation.screens.BertsoJolasaScreen2
import com.example.errenteriaapp.navigation.screens.HomeScreen
import com.example.errenteriaapp.navigation.screens.LoginScreen
import com.example.errenteriaapp.navigation.screens.MapaScreen
import com.example.errenteriaapp.navigation.screens.PuzleScreen
import com.example.errenteriaapp.database.viewModel.ConversacionViewModel
import com.example.errenteriaapp.database.viewModel.LoginViewModel
import com.example.errenteriaapp.database.viewModel.LoginViewModelFactory
import com.example.errenteriaapp.database.AppDatabase
import com.example.errenteriaapp.navigation.screens.LetraSopaScreen
import com.example.errenteriaapp.navigation.screens.OrdenatuJolasaScreen
import com.example.errenteriaapp.navigation.screens.PapresaScreen
import com.example.errenteriaapp.navigation.screens.SanMarkosekoGalderak
import com.example.errenteriaapp.navigation.screens.TaulaArrastrarScreen

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
        composable(Routes.LOGIN_SCREEN) {
            val context = LocalContext.current
            val db = remember {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "errenteria_database"
                ).build()
            }
            val ikasleDao = remember { db.ikasleDao() }
            val irakasleDao = remember { db.irakasleDao() }

            val loginViewModelFactory = remember {
                LoginViewModelFactory(ikasleDao, irakasleDao)
            }

            val loginViewModel: LoginViewModel = viewModel(factory = loginViewModelFactory)

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
        composable(Routes.BASURA_SCREEN) {
            PapresaScreen(
                navController = navController
            )
        }
        composable(Routes.SOPALETRA_SCREEN) {
            LetraSopaScreen(
                navController = navController
            )
        }
        composable(Routes.SANMARKOS_SCREEN) {
            SanMarkosekoGalderak(
                navController = navController
            )
        }
        composable(Routes.TAULAARRASTRAR_SCRENN) {
            TaulaArrastrarScreen(
                navController = navController
            )
        }
    }
}