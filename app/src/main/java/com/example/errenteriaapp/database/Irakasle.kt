package com.example.errenteriaapp.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey



@Entity(
    foreignKeys = [ForeignKey(
        entity = Irakasle::class,
        parentColumns = ["izenaAbizena"],
        childColumns = ["izenaAbizena"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Irakasle(
    @PrimaryKey val izenaAbizena: String,
    val rol: String = "Admin",
    val contraseña: String = "irakasle2026"
)
