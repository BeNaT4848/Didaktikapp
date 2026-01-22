package com.example.errenteriaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.room.Room
import com.example.errenteriaapp.database.AppDatabase
import com.example.errenteriaapp.database.viewModel.*
import com.example.errenteriaapp.navigation.screens.*
import com.example.errenteriaapp.screens.ranking.RankinScreen

// Variable global para la base de datos
private var appDatabase: AppDatabase? = null

@Composable
fun AppNavigation(
    conversacionViewModel: ConversacionViewModel,
    navController: NavHostController,
) {
    var currentUserName by remember { mutableStateOf<String?>(null) }

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

            // Crear la base de datos solo una vez
            if (appDatabase == null) {
                appDatabase = remember {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "errenteria_database"
                    ).build()
                }
            }

            val db = appDatabase!!
            val ikasleDao = remember { db.ikasleDao() }
            val irakasleDao = remember { db.irakasleDao() }
            val partidaDao = remember { db.partidaDao() }
            val puntuazioaDao = remember { db.puntuazioaDao() }

            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(
                    ikasleDao,
                    irakasleDao,
                    partidaDao,
                    puntuazioaDao
                )
            )

            val user by loginViewModel.currentUser.collectAsState()

            LaunchedEffect(user) {
                user?.let {
                    currentUserName = it
                }
            }

            LoginScreen(
                loginViewModel = loginViewModel,
                navController = navController,
            )
        }

        composable(Routes.ORDENATUJOLASA_SCREEN) {
            // Usar la base de datos ya creada
            val db = appDatabase
            if (db != null) {
                val puntuazioaDao = remember { db.puntuazioaDao() }

                // Usar el factory que ya tienes
                val viewModel: OrdenatuJolasaViewModel = viewModel(
                    factory = OrdenatuJolasaViewModelFactory(puntuazioaDao)
                )

                LaunchedEffect(currentUserName) {
                    currentUserName?.let {
                        viewModel.setUsuario(it)
                    }
                }

                OrdenatuJolasaScreen(
                    navController = navController,
                    userName = currentUserName,
                    viewModel = viewModel,
                    modifier = Modifier
                )
            } else {
                // Si no hay base de datos, redirigir al login
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN_SCREEN)
                }
            }
        }

        composable(Routes.RANKIN_SCREEN) {
            // Usar la base de datos ya creada
            val db = appDatabase
            if (db != null) {
                val puntuazioaDao = remember { db.puntuazioaDao() }

                // Usar el factory que ya tienes
                val viewModel: RankingViewModel = viewModel(
                    factory = RankingViewModelFactory(puntuazioaDao)
                )

                RankinScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            } else {
                // Si no hay base de datos, redirigir al login
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN_SCREEN)
                }
            }
        }

        // Para juegos que NO necesitan base de datos
        composable(Routes.MAPA_SCREEN) {
            MapaScreen(
                navController = navController
            )
        }

        composable(Routes.BERTSOJOLASA_SCREEN) {
            BertsoJolasaScreen(
                navController = navController,
                userName = currentUserName
            )
        }

        composable(Routes.BERTSOJOLASA2_SCREEN) {
            BertsoJolasaScreen2(
                navController = navController,
                userName = currentUserName
            )
        }

        composable(Routes.CRUCIGRAMA_SCREEN) {
            CrucigramaScreen(
                navController = navController,
                userName = currentUserName
            )
        }

        composable(Routes.BASURA_SCREEN) {
            PapresaScreen(
                navController = navController,
                userName = currentUserName
            )
        }

        composable(Routes.PUZZLE_SCREEN) {
            PuzzleScreen(
                onBack = { navController.navigateUp() },
                onPuzzleComplete = {
                    navController.navigate(Routes.BASURA_SCREEN)
                },
                userName = currentUserName
            )
        }

        composable(Routes.SOPALETRA_SCREEN) {
            LetraSopaScreen(
                navController = navController,
                userName = currentUserName
            )
        }

        composable(Routes.SANMARKOS_SCREEN) {
            SanMarkosekoGalderak(
                navController = navController,
                userName = currentUserName
            )
        }

        composable(Routes.TAULAARRASTRAR_SCRENN) {
            TaulaArrastrarScreen(
                navController = navController,
                userName = currentUserName
            )
        }

        composable(Routes.GPS_SCREEN) {
            MapaOsmScreen(
                navController = navController
            )
        }
    }
}