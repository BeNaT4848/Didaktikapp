package com.example.errenteriaapp.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey




@Entity(
    foreignKeys = [ForeignKey(
        entity = Puntuazioa::class,
        parentColumns = ["izenaAbizena"],
        childColumns = ["izenaAbizena"],
        onDelete = ForeignKey.CASCADE
    )]
)data class Puntuazioa(
    @PrimaryKey val izenaAbizena: String,
    val puntuazioaBertso: Int,
    val puntuazioaGalderak: Int,
    val puntuazioaGurutzegrama: Int,
    val puntuazioaArropaBuruHandiak: Int,
    val puntuazioaPapresa: Int,
    val puntuazioaArrastrar: Int,
    val puntuazioaSopaLetra: Int
)