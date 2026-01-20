package com.example.errenteriaapp.components



import androidx.compose.ui.graphics.Color
import com.example.errenteriaapp.classes.RankingItem

import com.example.errenteriaapp.ui.theme.primaryLight

object RankingDataProvider {
    fun getRankingData(): List<RankingItem> {
        return listOf(
            RankingItem("Juan Pérez", 2450, Color(0xFFFFC107)),
            RankingItem("María García", 2200, Color(0xFF9E9E9E)),
            RankingItem("Carlos López", 2100, Color(0xFF8D6E63)),
            RankingItem("Ana Martínez", 1950, primaryLight),
            RankingItem("Luis Rodríguez", 1850, Color(0xFF2196F3)),
            RankingItem("Sofía Fernández", 1750, Color(0xFF9C27B0)),
            RankingItem("Diego Gómez", 1650, Color(0xFFFF9800)),
            RankingItem("Laura Hernández", 1550, Color(0xFF00BCD4)),
            RankingItem("Miguel Sánchez", 1450, Color(0xFF795548)),
            RankingItem("Elena Ramírez", 1350, Color(0xFF607D8B))
        )
    }

    fun getTopThree(): List<RankingItem> {
        return getRankingData().take(3)
    }

    fun getRestOfRanking(): List<RankingItem> {
        return getRankingData().drop(3)
    }
}