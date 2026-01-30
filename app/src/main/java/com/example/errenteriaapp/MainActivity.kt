package com.example.errenteriaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.errenteriaapp.ai.AppContextHolder
import com.example.errenteriaapp.database.viewModel.ConversacionViewModel
import com.example.errenteriaapp.navigation.AppNavigation
import com.example.errenteriaapp.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        AppContextHolder.init(application)

        setContent {
            val navController = rememberNavController()
            val conversacionViewModel: ConversacionViewModel = viewModel()
            var isDarkMode by rememberSaveable { mutableStateOf(false) }

            AppTheme(darkTheme = isDarkMode, dynamicColor = false) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                ) {
                    AppNavigation(
                        navController = navController,
                        conversacionViewModel = conversacionViewModel,
                        isDarkMode = isDarkMode,
                        onThemeChange = { isDarkMode = it }
                    )
                }
            }
        }
    }
}