// kotlin
package com.example.errenteriaapp.classes



data class ConversationState(
    val currentMessage: Dialogo? = null,
    val messages: List<Dialogo> = emptyList(),
    val currentMessageIndex: Int = -1,
    val showStartButton: Boolean = false,
    val startButtonPressed: Boolean = false
)
