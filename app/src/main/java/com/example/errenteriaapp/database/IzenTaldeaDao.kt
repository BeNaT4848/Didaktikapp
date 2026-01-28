package com.example.errenteriaapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface IzenTaldeaDao {
    @Insert
    suspend fun insert(izenTaldea: IzenTaldea)

    @Update
    suspend fun update(izenTaldea: IzenTaldea)

    @Delete
    suspend fun delete(izenTaldea: IzenTaldea)

    @Query("SELECT * FROM IzenTaldea WHERE izenaAbizena = :izenaAbizena")
    suspend fun getByIzenaAbizena(izenaAbizena: String): IzenTaldea?

    @Query("SELECT * FROM IzenTaldea WHERE taldea = :taldea")
    fun getByTaldea(taldea: String): Flow<List<IzenTaldea>>

    @Query("SELECT * FROM IzenTaldea")
    fun getAll(): Flow<List<IzenTaldea>>

    @Query("DELETE FROM IzenTaldea WHERE izenaAbizena = :izenaAbizena")
    suspend fun deleteByIzenaAbizena(izenaAbizena: String)
}