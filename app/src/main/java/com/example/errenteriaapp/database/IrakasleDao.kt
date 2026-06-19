package com.example.errenteriaapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow


/**
 * Irakasleentzako Data Access Object interfazea, Irakasle entitatearen datu-base eragiketak definitzen ditu
 * @see Dao
 */
@Dao
interface IrakasleDao {
    /**
     * Irakasle bat txertatzen edo existitzen bada ordezkatzen du datu-basean
     * @param irakasle Txertatu edo ordezkatu beharreko Irakasle objektua
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(irakasle: Irakasle)

    /**
     * Irakasle guztiak lortzen ditu Fluxu batean
     * @return Irakasleen zerrenda Flow moduan
     */
    @Query("SELECT * FROM Irakasle")
    fun getAll(): Flow<List<Irakasle>>

    /**
     * Irakasle bat bilatzen du bere izen eta abizenaren arabera
     * @param name Bilatu beharreko irakaslearen izen eta abizenak
     * @return Aurkitu den Irakasle objektua edo null ez bada aurkitu
     */
    @Query("SELECT * FROM Irakasle WHERE izenaAbizena = :name")
    suspend fun getByName(name: String): Irakasle?

    /**
     * Irakasle bat txertatzen du existitzen ez bada (transakzio atoma)
     * @param name Irakaslearen izen eta abizenak (lehenetsia: "irakasle")
     * @param rol Irakaslearen rola (lehenetsia: "Admin")
     * @param contraseña Irakaslearen pasahitza (lehenetsia: "irakasle2026")
     */
    @Transaction
    suspend fun insertarIrakasleSiNoExiste(name: String = "irakasle", rol: String = "Admin", contraseña: String = "irakasle2026") {
        val existente = getByName(name)
        if (existente == null) {
            val nuevoIrakasle = Irakasle(izenaAbizena = name, rol = rol, contraseña = contraseña)
            insert(nuevoIrakasle)
        }
    }
}