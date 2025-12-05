// kotlin
package com.example.errenteriaapp.model

data class ConversationState(
    val currentMessage: Message? = null,
    val messages: List<Message> = emptyList(),
    val currentMessageIndex: Int = -1,
    val showStartButton: Boolean = false
)
