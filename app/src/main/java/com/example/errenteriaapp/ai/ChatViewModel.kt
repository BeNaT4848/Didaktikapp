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

/**
 * Mezu bat irudikatzen du erabiltzaile-interfazean.
 * @property id Mezuaren identifikadore bakarra
 * @property text Mezuaren edukia
 * @property isUser Erabiltzailearen mezua den ala ez (false bada, laguntzailearena da)
 */
data class ChatUiMessage(
    val id: String,
    val text: String,
    val isUser: Boolean
)

/**
 * Txat-interfazearen egoera gordetzen du.
 * @property messages Mezu zerrenda
 * @property input Erabiltzaileak idatzitako testua
 * @property isSending Mezua bidaltzen ari den ala ez
 * @property error Akats-mezua, baldin badago
 */
data class ChatUiState(
    val messages: List<ChatUiMessage> = emptyList(),
    val input: String = "",
    val isSending: Boolean = false,
    val error: String? = null
)

/**
 * Txataren logika kudeatzen duen ViewModel-a.
 * @see ViewModel
 */
class ChatViewModel : ViewModel() {
    // Depedenziak
    private val repository = ChatRepository(ChatConfig.apiKey())
    private val userName: String? = readUserName()
    private val appContext: Context? get() = AppContextHolder.appContext

    // Egoera kudeaketa
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

    /** Txataren egoeraren fluxu publikoa */
    val uiState: StateFlow<ChatUiState> = _uiState

    /**
     * Sarrera-testua aldatzen denean deitzen da.
     * @param value Testu berria
     */
    fun onInputChange(value: String) {
        _uiState.value = _uiState.value.copy(input = value, error = null)
    }

    /**
     * Mezua bidaltzeko botoian klik egitean deitzen da.
     * Erabiltzailearen mezua gehitzen du eta API-ra bidaltzen du.
     */
    fun onSend() {
        val text = _uiState.value.input.trim()
        if (text.isEmpty() || _uiState.value.isSending) return

        // Erabiltzailearen mezua sortu
        val userMessage = ChatUiMessage(
            id = UUID.randomUUID().toString(),
            text = text,
            isUser = true
        )

        // Egoerari erabiltzailearen mezua gehitu
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            input = "",
            isSending = true,
            error = null
        )

        // Mezua erregistroan gorde
        appContext?.let { ctx ->
            ChatLogWriter.appendMessage(ctx, "user", text)
        }

        // API dei asinkronoa egin
        viewModelScope.launch {
            try {
                val apiMessages = buildApiMessages(_uiState.value.messages)
                val reply = repository.send(apiMessages)
                delay(2500) // Erantzunaren efektua simulatu

                // Laguntzailearen mezua sortu
                val assistantMessage = ChatUiMessage(
                    id = UUID.randomUUID().toString(),
                    text = reply,
                    isUser = false
                )

                // Egoerari laguntzailearen mezua gehitu
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + assistantMessage,
                    isSending = false
                )

                // Mezua erregistroan gorde
                appContext?.let { ctx ->
                    ChatLogWriter.appendMessage(ctx, "assistant", reply)
                }
            } catch (e: Exception) {
                // Akatsak kudeatu
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

    /**
     * UI mezutik API mezutara bihurtzen ditu.
     * @param uiMessages UI mezuen zerrenda
     * @return API mezuen zerrenda
     * @see ChatMessage
     */
    private fun buildApiMessages(uiMessages: List<ChatUiMessage>): List<ChatMessage> {
        // Hizkuntza detektatu
        val languageHint = detectLanguage(uiMessages.lastOrNull { it.isUser }?.text)
        val systemPrompt = ChatConfig.systemPrompt(userName, languageHint)

        // Mezuen zerrenda eraiki
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

    /**
     * Testuan erabilitako hizkuntza detektatzen du gako-hitzak erabiliz.
     * @param text Analizatzeko testua
     * @return "eu", "es", "en" edo null detektatutako hizkuntza
     */
    private fun detectLanguage(text: String?): String? {
        if (text.isNullOrBlank()) return null
        val normalized = normalize(text)

        // Euskarazko gako-hitzak
        val basqueKeywords = listOf(
            "kaixo", "eskerrik", "mesedez", "zer", "nola", "nahi", "zara", "naiz", "eta", "non", "nor",
            "errenteria", "errenterian", "herria", "kalea", "plaza", "jaia", "jaiak", "mendi"
        )

        // Gaztelaniazko gako-hitzak
        val spanishKeywords = listOf(
            "hola", "gracias", "por favor", "que", "como", "quiero", "eres", "soy", "y", "donde", "quien",
            "necesito", "puedes", "podrias", "explica", "dime", "cuando", "porque", "errenteria",
            "calle", "plaza", "fiesta", "fiestas", "monte"
        )

        // Ingeleseko gako-hitzak
        val englishKeywords = listOf(
            "hello", "thanks", "please", "what", "how", "i want", "you are", "i am", "and", "where", "who",
            "errenteria", "street", "square", "festival", "festivals", "mountain"
        )

        // Hizkuntza detektatu
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

    /**
     * Testua normalizatzen du minuskulara bihurtuz eta azentu ezberdinak kenduz.
     * @param text Normalizatzeko testua
     * @return Testu normalizatua
     */
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

    /**
     * Erabiltzaile-izena irakurtzen du partekatutako hobespenetatik.
     * @return Erabiltzaile-izena edo null
     */
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