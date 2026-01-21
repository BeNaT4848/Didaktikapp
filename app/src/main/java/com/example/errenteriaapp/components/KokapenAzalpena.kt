package com.example.errenteriaapp.components

import androidx.compose.runtime.Composable
import com.example.errenteriaapp.classes.Kokapena
import com.example.errenteriaapp.components.ReusableModalBottomSheet
import com.example.errenteriaapp.navigation.Routes
import com.example.errenteriaapp.navigation.screens.azalpenOrriak.*

@Composable
fun KokapenaAzalpen(
    kokapena: Kokapena,
    navController: androidx.navigation.NavController,
    onClose: () -> Unit,
    onNavigateToGame: (route: String) -> Unit
) {
    when (kokapena.route) {
        Routes.BERTSOJOLASA_SCREEN -> AzalpenBertso(
            onClose = onClose,
            onNavigateToGame = { onNavigateToGame(Routes.BERTSOJOLASA_SCREEN) }
        )

        Routes.SANMARKOS_SCREEN -> AzalpenSanMarkos(
            onClose = onClose,
            onNavigateToGame = { onNavigateToGame(Routes.SANMARKOS_SCREEN) }
        )

        Routes.ORDENATUJOLASA_SCREEN -> AzalpenOrdenatu(
            onClose = onClose,
            onNavigateToGame = { onNavigateToGame(Routes.ORDENATUJOLASA_SCREEN) }
        )

        Routes.PUZZLE_SCREEN -> AzalpenPapresa(
            onClose = onClose,
            onNavigateToGame = { onNavigateToGame(Routes.PUZZLE_SCREEN) }
        )

        Routes.SOPALETRA_SCREEN -> AzalpenLetraZopa(
            onClose = onClose,
            onNavigateToGame = { onNavigateToGame(Routes.SOPALETRA_SCREEN) }
        )

        Routes.TAULAARRASTRAR_SCRENN -> AzalpenArrastrar(
            onClose = onClose,
            onNavigateToGame = { onNavigateToGame(Routes.TAULAARRASTRAR_SCRENN) }
        )

        Routes.CRUCIGRAMA_SCREEN -> AzalpenCrucigrama(
            onClose = onClose,
            onNavigateToGame = { onNavigateToGame(Routes.CRUCIGRAMA_SCREEN) }
        )
    }
}