package com.example.errenteriaapp.ai

import android.content.Context
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ChatLogWriter {
    private const val LOG_FILE_NAME = "chat_log.txt"
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

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
