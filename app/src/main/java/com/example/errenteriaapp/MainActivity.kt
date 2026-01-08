package com.example.errenteriaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.errenteriaapp.navigation.AppNavigation
import com.example.errenteriaapp.database.viewModel.ConversacionViewModel
import com.example.errenteriaapp.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            val conversacionViewModel: ConversacionViewModel = viewModel()

            AppTheme(
                dynamicColor = false
            ) {
                AppNavigation(
                    navController = navController,
                    conversacionViewModel = conversacionViewModel,
                )
            }
        }
    }
}