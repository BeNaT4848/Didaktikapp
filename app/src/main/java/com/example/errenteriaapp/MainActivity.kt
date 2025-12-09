package com.example.errenteriaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.errenteriaapp.database.AppDatabase
import com.example.errenteriaapp.database.MIGRATION_1_2
import com.example.errenteriaapp.navigation.AppNavigation
import com.example.errenteriaapp.ui.theme.ErrenteriaappTheme
import com.example.errenteriaapp.viewModel.ConversacionViewModel
import com.example.errenteriaapp.viewModel.LoginViewModel
import com.example.errenteriaapp.viewModel.LoginViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1️⃣ Crear la base de datos
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "errenteria_database"
        ).build()

        // Obtener DAOs
        val ikasleDao = db.ikasleDao()
        val irakasleDao = db.irakasleDao()

        // Crear ViewModel factories
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