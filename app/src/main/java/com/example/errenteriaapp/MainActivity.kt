package com.example.errenteriaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.errenteriaapp.database.AppDatabase
import com.example.errenteriaapp.navigation.AppNavigation
import com.example.errenteriaapp.ui.theme.ErrenteriaappTheme
import com.example.errenteriaapp.database.viewModel.ConversacionViewModel
import com.example.errenteriaapp.database.viewModel.LoginViewModel
import com.example.errenteriaapp.database.viewModel.LoginViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Errenteriaapp)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Lanzar una corrutina ligada al ciclo de vida de la Activity
        lifecycleScope.launch {
            // Crear la base de datos en hilo de IO
            val db = withContext(Dispatchers.IO) {
                Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "errenteria_database"
                ).build()
            }

            // Obtener DAOs una vez creada la DB
            val ikasleDao = db.ikasleDao()
            val irakasleDao = db.irakasleDao()

            // Crear ViewModel factory
            val loginViewModelFactory = LoginViewModelFactory(ikasleDao, irakasleDao)

            setContent {
                val navController = rememberNavController()

                // Usar viewModel con factory
                val loginViewModel: LoginViewModel = viewModel(
                    factory = loginViewModelFactory
                )

                val conversacionViewModel: ConversacionViewModel = viewModel()

                ErrenteriaappTheme {
                    AppNavigation(
                        navController = navController,
                        conversacionViewModel = conversacionViewModel,
                        loginViewModel = loginViewModel
                    )
                }
            }
        }
    }
}