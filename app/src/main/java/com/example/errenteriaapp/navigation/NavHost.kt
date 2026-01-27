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

// Variable global para la base de datos
private var appDatabase: AppDatabase? = null

@Composable
fun AppNavigation(
    conversacionViewModel: ConversacionViewModel,
    navController: NavHostController,
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit,
) {
    var currentUserName by remember { mutableStateOf<String?>(null) }
    var isTeacherMode by rememberSaveable { mutableStateOf(false) }
    var isDeletingRanking by rememberSaveable { mutableStateOf(false) }
    var isResettingScores by rememberSaveable { mutableStateOf(false) }

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
                    ) .addMigrations(MIGRATION_1_2) .build()
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
            val teacherMode by loginViewModel.isTeacherMode.collectAsState()

            LaunchedEffect(user) {
                user?.let {
                    currentUserName = it
                }
            }

            LaunchedEffect(teacherMode) {
                isTeacherMode = teacherMode
            }

            LoginScreen(
                loginViewModel = loginViewModel,
                navController = navController,
                initialTeacherMode = isTeacherMode,
                onTeacherModeChange = { loginViewModel.setTeacherMode(it) }
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

        composable(Routes.BERTSOJOLASA_SCREEN) {
            val db = appDatabase
            if (db != null) {
                val puntuazioaDao = remember { db.puntuazioaDao() }

                // Usar el factory que ya tienes
                val viewModel: BertsoViewModel = viewModel(
                    factory = BertsoViewModelFactory(puntuazioaDao)
                )

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
                // Si no hay base de datos, redirigir al login
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN_SCREEN)
                }

            }
        }

        composable(Routes.BERTSOJOLASA2_SCREEN) {
            val db = appDatabase
            if (db != null) {

                val puntuazioaDao = remember { db.puntuazioaDao() }
                // Usar el factory que ya tienes
                val viewModel: BertsoViewModel = viewModel(
                    factory = BertsoViewModelFactory(puntuazioaDao)
                )


                LaunchedEffect(currentUserName) {
                    currentUserName?.let {
                        viewModel.setUsuario(it)
                    }
                }

                BertsoJolasaScreen2(
                    navController = navController,
                    userName = currentUserName,
                    viewModel = viewModel
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN_SCREEN)
                }
            }

        }

        composable(Routes.CRUCIGRAMA_SCREEN) {
            val db = appDatabase
            if (db != null) {

                    val puntuazioaDao = remember { db.puntuazioaDao() }
                    // Usar el factory que ya tienes
                    val viewModel: CrucigramaViewModel = viewModel(
                        factory = CrucigramaViewModelFactory(puntuazioaDao)
                    )
                    LaunchedEffect(currentUserName) {
                        currentUserName?.let {
                            viewModel.setUsuario(it)
                        }
                    }
                CrucigramaScreen(
                    navController = navController,
                    userName = currentUserName,
                    viewModel = viewModel
                )
            } else {
                    LaunchedEffect(Unit) {
                        navController.navigate(Routes.LOGIN_SCREEN)
                    }
                }
        }


        composable(Routes.BASURA_SCREEN) {
            // Usar la base de datos ya creada
            val db = appDatabase
            if (db != null) {
                val puntuazioaDao = remember { db.puntuazioaDao() }

                // Usar el factory de Papresa
                val viewModel: PapresaViewModel = viewModel(
                    factory = PapresaViewModelFactory(puntuazioaDao)
                )

                LaunchedEffect(currentUserName) {
                    currentUserName?.let {
                        viewModel.setUsuario(it)
                    }
                }

                PapresaScreen(
                    navController = navController,
                    userName = currentUserName,
                    viewModel = viewModel
                )
            } else {
                // Si no hay base de datos, redirigir al login
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN_SCREEN)
                }
            }
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
            val db = appDatabase
                if (db != null) {
                    val puntuazioaDao = remember { db.puntuazioaDao() }

                    // Usar el factory de Papresa
                    val viewModel: SopaDeLetrasViewModel = viewModel(
                        factory = SopaDeLetrasViewModelFactory(puntuazioaDao)
                    )

                    LaunchedEffect(currentUserName) {
                        currentUserName?.let {
                            viewModel.setUsuario(it)
                        }
                    }
                    LetraSopaScreen(
                        navController = navController,
                        userName = currentUserName,
                        viewModel = viewModel
                    )
                } else {
                    // Si no hay base de datos, redirigir al login
                    LaunchedEffect(Unit) {
                        navController.navigate(Routes.LOGIN_SCREEN)
                    }
                }
            }

        composable(Routes.SANMARKOS_SCREEN) {
            val db = appDatabase
            if (db != null) {
                val puntuazioaDao = remember { db.puntuazioaDao() }


                // Usar el factory de Papresa
                val viewModel: SanMarkosViewModel = viewModel(
                    factory = SanMarkosViewModelFactory(puntuazioaDao)
                )
            SanMarkosekoGalderak(
                navController = navController,
                userName = currentUserName,
                viewModel = viewModel
            )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN_SCREEN)
                }
            }
        }

        composable(Routes.TAULAARRASTRAR_SCRENN) {
            val db = appDatabase
            if (db != null) {
                val puntuazioaDao = remember { db.puntuazioaDao() }


                // Usar el factory de Papresa
                val viewModel: ArropaBuruHandiakViewModel = viewModel(
                    factory = ArropaBuruHandiakFactory(puntuazioaDao)
                )
            LaunchedEffect(currentUserName) {
                currentUserName?.let {
                    viewModel.setUsuario(it)
                }
            }
            TaulaArrastrarScreen(
                navController = navController,
                userName = currentUserName,
                viewModel = viewModel
            )
        } else {
        LaunchedEffect(Unit) {
            navController.navigate(Routes.LOGIN_SCREEN)
        }
    }
    }

        composable(Routes.GPS_SCREEN) {
            MapaOsmScreen(
                navController = navController
            )
        }

        composable(Routes.AJUSTES_SCREEN) {
            val db = appDatabase
            if (db != null) {
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

                val ajustesScope = rememberCoroutineScope()

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
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN_SCREEN)
                }
            }
        }
    }
}