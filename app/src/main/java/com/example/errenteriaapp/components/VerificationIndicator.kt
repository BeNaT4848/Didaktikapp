package com.example.errenteriaapp.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.errenteriaapp.classes.CeldaEstado

@Composable
fun VerificationIndicator(celdas: List<CeldaEstado>) {
    val correctas = celdas.count { !it.esNegra && it.esCorrecta }
    val totales = celdas.count { !it.esNegra }

    Text(
        text = "Letras correctas: $correctas / $totales",
        color = Color(0xFFFF9800),
        fontWeight = FontWeight.Bold
    )
}