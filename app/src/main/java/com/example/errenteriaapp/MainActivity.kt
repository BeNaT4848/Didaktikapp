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

/**
 * Aplikazioaren sarrera nagusiko aktibitatea.
 * Hasiertako aktibitatea da eta nabigazioa eta aplikazioaren itxura kudeatzen ditu.
 *
 * @see AppNavigation
 * @see AppTheme
 * @see LanguageManager
 */
class MainActivity : ComponentActivity() {
    /**
     * Testuingurua inguratzen du hizkuntza kudeaketa gaitzeko.
     */
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageManager.wrapContext(newBase))
    }

    /**
     * Aktibitatea sortzean.
     * Hizkuntza eta itxura konfiguratzen ditu eta Compose UIa ezartzen du.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // Gordetako hizkuntza aplikatu setContent aurretik.
        LanguageManager.applySavedLanguage(this)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Nabigazio kontroladorea memorizatu
            val navController = rememberNavController()
            // Ilunpeko modua egoera aldakor gisa memorizatu
            var isDarkMode by rememberSaveable { mutableStateOf(false) }

            // Hizkuntza aldatzean, Compose zuhaitza osoki birkonstruitu
            // (Aktibitatea berriz sortu gabe). Honek stringResource(...) berriz ebaztea eragiten du.
            val langNonce = AppLanguageState.nonce

            // Hizkuntza aldaketak detektatzeko gakoa (Compose birkonposaketa eragiteko)
            key(langNonce) {
                // Aplikazioaren itxura (tema) ezarri
                AppTheme(darkTheme = isDarkMode, dynamicColor = false) {
                    // Pantaila osoa betetzen duen kutxa
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding()
                    ) {
                        // Nabigazio nagusia ezarri
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