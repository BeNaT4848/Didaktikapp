package com.example.errenteriaapp.database




import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import java.net.InetAddress
import java.net.InetAddress.getByName

@Dao
interface IrakasleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(irakasle: Irakasle)

    @Query("SELECT * FROM Irakasle")
    suspend fun getAll(): List<Irakasle>

    @Query("SELECT * FROM Irakasle WHERE izenaAbizena = :name")
    suspend fun getByName(name: String): Irakasle?

    @Transaction
    suspend fun insertarIrakasleSiNoExiste(name: String = "Irakasle", rol: String = "Admin",  contraseña: String = "irakasle2026" ) {
        val existente = getByName(name)
        if (existente == null) {
            val nuevoIrakasle = Irakasle(izenaAbizena = name, rol = rol,  contraseña = contraseña)
            insert(nuevoIrakasle)
        }
    }
}
