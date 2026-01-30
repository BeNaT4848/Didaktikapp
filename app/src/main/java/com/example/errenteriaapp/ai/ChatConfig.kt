package com.example.errenteriaapp.ai

import com.example.errenteriaapp.BuildConfig

object ChatConfig {
    const val MODEL = "llama-3.1-8b-instant"

    private const val BASE_SYSTEM_PROMPT_EU =
        "Errenteriari buruzko gaietarako laguntzaile bat zara (historia, lekuak, kultura, jaiak). " +
            "Galdera Errenteria ez bada, ukatu labur eta erabiltzailearen hizkuntzan."

    fun systemPrompt(userName: String?, languageHint: String?): String {
        val nameLine = if (!userName.isNullOrBlank()) {
            "Erabiltzailea $userName da."
        } else {
            ""
        }
        return listOf(
            BASE_SYSTEM_PROMPT_EU,
            "Erantzun beti euskaraz.",
            "Erabili euskara zuzena eta esaldi laburrak. Ez nahastu beste hizkuntzak.",
            nameLine
        )
            .filter { it.isNotBlank() }
            .joinToString(" ")
    }

    fun apiKey(): String {
        return try {
            val field = BuildConfig::class.java.getField("GROQ_API_KEY")
            field.get(null)?.toString().orEmpty()
        } catch (_: Exception) {
            ""
        }
    }
}
