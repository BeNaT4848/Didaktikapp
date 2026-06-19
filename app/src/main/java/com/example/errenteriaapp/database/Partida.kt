package com.example.errenteriaapp.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Partida entitatearen datu-klasea, datu-baseko taula batekin mapeatzen duena
 * @see Entity
 * @property izenaAbizena Ikaslearen izen eta abizenak (gako nagusia)
 * @property ordua Partida hasi zen ordua (String moduan gordeta, TypeConverter erabili daiteke LocalDateTime bihurtzeko)
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
data class Partida(
    @PrimaryKey val izenaAbizena: String,
    val ordua: String // edo LocalDateTime erabili TypeConverter-ekin
)