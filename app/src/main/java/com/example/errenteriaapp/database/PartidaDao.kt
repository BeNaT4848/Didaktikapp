package com.example.errenteriaapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PartidaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(partida: Partida)

    @Query("SELECT * FROM Partida WHERE izenaAbizena = :name ORDER BY ordua DESC")
    suspend fun getByName(name: String): List<Partida> // Cambiar a lista para ver todas las partidas

    @Query("SELECT * FROM Partida ORDER BY ordua DESC")
    suspend fun getAll(): List<Partida>
}
