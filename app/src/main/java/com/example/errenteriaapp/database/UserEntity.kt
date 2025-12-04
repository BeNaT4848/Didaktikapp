package com.example.errenteriaapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "jugadores")
data class JugadorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,      // Ej: "Carlos", "Sofía", "Pedro"
    val apellido: String     // Ej: "Sanchez", "Garcia", "Contreras"
)
