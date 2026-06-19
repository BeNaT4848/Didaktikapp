package com.example.errenteriaapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Ikasleentzako Data Access Object interfazea, Ikasle entitatearen datu-base eragiketak definitzen ditu
 * @see Dao
 */
@Dao
interface IkasleDao {
    /**
     * Ikasle bat txertatzen edo existitzen bada ordezkatzen du datu-basean
     * @param ikasle Txertatu edo ordezkatu beharreko Ikasle objektua
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ikasle: Ikasle)

    /**
     * Ikasle bat bilatzen du bere izen eta abizenaren arabera
     * @param name Bilatu beharreko ikaslearen izen eta abizenak
     * @return Aurkitu den Ikasle objektua edo null ez bada aurkitu
     */
    @Query("SELECT * FROM Ikasle WHERE izenaAbizena = :name")
    suspend fun getByName(name: String): Ikasle?

    /**
     * Ikasle guztiak ezabatzen ditu datu-basetik
     */
    @Query("DELETE FROM Ikasle")
    suspend fun deleteAll()
}