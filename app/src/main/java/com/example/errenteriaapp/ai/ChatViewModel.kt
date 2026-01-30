package com.example.errenteriaapp.ai

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import retrofit2.HttpException
import kotlinx.coroutines.delay

data class ChatUiMessage(
    val id: String,
    val text: String,
    val isUser: Boolean
)

data class ChatUiState(
    val messages: List<ChatUiMessage> = emptyList(),
    val input: String = "",
    val isSending: Boolean = false,
    val error: String? = null
)

class ChatViewModel : ViewModel() {
    private val repository = ChatRepository(ChatConfig.apiKey())
    private val userName: String? = readUserName()

    private val _uiState = MutableStateFlow(
        ChatUiState(
            messages = listOf(
                ChatUiMessage(
                    id = UUID.randomUUID().toString(),
                    text = "Kaixo! Zer nahi duzu galdetu?",
                    isUser = false
                )
            )
        )
    )
    val uiState: StateFlow<ChatUiState> = _uiState

    fun onInputChange(value: String) {
        _uiState.value = _uiState.value.copy(input = value, error = null)
    }

    fun onSend() {
        val text = _uiState.value.input.trim()
        if (text.isEmpty() || _uiState.value.isSending) return

        val userMessage = ChatUiMessage(
            id = UUID.randomUUID().toString(),
            text = text,
            isUser = true
        )

        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            input = "",
            isSending = true,
            error = null
        )

        viewModelScope.launch {
            try {
                val apiMessages = buildApiMessages(_uiState.value.messages)
                val reply = repository.send(apiMessages)
                delay(2500)
                val assistantMessage = ChatUiMessage(
                    id = UUID.randomUUID().toString(),
                    text = reply,
                    isUser = false
                )
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + assistantMessage,
                    isSending = false
                )
            } catch (e: Exception) {
                val message = when (e) {
                    is HttpException -> when (e.code()) {
                        401 -> "GROQ_API_KEY okerra edo baliogabea."
                        429 -> "Muga/kuota gaindituta. Aktibatu billing-a edo itxaron pixka bat."
                        else -> "Errorea HTTP ${e.code()}."
                    }
                    else -> "Errorea: ${e.message ?: "ezezaguna"}"
                }
                _uiState.value = _uiState.value.copy(
                    isSending = false,
                    error = message
                )
            }
        }
    }

    private fun buildApiMessages(uiMessages: List<ChatUiMessage>): List<ChatMessage> {
        val languageHint = detectLanguage(uiMessages.lastOrNull { it.isUser }?.text)
        val systemPrompt = ChatConfig.systemPrompt(userName, languageHint)
        val messages = mutableListOf(ChatMessage("system", systemPrompt))
        uiMessages.forEach { msg ->
            messages.add(
                ChatMessage(
                    role = if (msg.isUser) "user" else "assistant",
                    content = msg.text
                )
            )
        }
        return messages
    }

    private fun detectLanguage(text: String?): String? {
        if (text.isNullOrBlank()) return null
        val normalized = normalize(text)

        val basqueKeywords = listOf(
            "kaixo", "eskerrik", "mesedez", "zer", "nola", "nahi", "zara", "naiz", "eta", "non", "nor",
            "errenteria", "errenterian", "herria", "kalea", "plaza", "jaia", "jaiak", "mendi"
        )
        val spanishKeywords = listOf(
            "hola", "gracias", "por favor", "que", "como", "quiero", "eres", "soy", "y", "donde", "quien",
            "necesito", "puedes", "podrias", "explica", "dime", "cuando", "porque", "errenteria",
            "calle", "plaza", "fiesta", "fiestas", "monte"
        )
        val englishKeywords = listOf(
            "hello", "thanks", "please", "what", "how", "i want", "you are", "i am", "and", "where", "who",
            "errenteria", "street", "square", "festival", "festivals", "mountain"
        )

        val hasBasque = basqueKeywords.any { normalized.contains(it) }
        val hasSpanish = spanishKeywords.any { normalized.contains(it) }
        val hasEnglish = englishKeywords.any { normalized.contains(it) }

        return when {
            hasBasque && !hasSpanish && !hasEnglish -> "eu"
            hasSpanish && !hasBasque && !hasEnglish -> "es"
            hasEnglish && !hasBasque && !hasSpanish -> "en"
            else -> null
        }
    }

    private fun normalize(text: String): String {
        return text.lowercase()
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
            .replace("ü", "u")
            .replace("ñ", "n")
    }

    private fun readUserName(): String? {
        return try {
            val context = AppContextHolder.appContext ?: return null
            val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
            prefs.getString("active_user_name", null)
        } catch (_: Exception) {
            null
        }
    }
}
