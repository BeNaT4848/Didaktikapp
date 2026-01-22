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
    @Query("SELECT * FROM Puntuazioa ORDER BY (puntuazioaBertso + puntuazioaGalderak + puntuazioaGurutzegrama + puntuazioaErrotaProzezua + puntuazioaPapresa + puntuazioaArrastrar + puntuazioaSopaLetra) DESC")
    suspend fun getAllSorted(): List<Puntuazioa>

    // O para obtener solo los top N
    @Query("SELECT * FROM Puntuazioa ORDER BY (puntuazioaBertso + puntuazioaGalderak + puntuazioaGurutzegrama + puntuazioaErrotaProzezua + puntuazioaPapresa + puntuazioaArrastrar + puntuazioaSopaLetra) DESC LIMIT :limit")
    suspend fun getTopRanking(limit: Int = 10): List<Puntuazioa>
}
