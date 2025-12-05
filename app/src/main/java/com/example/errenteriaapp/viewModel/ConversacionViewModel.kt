package com.example.errenteriaapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.model.ConversationState
import com.example.errenteriaapp.model.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConversacionViewModel : ViewModel() {
    private val _state = MutableStateFlow(ConversationState())
    val state: StateFlow<ConversationState> = _state

    private val conversation = listOf(
        Message(
            "Egun on ikasleak! Gu Xanti eta Maialen gara, Errenteriako jaietako erraldoiak.",
            true,
            8000L
        ), Message(
            "Baina gaur ez gara festetara joango, Errenteriatik txangoa egingo dugu.",
            false,
            6000L
        ), Message(
            "Bitartean jardueratxoak egingo ditugu dibertigarriagoa izan dadin!",
            false,
            6000L
        ), Message(
            "Errenteriako historiaren parte diren txoko eta gaiak landuko ditugu egunean zehar.",
            true,
            8000L
        ), Message(
            "Hemengo mapan ikus ditzakezue ze leku bisitatuko ditugun.",
            false,
            6000L
        ), Message(
            "Denak ezagutu nahi badituzue jarraitu zuen irakasleak!",
            false,
            6000L
        ), Message(
            "Jolasten hasteko sakatu beheko botoia.",
            true,
            3000L
        )
    )

    fun startConversation() {
        viewModelScope.launch {
            for (i in conversation.indices) {
                // Actualizamos el mensaje actual
                _state.value = _state.value.copy(
                    currentMessage = conversation[i],
                    currentMessageIndex = i,
                    messages = _state.value.messages + conversation[i]
                )

                // Esperamos la duración del mensaje
                delay(conversation[i].duration)
            }

            // Al terminar la conversación, mostramos el botón
            _state.value = _state.value.copy(showStartButton = true)
        }
    }
}