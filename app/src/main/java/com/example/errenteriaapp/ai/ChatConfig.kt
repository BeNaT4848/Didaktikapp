package com.example.errenteriaapp.ai

import com.example.errenteriaapp.BuildConfig

/**
 * Chat-a konfiguratzeko objektua.
 * Sistema-promptak, API giltza eta modeloa zehazten ditu.
 *
 * @since 1.0
 * @see [BuildConfig]
 */
// Chat-a konfiguratzeko objektua
object ChatConfig {
    /**
     * Ereduaren izena Groq APIan erabiltzeko.
     */
    const val MODEL = "llama-3.1-8b-instant"

    /**
     * Oinarrizko sistema-prompt euskaraz.
     * Errenteriari buruzko galderak erantzutera mugatzen du.
     */
    private const val BASE_SYSTEM_PROMPT_EU =
        "Errenteriari buruzko gaietarako laguntzaile bat zara (historia, lekuak, kultura, jaiak). " +
                "Galdera Errenteria ez bada, ukatu labur eta erabiltzailearen hizkuntzan."

    /**
     * Sistema-prompt osoa sortzen du erabiltzailearen izena eta hizkuntza iradokizuna kontuan hartuta.
     *
     * @param userName Erabiltzailearen izena (aukera)
     * @param languageHint Hizkuntzaren iradokizuna (aukera)
     * @return Sistemarako prompt osoa testu bakarrean
     *
     * @see [BASE_SYSTEM_PROMPT_EU]
     */
    // Sistema-prompt osoa sortzen du erabiltzailearen izena kontuan hartuta
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

    /**
     * Groq API giltza eskuratzen du BuildConfig-etik.
     *
     * @return API giltza edo kate hutsa errorearen kasuan
     * @throws [NoSuchFieldException] Eremua ez badago BuildConfig-en
     * @throws [IllegalAccessException] Eremua eskuragarri ez badago
     *
     * @see [BuildConfig]
     */
    // Groq API giltza eskuratzen du BuildConfig-etik
    fun apiKey(): String {
        return try {
            val field = BuildConfig::class.java.getField("GROQ_API_KEY")
            field.get(null)?.toString().orEmpty()
        } catch (_: Exception) {
            ""
        }
    }
}