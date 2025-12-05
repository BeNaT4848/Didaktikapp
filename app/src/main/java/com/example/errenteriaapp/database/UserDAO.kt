package com.example.errenteriaapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface JugadorDAO {
    @Insert
    suspend fun insert(jugador: JugadorEntity)

    @Query("SELECT * FROM jugadores ORDER BY nombre ASC")
    suspend fun getAll(): List<JugadorEntity>
}

