package com.example.errenteriaapp.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.errenteriaapp.R
import com.example.errenteriaapp.classes.CeldaEstado

/**
 * Gurutze-hitzaren egiaztapen-adierazlea erakusten du.
 * Zenbat letra zuzen dauden eta zenbat dauden guztira erakusten du.
 *
 * @param celdas Zelda guztien zerrenda
 */
@Composable
fun VerificationIndicator(celdas: List<CeldaEstado>) {
    val correctas = celdas.count { !it.esNegra && it.esCorrecta }
    val totales = celdas.count { !it.esNegra }

    Text(
        text = stringResource(R.string.game_crucigrama_correct_letters, correctas, totales),
        color = Color(0xFFFF9800),
        fontWeight = FontWeight.Bold
    )
}