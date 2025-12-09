package com.example.errenteriaapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PuntuazioRankina(
    @PrimaryKey val izenaAbizena: String,
    val puntuazioaIkuzi: Int,
    val izenaAbizenakKonfigurat: String? // Solo editable por admin
)