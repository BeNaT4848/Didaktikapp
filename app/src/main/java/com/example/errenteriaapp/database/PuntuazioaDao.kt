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
}
