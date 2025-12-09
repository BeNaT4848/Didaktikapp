package com.example.errenteriaapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PuntuazioRankinaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rankina: PuntuazioRankina)

    @Query("SELECT * FROM PuntuazioRankina")
    suspend fun getAll(): List<PuntuazioRankina>

    @Update
    suspend fun update(rankina: PuntuazioRankina)
}
