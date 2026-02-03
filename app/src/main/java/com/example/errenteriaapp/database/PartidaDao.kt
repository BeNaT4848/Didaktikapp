package com.example.errenteriaapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Partidentzako Data Access Object interfazea, Partida entitatearen datu-base eragiketak definitzen ditu
 * @see Dao
 */
@Dao
interface PartidaDao {
    /**
     * Partida bat txertatzen edo existitzen bada ordezkatzen du datu-basean
     * @param partida Txertatu edo ordezkatu beharreko Partida objektua
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(partida: Partida)

    /**
     * Partidak bilatzen ditu ikasle baten izen eta abizenaren arabera, ordutegiaren arabera beheranzko ordenan
     * @param name Bilatu beharreko ikaslearen izen eta abizenak
     * @return Aurkitu diren Partida objektuen zerrenda, berriena lehenengo
     */
    @Query("SELECT * FROM Partida WHERE izenaAbizena = :name ORDER BY ordua DESC")
    suspend fun getByName(name: String): List<Partida> // Aldatu zerrendara partida guztiak ikusteko

    /**
     * Partida guztiak lortzen ditu ordutegiaren arabera beheranzko ordenan
     * @return Partida guztien zerrenda, berriena lehenengo
     */
    @Query("SELECT * FROM Partida ORDER BY ordua DESC")
    suspend fun getAll(): List<Partida>

    /**
     * Partida guztiak ezabatzen ditu datu-basetik
     */
    @Query("DELETE FROM Partida")
    suspend fun deleteAll()
}