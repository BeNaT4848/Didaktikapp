package com.example.errenteriaapp.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Irakasle entitatearen datu-klasea, datu-baseko taula batekin mapeatzen duena
 * @see Entity
 * @property izenaAbizena Irakaslearen izen eta abizenak (gako nagusia)
 * @property rol Irakaslearen rol edo funtzioa (lehenetsia: "Admin")
 * @property contraseña Irakaslearen saioa hasteko pasahitza (lehenetsia: "irakasle2026")
 *
 * @param foreignKeys Atzerriko giltzak definitzen ditu Irakasle entitatearekin erlazionatzeko
 * @param entity Atzerriko giltzarekin erlazionatzen den entitatea
 * @param parentColumns Atzerriko giltzaren jatorriko eremuak
 * @param childColumns Atzerriko giltzaren helburuko eremuak
 * @param onDelete Atzerriko giltzaren jatorriko erregistroa ezabatzerakoan zein ekintza burutu (CASCADE: kateatutako ezabaketa)
 */
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