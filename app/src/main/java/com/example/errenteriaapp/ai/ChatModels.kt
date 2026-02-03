package com.example.errenteriaapp.ai

/**
 * Txat-mezua irudikatzeko datu-klasea.
 *
 * @property role Mezuaren rola ("user", "assistant", "system")
 * @property content Mezuaren edukia
 *
 * @see [ChatRequest]
 * @see [ChatResponse]
 */
// Txat-mezua irudikatzeko datu-klasea
data class ChatMessage(
    val role: String,
    val content: String
)

/**
 * Txat-eskaera bat irudikatzeko datu-klasea.
 * APIra bidaltzeko eskaeraren egitura definitzen du.
 *
 * @property model Ereduaren izena
 * @property messages Txat-mezuen zerrenda
 * @property temperature Ausazkotasun-maila (lehenetsia: 0.7)
 *
 * @see [ChatMessage]
 * @see [ChatApi.chatCompletions]
 */
// Txat-eskaera bat irudikatzeko datu-klasea
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double = 0.7
)

/**
 * Txat-erantzuna irudikatzeko datu-klasea.
 * APItik jasotako erantzunaren egitura definitzen du.
 *
 * @property choices Aukeren zerrenda (erantzun posibleak)
 *
 * @see [Choice]
 * @see [ChatApi.chatCompletions]
 */
// Txat-erantzuna irudikatzeko datu-klasea
data class ChatResponse(
    val choices: List<Choice>
) {
    /**
     * Aukera indibidual bat txat-erantzunaren barruan.
     *
     * @property message Aukerarekin lotutako txat-mezua
     *
     * @see [ChatMessage]
     */
    // Aukera indibidual bat txat-erantzunaren barruan
    data class Choice(
        val message: ChatMessage
    )
}