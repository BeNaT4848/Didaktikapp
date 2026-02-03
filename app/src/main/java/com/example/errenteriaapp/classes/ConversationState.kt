package com.example.errenteriaapp.classes

/**
 * Elkarrizketaren egoera gordetzen du.
 *
 * @property currentMessage Uneko mezua, baldin badago
 * @property messages Elkarrizketako mezu guztien zerrenda
 * @property currentMessageIndex Mezu-zerrendako uneko posizioa (-1 = hasieran)
 * @property showStartButton "Hasi" botoia erakutsi behar den
 * @property startButtonPressed "Hasi" botoia sakatu den
 */
data class ConversationState(
    val currentMessage: Dialogo? = null,
    val messages: List<Dialogo> = emptyList(),
    val currentMessageIndex: Int = -1,
    val showStartButton: Boolean = false,
    val startButtonPressed: Boolean = false
)