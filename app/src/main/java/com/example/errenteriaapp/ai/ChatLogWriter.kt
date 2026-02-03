package com.example.errenteriaapp.ai

import android.content.Context
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Txat-mezuak fitxategi batean idazteko objektua.
 * Denbora-marka eta rola gehitzen ditu erregistro bakoitzean.
 *
 * @since 1.0
 * @see [Context.getExternalFilesDir]
 * @see [LocalDateTime]
 */
// Txat-mezuak fitxategi batean idazteko objektua
object ChatLogWriter {
    /**
     * Erregistro-fitxategiaren izena.
     */
    private const val LOG_FILE_NAME = "chat_log.txt"

    /**
     * Denbora-marka formatatzeko formatatzailea.
     */
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    /**
     * Mezu berri bat gehitzen du erregistro-fitxategiaren amaieran.
     *
     * @param context Aplikazioaren testuingurua
     * @param role Mezuaren rola ("user", "assistant", "system")
     * @param message Gorde beharreko mezua
     *
     * @throws [java.io.IOException] Fitxategia idazteko erroreak
     * @see [Context.getExternalFilesDir]
     */
    // Mezu berri bat gehitzen du erregistro-fitxategiaren amaieran
    fun appendMessage(context: Context, role: String, message: String) {
        val safeRole = role.ifBlank { "unknown" }
        val timestamp = LocalDateTime.now().format(formatter)
        val line = "[$timestamp] [$safeRole] $message\n"

        val externalDir = context.getExternalFilesDir(null)
        val logDir = externalDir ?: context.filesDir
        val logFile = File(logDir, LOG_FILE_NAME)

        logFile.appendText(line)
    }
}