package com.example.errenteriaapp.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Irakasle::class, Ikasle::class, Partida::class, Puntuazioa::class, PuntuazioRankina::class],
    version = 1
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun irakasleDao(): IrakasleDao
    abstract fun ikasleDao(): IkasleDao
    abstract fun partidaDao(): PartidaDao
    abstract fun puntuazioaDao(): PuntuazioaDao
    abstract fun puntuazioRankinaDao(): PuntuazioRankinaDao
}
val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
    override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
        // Ejecutar SQL directamente para agregar columna
        //
    }
}