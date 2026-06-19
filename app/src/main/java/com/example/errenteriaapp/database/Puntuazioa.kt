package com.example.errenteriaapp.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Puntuazio entitatearen datu-klasea, datu-baseko taula batekin mapeatzen duena
 * @see Entity
 * @property izenaAbizena Jokalariaren izen eta abizenak (gako nagusia)
 * @property puntuazioaBertso Bertso-jokoaren puntuazioa
 * @property puntuazioaGalderak Galdera-jokoaren puntuazioa
 * @property puntuazioaGurutzegrama Gurutzegrama-jokoaren puntuazioa
 * @property puntuazioaArropaBuruHandiak Arropa buru handiak jokoaren puntuazioa
 * @property puntuazioaPapresa Papresa-jokoaren puntuazioa
 * @property puntuazioaArrastrar Arrastrar (eramate) jokoaren puntuazioa
 * @property puntuazioaSopaLetra Sopa de letras jokoaren puntuazioa
 *
 * @param foreignKeys Atzerriko giltzak definitzen ditu Puntuazioa entitatearekin erlazionatzeko
 * @param entity Atzerriko giltzarekin erlazionatzen den entitatea
 * @param parentColumns Atzerriko giltzaren jatorriko eremuak
 * @param childColumns Atzerriko giltzaren helburuko eremuak
 * @param onDelete Atzerriko giltzaren jatorriko erregistroa ezabatzerakoan zein ekintza burutu (CASCADE: kateatutako ezabaketa)
 */
@Entity(
    foreignKeys = [ForeignKey(
        entity = Puntuazioa::class,
        parentColumns = ["izenaAbizena"],
        childColumns = ["izenaAbizena"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Puntuazioa(
    @PrimaryKey val izenaAbizena: String,
    val puntuazioaBertso: Int,
    val puntuazioaGalderak: Int,
    val puntuazioaGurutzegrama: Int,
    val puntuazioaArropaBuruHandiak: Int,
    val puntuazioaPapresa: Int,
    val puntuazioaArrastrar: Int,
    val puntuazioaSopaLetra: Int
)