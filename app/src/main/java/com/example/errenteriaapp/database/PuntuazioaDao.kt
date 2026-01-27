package com.example.errenteriaapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PuntuazioaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(puntuazioa: Puntuazioa)

    @Query("SELECT * FROM Puntuazioa WHERE izenaAbizena = :name")
    suspend fun getByName(name: String): Puntuazioa?

    // Añade este método para obtener todas las puntuaciones
    @Query("SELECT * FROM Puntuazioa ORDER BY (puntuazioaBertso + puntuazioaGalderak + puntuazioaGurutzegrama + puntuazioaArropaBuruHandiak + puntuazioaPapresa + puntuazioaArrastrar + puntuazioaSopaLetra) DESC")
    suspend fun getAllSorted(): List<Puntuazioa>

    // O para obtener solo los top N
    @Query("SELECT * FROM Puntuazioa ORDER BY (puntuazioaBertso + puntuazioaGalderak + puntuazioaGurutzegrama + puntuazioaArropaBuruHandiak + puntuazioaPapresa + puntuazioaArrastrar + puntuazioaSopaLetra) DESC LIMIT :limit")
    suspend fun getTopRanking(limit: Int = 10): List<Puntuazioa>

    @Query("DELETE FROM Puntuazioa")
    suspend fun deleteAll()

    @Query("UPDATE Puntuazioa SET puntuazioaBertso = 0, puntuazioaGalderak = 0, puntuazioaGurutzegrama = 0, puntuazioaArropaBuruHandiak = 0, puntuazioaPapresa = 0, puntuazioaArrastrar = 0, puntuazioaSopaLetra = 0")
    suspend fun resetScores()
}
