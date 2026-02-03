package com.example.errenteriaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.room.Room
import com.example.errenteriaapp.database.AppDatabase
import com.example.errenteriaapp.database.MIGRATION_1_2
import com.example.errenteriaapp.database.viewModel.*
import com.example.errenteriaapp.navigation.screens.*
import com.example.errenteriaapp.screens.ranking.RankinScreen
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

// Datu-base globalerako aldagaia
private var appDatabase: AppDatabase? = null

/**
 * Aplikazioaren nabigazio nagusia kudeatzen duen funtzioa.
 * Pantaila guztiak eta haien arteko transizioak konfiguratzen ditu.
 *
 * @param navController Nabigazio kontroladorea
 * @param isDarkMode Ilunpeko modua aktibatuta dagoen
 * @param onThemeChange Gai aldaketaren kudeatzailea
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit,
) {
    // Uneko erabiltzaile izena gordetzeko
    var currentUserName by remember { mutableStateOf<String?>(null) }
    // Irakasle modua aktibatuta dagoen
    var isTeacherMode by rememberSaveable { mutableStateOf(false) }
    // Rankinga ezabatzen ari den
    var isDeletingRanking by rememberSaveable { mutableStateOf(false) }
    // Puntuazioak berrabiarazten ari den
    var isResettingScores by rememberSaveable { mutableStateOf(false) }

    // Nabigazio hosta konfiguratu
    NavHost(
        navController = navController,
        startDestination = Routes.HOME_SCREEN,
        modifier = Modifier
    ) {
        // HASIERAKO PANTIALLA
        composable(Routes.HOME_SCREEN) {
            HomeScreen(
                navController = navController
            )
        }

        // SAIO HASIERA PANTIALLA
        composable(Routes.LOGIN_SCREEN) {
            val context = LocalContext.current

            // Datu-basea behin bakarrik sortu
            if (appDatabase == null) {
                appDatabase = remember {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "errenteria_database"
                    ).addMigrations(MIGRATION_1_2).build()
                }
            }

            val db = appDatabase!!
            val ikasleDao = remember { db.ikasleDao() }
            val irakasleDao = remember { db.irakasleDao() }
            val partidaDao = remember { db.partidaDao() }
            val puntuazioaDao = remember { db.puntuazioaDao() }
            val izenTaldeaDao = remember { db.klaseakDao() }

            // Login ViewModel sortu
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(
                    ikasleDao,
                    irakasleDao,
                    partidaDao,
                    puntuazioaDao,
                    izenTaldeaDao
                )
            )

            // Erabiltzailearen egoerak behatu
            val user by loginViewModel.currentUser.collectAsState()
            val teacherMode by loginViewModel.isTeacherMode.collectAsState()

            // Erabiltzailea aldatu denean, uneko erabiltzailea eguneratu
            LaunchedEffect(user) {
                user?.let {
                    currentUserName = it
                    // Erabiltzaile aktiboa gorde erabilgarritasuna hobetzeko
                    context.getSharedPreferences("session", android.content.Context.MODE_PRIVATE)
                        .edit { putString("active_user_name", it) }
                }
            }

            // Irakasle modua aldatu denean eguneratu
            LaunchedEffect(teacherMode) {
                isTeacherMode = teacherMode
            }

            // Login pantaila erakutsi
            LoginScreen(
                loginViewModel = loginViewModel,
                navController = navController
            )
        }

        // ORDENATU JOKOA PANTIALLA
        composable(Routes.ORDENATUJOLASA_SCREEN) {
            // Dagoeneko sortutako datu-basea erabili
            val db = appDatabase
            if (db != null) {
                val puntuazioaDao = remember { db.puntuazioaDao() }

                // Ordenatu jokoaren ViewModel sortu
                val viewModel: OrdenatuJolasaViewModel = viewModel(
                    factory = OrdenatuJolasaViewModelFactory(puntuazioaDao)
                )

                // Erabiltzailea ViewModel-ean ezarri
                LaunchedEffect(currentUserName) {
                    currentUserName?.let {
                        viewModel.setUsuario(it)
                    }
                }

                // Ordenatu jokoaren pantaila erakutsi
                OrdenatuJolasaScreen(
                    navController = navController,
                    userName = currentUserName,
                    viewModel = viewModel,
                    modifier = Modifier
                )
            } else {
                // Datu-basea ez badago, login-era birbideratu
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN_SCREEN)
                }
            }
        }

        // RANKING PANTIALLA
        composable(Routes.RANKIN_SCREEN) {
            val db = appDatabase
            if (db != null) {
                val puntuazioaDao = remember { db.puntuazioaDao() }

                // Ranking ViewModel sortu
                val viewModel: RankingViewModel = viewModel(
                    factory = RankingViewModelFactory(puntuazioaDao)
                )

                // Ranking pantaila erakutsi
                RankinScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            } else {
                // Datu-basea ez badago, login-era birbideratu
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN_SCREEN)
                }
            }
        }

        // BERTSO JOKOA (1. bertsioa) PANTIALLA
        composable(Routes.BERTSOJOLASA_SCREEN) {
            val db = appDatabase
            if (db != null) {
                val puntuazioaDao = remember { db.puntuazioaDao() }

                // Bertso jokoaren ViewModel sortu (1. bertsioa)
                val viewModel: BertsoViewModel = viewModel(
                    factory = BertsoViewModelFactory(puntuazioaDao)
                )

                // Erabiltzailea ViewModel-ean ezarri
                LaunchedEffect(currentUserName) {
                    currentUserName?.let {
                        viewModel.setUsuario(it)
                    }
                }

                BertsoJolasaScreen(
                    navController = navController,
                    userName = currentUserName,
                    viewModel = viewModel
                )
            } else {
                // Datu-basea ez badago, login-era birbideratu
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN_SCREEN)
                }

            }
        }

        // BERTSO JOKOA (2. bertsioa) PANTIALLA
        composable(Routes.BERTSOJOLASA2_SCREEN) {
            val db = appDatabase
            if (db != null) {

                val puntuazioaDao = remember { db.puntuazioaDao() }
                // Bigarren bertsoaren konfigurazio berezia
                val viewModel: BertsoViewModel = viewModel(
                    factory = BertsoViewModelFactory(
                        puntuazioaDao,
                        BertsoViewModel.ConfigJuego.DEFAULT_BERTSOA_2
                    )
                )

                // Erabiltzailea ViewModel-ean ezarri
                LaunchedEffect(currentUserName) {
                    currentUserName?.let {
                        viewModel.setUsuario(it)
                    }
                }

                // Bertso jokoaren bigarren pantaila erakutsi
                BertsoJolasaScreen2(
                    navController = navController,
                    userName = currentUserName,
                    viewModel = viewModel
                )
            } else {
                // Datu-basea ez badago, login-era birbideratu
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN_SCREEN)
                }
            }

        }

        // CRUCIGRAMA PANTIALLA
        composable(Routes.CRUCIGRAMA_SCREEN) {
            val db = appDatabase
            if (db != null) {

                val puntuazioaDao = remember { db.puntuazioaDao() }
                // Crucigrama ViewModel sortu
                val viewModel: CrucigramaViewModel = viewModel(
                    factory = CrucigramaViewModelFactory(puntuazioaDao)
                )
                // Erabiltzailea ViewModel-ean ezarri
                LaunchedEffect(currentUserName) {
                    currentUserName?.let {
                        viewModel.setUsuario(it)
                    }
                }
                // Crucigrama pantaila erakutsi
                CrucigramaScreen(
                    navController = navController,
                    userName = currentUserName,
                    viewModel = viewModel
                )
            } else {
                // Datu-basea ez badago, login-era birbideratu
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN_SCREEN)
                }
            }
        }


        // PAPRESA JOKOA (Baztertu) PANTIALLA
        composable(Routes.BASURA_SCREEN) {
            val db = appDatabase
            if (db != null) {
                val puntuazioaDao = remember { db.puntuazioaDao() }

                // Papresa ViewModel sortu
                val viewModel: PapresaViewModel = viewModel(
                    factory = PapresaViewModelFactory(puntuazioaDao)
                )

                // Erabiltzailea ViewModel-ean ezarri
                LaunchedEffect(currentUserName) {
                    currentUserName?.let {
                        viewModel.setUsuario(it)
                    }
                }

                // Papresa pantaila erakutsi
                PapresaScreen(
                    navController = navController,
                    userName = currentUserName,
                    viewModel = viewModel
                )
            } else {
                // Datu-basea ez badago, login-era birbideratu
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN_SCREEN)
                }
            }
        }

        // PUZZLE PANTIALLA
        composable(Routes.PUZZLE_SCREEN) {
            PuzzleScreen(
                onBack = { navController.navigateUp() },
                onPuzzleComplete = {
                    navController.navigate(Routes.BASURA_SCREEN)
                },
                userName = currentUserName
            )
        }

        // SOPA LETRA PANTIALLA
        composable(Routes.SOPALETRA_SCREEN) {
            val db = appDatabase
            if (db != null) {
                val puntuazioaDao = remember { db.puntuazioaDao() }

                // Sopa letra ViewModel sortu
                val viewModel: SopaDeLetrasViewModel = viewModel(
                    factory = SopaDeLetrasViewModelFactory(puntuazioaDao)
                )

                // Erabiltzailea ViewModel-ean ezarri
                LaunchedEffect(currentUserName) {
                    currentUserName?.let {
                        viewModel.setUsuario(it)
                    }
                }
                // Sopa letra pantaila erakutsi
                LetraSopaScreen(
                    navController = navController,
                    userName = currentUserName,
                    viewModel = viewModel
                )
            } else {
                // Datu-basea ez badago, login-era birbideratu
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN_SCREEN)
                }
            }
        }

        // SAN MARKOS GALDERAK PANTIALLA
        composable(Routes.SANMARKOS_SCREEN) {
            val db = appDatabase
            if (db != null) {
                val puntuazioaDao = remember { db.puntuazioaDao() }

                // San Markos ViewModel sortu
                val viewModel: SanMarkosViewModel = viewModel(
                    factory = SanMarkosViewModelFactory(puntuazioaDao)
                )
                // San Markos pantaila erakutsi
                SanMarkosekoGalderak(
                    navController = navController,
                    userName = currentUserName,
                    viewModel = viewModel
                )
            } else {
                // Datu-basea ez badago, login-era birbideratu
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN_SCREEN)
                }
            }
        }

        // TAULA ARRASTRAR PANTIALLA
        composable(Routes.TAULAARRASTRAR_SCRENN) {
            val db = appDatabase
            if (db != null) {
                val puntuazioaDao = remember { db.puntuazioaDao() }

                // Arropa buru handiak ViewModel sortu
                val viewModel: ArropaBuruHandiakViewModel = viewModel(
                    factory = ArropaBuruHandiakFactory(puntuazioaDao)
                )
                // Erabiltzailea ViewModel-ean ezarri
                LaunchedEffect(currentUserName) {
                    currentUserName?.let {
                        viewModel.setUsuario(it)
                    }
                }
                // Taula arrastrar pantaila erakutsi
                TaulaArrastrarScreen(
                    navController = navController,
                    userName = currentUserName,
                    viewModel = viewModel
                )
            } else {
                // Datu-basea ez badago, login-era birbideratu
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN_SCREEN)
                }
            }
        }

        // GPS Mapa PANTIALLA
        composable(Routes.GPS_SCREEN) {
            MapaOsmScreen(
                navController = navController
            )
        }

        // DOITURAK PANTIALLA
        composable(Routes.AJUSTES_SCREEN) {
            val db = appDatabase
            if (db != null) {
                val ikasleDao = remember { db.ikasleDao() }
                val irakasleDao = remember { db.irakasleDao() }
                val partidaDao = remember { db.partidaDao() }
                val puntuazioaDao = remember { db.puntuazioaDao() }
                val izenTaldeaDao = remember { db.klaseakDao() }
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(
                        ikasleDao,
                        irakasleDao,
                        partidaDao,
                        puntuazioaDao,
                        izenTaldeaDao
                    )
                )

                // Doiturak korrutina esparrua memorizatu
                val ajustesScope = rememberCoroutineScope()

                // Doiturak pantaila erakutsi
                AjustesScreen(
                    isTeacherMode = isTeacherMode,
                    isDarkMode = isDarkMode,
                    onThemeToggle = onThemeChange,
                    onDeleteRanking = {
                        if (!isDeletingRanking) {
                            ajustesScope.launch {
                                isDeletingRanking = true
                                try {
                                    loginViewModel.deleteEntireRanking()
                                } finally {
                                    isDeletingRanking = false
                                }
                            }
                        }
                    },
                    onResetScores = {
                        if (!isResettingScores) {
                            ajustesScope.launch {
                                isResettingScores = true
                                try {
                                    loginViewModel.resetRankingScores()
                                } finally {
                                    isResettingScores = false
                                }
                            }
                        }
                    },
                    isDeletingRanking = isDeletingRanking,
                    isResettingScores = isResettingScores
                )
            } else {
                // Datu-basea ez badago, login-era birbideratu
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN_SCREEN)
                }
            }
        }

        // TXAT PANTIALLA
        composable(Routes.CHAT_SCREEN) {
            ChatRoute(
                onBack = { navController.navigate(Routes.GPS_SCREEN) }
            )
        }
    }
}