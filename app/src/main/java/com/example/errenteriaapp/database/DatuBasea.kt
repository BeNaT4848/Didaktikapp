package com.example.errenteriaapp.database

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Aplikazioaren datu-base nagusia, Room datu-basea erabiltzen duena
 * @see RoomDatabase
 * @property entities Datu-basean gordetzen diren entitate klaseen zerrenda
 * @property version Datu-basearen bertsio zenbakia
 */
@Database(
    entities = [Irakasle::class, Ikasle::class, Partida::class, Puntuazioa::class, IzenTaldea::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Irakasleentzako Data Access Object lortzen du
     * @return IrakasleDao instantzia
     */
    abstract fun irakasleDao(): IrakasleDao

    /**
     * Ikasleentzako Data Access Object lortzen du
     * @return IkasleDao instantzia
     */
    abstract fun ikasleDao(): IkasleDao

    /**
     * Partidentzako Data Access Object lortzen du
     * @return PartidaDao instantzia
     */
    abstract fun partidaDao(): PartidaDao

    /**
     * Puntuazioentzako Data Access Object lortzen du
     * @return PuntuazioaDao instantzia
     */
    abstract fun puntuazioaDao(): PuntuazioaDao

    /**
     * Klase eta taldeentzako Data Access Object lortzen du
     * @return IzenTaldeaDao instantzia
     */
    abstract fun klaseakDao(): IzenTaldeaDao
}

/**
 * Bertsio 1-tik 2-rako migrazioa datu-basearen egitura aldatzean
 * @property contraseña eremua gehitzen du Irakasle entitateari
 */
val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
    override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
        // "contraseña" eremua gehitzen du Irakasle taulan
        database.execSQL("ALTER TABLE Irakasle ADD COLUMN contraseña TEXT DEFAULT 'irakasle2026'")

        // Existitzen diren erregistroak eguneratzen ditu pasahitz lehenetsiarekin
        database.execSQL("UPDATE Irakasle SET contraseña = 'irakasle2026' WHERE contraseña IS NULL")
    }
}