package com.example.errenteriaapp.classes

import androidx.compose.ui.graphics.Color

/**
 * Rankingaren elementu bat irudikatzen du.
 *
 * @property name Erabiltzailearen izena
 * @property points Erabiltzailearen puntuazioa
 * @property color Elementuaren kolorea ranking-taulan erakusteko
 */
data class RankingItem(
    val name: String,
    val points: Int,
    val color: Color
)