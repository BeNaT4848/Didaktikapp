package com.example.errenteriaapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

/**
 * IzenTaldea entitatearentzako Data Access Object interfazea, izenak eta taldeak erlazionatzeko datu-base eragiketak definitzen ditu
 * @see Dao
 */
@Dao
interface IzenTaldeaDao {
    /**
     * IzenTaldea erregistro bat txertatzen du datu-basean
     * @param izenTaldea Txertatu beharreko IzenTaldea objektua
     */
    @Insert
    suspend fun insert(izenTaldea: IzenTaldea)

    /**
     * IzenTaldea erregistro bat eguneratzen du datu-basean
     * @param izenTaldea Eguneratu beharreko IzenTaldea objektua
     */
    @Update
    suspend fun update(izenTaldea: IzenTaldea)

    /**
     * IzenTaldea erregistro bat ezabatzen du datu-basetik
     * @param izenTaldea Ezabatu beharreko IzenTaldea objektua
     */
    @Delete
    suspend fun delete(izenTaldea: IzenTaldea)

    /**
     * IzenTaldea erregistro bat bilatzen du izen eta abizenaren arabera
     * @param izenaAbizena Bilatu beharreko ikaslearen izen eta abizenak
     * @return Aurkitu den IzenTaldea objektua edo null ez bada aurkitu
     */
    @Query("SELECT * FROM IzenTaldea WHERE izenaAbizena = :izenaAbizena")
    suspend fun getByIzenaAbizena(izenaAbizena: String): IzenTaldea?

    /**
     * IzenTaldea erregistro guztiak lortzen ditu talde jakin batean Fluxu batean
     * @param taldea Bilatu beharreko taldearen izena
     * @return Talde horretako IzenTaldea objektuen zerrenda Flow moduan
     */
    @Query("SELECT * FROM IzenTaldea WHERE taldea = :taldea")
    fun getByTaldea(taldea: String): Flow<List<IzenTaldea>>

    /**
     * IzenTaldea erregistro guztiak lortzen ditu Fluxu batean
     * @return IzenTaldea objektuen zerrenda Flow moduan
     */
    @Query("SELECT * FROM IzenTaldea")
    fun getAll(): Flow<List<IzenTaldea>>

    /**
     * IzenTaldea erregistro bat ezabatzen du izen eta abizenaren arabera
     * @param izenaAbizena Ezabatu beharreko ikaslearen izen eta abizenak
     */
    @Query("DELETE FROM IzenTaldea WHERE izenaAbizena = :izenaAbizena")
    suspend fun deleteByIzenaAbizena(izenaAbizena: String)
}