package com.example.errenteriaapp.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey



@Entity(
    foreignKeys = [ForeignKey(
        entity = Ikasle::class,
        parentColumns = ["izenaAbizena"],
        childColumns = ["izenaAbizena"],
        onDelete = ForeignKey.CASCADE
    )]
)data class Partida(
    @PrimaryKey val izenaAbizena: String,
    val ordua: String // o usar LocalDateTime con TypeConverter
)