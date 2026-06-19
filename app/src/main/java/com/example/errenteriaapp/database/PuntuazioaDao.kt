package com.example.errenteriaapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Puntuazioentzako Data Access Object interfazea, Puntuazioa entitatearen datu-base eragiketak definitzen ditu
 * @see Dao
 */
@Dao
interface PuntuazioaDao {
    /**
     * Puntuazio bat txertatzen edo existitzen bada ordezkatzen du datu-basean
     * @param puntuazioa Txertatu edo ordezkatu beharreko Puntuazioa objektua
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(puntuazioa: Puntuazioa)

    /**
     * Puntuazio bat bilatzen du jokalari baten izen eta abizenaren arabera
     * @param name Bilatu beharreko jokalariaren izen eta abizenak
     * @return Aurkitu den Puntuazioa objektua edo null ez bada aurkitu
     */
    @Query("SELECT * FROM Puntuazioa WHERE izenaAbizena = :name")
    suspend fun getByName(name: String): Puntuazioa?

    /**
     * Puntuazio guztiak lortzen ditu puntuazio totalaren arabera beheranzko ordenan (rankinga)
     * @return Puntuazio guztien zerrenda, puntuazio altuena lehenengo
     */
    @Query("SELECT * FROM Puntuazioa ORDER BY (puntuazioaBertso + puntuazioaGalderak + puntuazioaGurutzegrama + puntuazioaArropaBuruHandiak + puntuazioaPapresa + puntuazioaArrastrar + puntuazioaSopaLetra) DESC")
    suspend fun getAllSorted(): List<Puntuazioa>

    /**
     * Puntuazio altuenak lortzen ditu mugatutako kopuru batean (ranking goikoa)
     * @param limit Erakutsiko diren jokalarien kopurua (lehenetsia: 10)
     * @return Puntuazio altuenen zerrenda, mugatutako kopuruan
     */
    @Query("SELECT * FROM Puntuazioa ORDER BY (puntuazioaBertso + puntuazioaGalderak + puntuazioaGurutzegrama + puntuazioaArropaBuruHandiak + puntuazioaPapresa + puntuazioaArrastrar + puntuazioaSopaLetra) DESC LIMIT :limit")
    suspend fun getTopRanking(limit: Int = 10): List<Puntuazioa>

    /**
     * Puntuazio guztiak ezabatzen ditu datu-basetik
     */
    @Query("DELETE FROM Puntuazioa")
    suspend fun deleteAll()

    /**
     * Puntuazio guztien balioak berrabiarazten ditu zerora (puntuazioak berrabiarazteko)
     */
    @Query("UPDATE Puntuazioa SET puntuazioaBertso = 0, puntuazioaGalderak = 0, puntuazioaGurutzegrama = 0, puntuazioaArropaBuruHandiak = 0, puntuazioaPapresa = 0, puntuazioaArrastrar = 0, puntuazioaSopaLetra = 0")
    suspend fun resetScores()
}