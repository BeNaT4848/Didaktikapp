package com.example.errenteriaapp.classes

import androidx.compose.ui.graphics.Color

/**
 * Pertsonaia bat irudikatzen duen enumerazioa.
 * @property displayName Pertsonaiaren erakusteko izena
 * @property backgroundColor Pertsonaiaren atzeko kolorea UI elementuetarako
 */
enum class Character(val displayName: String, val backgroundColor: Color) {
    /**
     * Xanti pertsonaia - urdin argi kolorearekin.
     */
    XANTI("XANTI", Color(0xFFE1F5FE)),

    /**
     * Maialen pertsonaia - more argi kolorearekin.
     */
    MAIALEN("MAIALEN", Color(0xFFF3E5F5))
}