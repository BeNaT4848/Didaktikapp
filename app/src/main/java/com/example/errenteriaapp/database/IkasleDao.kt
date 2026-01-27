package com.example.errenteriaapp.database



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface IkasleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ikasle: Ikasle)

    @Query("SELECT * FROM Ikasle WHERE izenaAbizena = :name")
    suspend fun getByName(name: String): Ikasle?

    @Query("DELETE FROM Ikasle")
    suspend fun deleteAll()
}
