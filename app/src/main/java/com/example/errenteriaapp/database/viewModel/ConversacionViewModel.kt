package com.example.errenteriaapp.database.viewModel

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
        )
    )

    private val idleLoop = listOf(
        Message(
            "Prest gaude! Sakatu botoia jolasten hasteko.",
            true,
            15000L
        ), Message(
            "Zure zain gaude, sakatu botoia eta has gaitezen!",
            false,
            15000L
        )
    )

    fun startConversation() {
        viewModelScope.launch {
            conversation.forEach { emitMessage(it) }
            _state.value = _state.value.copy(showStartButton = true)

            while (!_state.value.startButtonPressed) {
                idleLoop.forEach {
                    if (_state.value.startButtonPressed) return@launch
                    emitMessage(it)
                }
            }
        }
    }

    private suspend fun emitMessage(message: Message) {
        _state.value = _state.value.copy(
            currentMessage = message,
            currentMessageIndex = _state.value.currentMessageIndex + 1,
            messages = _state.value.messages + message
        )
        delay(message.duration)
    }

    fun onStartButtonClicked() {
        _state.value = _state.value.copy(startButtonPressed = true)
    }
}