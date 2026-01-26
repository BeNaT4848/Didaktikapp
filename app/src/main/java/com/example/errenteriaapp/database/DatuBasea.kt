package com.example.errenteriaapp.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Irakasle::class, Ikasle::class, Partida::class, Puntuazioa::class, PuntuazioRankina::class],
    version = 2
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
        database.execSQL("ALTER TABLE Irakasle ADD COLUMN contraseña TEXT DEFAULT 'irakasle2026'")

        // Actualizar los registros existentes con la contraseña por defecto
        database.execSQL("UPDATE Irakasle SET contraseña = 'irakasle2026' WHERE contraseña IS NULL")
    }
}