// kotlin
package com.example.errenteriaapp.model

import com.example.errenteriaapp.classes.Message

data class ConversationState(
    val currentMessage: Message? = null,
    val messages: List<Message> = emptyList(),
    val currentMessageIndex: Int = -1,
    val showStartButton: Boolean = false,
    val startButtonPressed: Boolean = false
)
