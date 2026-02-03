package com.example.errenteriaapp.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Izena eta taldea erlazionatzeko entitatearen datu-klasea, datu-baseko taula batekin mapeatzen duena
 * @see Entity
 * @property id Erregistroaren identifikatzaile bakarra (gako nagusia, automatikoki sortua)
 * @property izenaAbizena Ikaslearen izen eta abizenak
 * @property taldea Ikaslearen talde edo klasea
 *
 * @param foreignKeys Atzerriko giltzak definitzen ditu Ikasle entitatearekin erlazionatzeko
 * @param entity Atzerriko giltzarekin erlazionatzen den entitatea
 * @param parentColumns Atzerriko giltzaren jatorriko eremuak
 * @param childColumns Atzerriko giltzaren helburuko eremuak
 * @param onDelete Atzerriko giltzaren jatorriko erregistroa ezabatzerakoan zein ekintza burutu (CASCADE: kateatutako ezabaketa)
 */
@Entity(
    foreignKeys = [ForeignKey(
        entity = Ikasle::class,
        parentColumns = ["izenaAbizena"],
        childColumns = ["izenaAbizena"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class IzenTaldea(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val izenaAbizena: String,
    val taldea: String
)