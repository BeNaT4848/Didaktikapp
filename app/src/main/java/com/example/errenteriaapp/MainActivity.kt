package com.example.errenteriaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.errenteriaapp.components.AppScaffold
import com.example.errenteriaapp.navigation.AppNavigation
import com.example.errenteriaapp.ui.theme.ErrenteriaappTheme
import com.example.errenteriaapp.viewModel.ConversacionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ErrenteriaappTheme {
                val navController = rememberNavController()
                val conversacionViewModel: ConversacionViewModel = viewModel()

                AppScaffold(
                    navController = navController,
                    ) { padding ->
                    AppNavigation(
                        navController = navController,
                        conversacionViewModel = conversacionViewModel
                    )
                }
            }
        }
    }
}