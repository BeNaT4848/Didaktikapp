package com.example.errenteriaapp.ai

data class ChatMessage(
    val role: String,
    val content: String
)

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double = 0.7
)

data class ChatResponse(
    val choices: List<Choice>
) {
    data class Choice(
        val message: ChatMessage
    )
}

