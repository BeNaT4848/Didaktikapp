package com.example.errenteriaapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.errenteriaapp.i18n.AppLanguageState
import com.example.errenteriaapp.i18n.LanguageManager
import com.example.errenteriaapp.navigation.AppNavigation
import com.example.errenteriaapp.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageManager.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Aplica el idioma persistido antes de setContent.
        LanguageManager.applySavedLanguage(this)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            var isDarkMode by rememberSaveable { mutableStateOf(false) }

            // Cada vez que cambie el idioma, reconstruimos el árbol Compose completo
            // (sin recrear Activity). Esto hace que stringResource(...) se vuelva a resolver.
            val langNonce = AppLanguageState.nonce

            key(langNonce) {
                AppTheme(darkTheme = isDarkMode, dynamicColor = false) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding()
                    ) {
                        AppNavigation(
                            navController = navController,
                            isDarkMode = isDarkMode,
                            onThemeChange = { isDarkMode = it }
                        )
                    }
                }
            }
        }
    }
}