package com.example.errenteriaapp.database

import androidx.room.Database
import androidx.room.RoomDatabase

// 2. Database
@Database(entities = [JugadorEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jugadorDao(): JugadorDAO
}