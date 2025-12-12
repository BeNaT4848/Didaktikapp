package com.example.errenteriaapp.database.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.classes.ConversationState
import com.example.errenteriaapp.classes.Dialogo

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConversacionViewModel : ViewModel() {
    private val _state = MutableStateFlow(ConversationState())
    val state: StateFlow<ConversationState> = _state

    private val conversation = listOf(
        Dialogo(
            "Egun on ikasleak! Gu Xanti eta Maialen gara, Errenteriako jaietako erraldoiak.",
            true,
            8000L
        ), Dialogo(
            "Baina gaur ez gara festetara joango, Errenteriatik txangoa egingo dugu.",
            false,
            6000L
        ), Dialogo(
            "Bitartean jardueratxoak egingo ditugu dibertigarriagoa izan dadin!",
            false,
            6000L
        ), Dialogo(
            "Errenteriako historiaren parte diren txoko eta gaiak landuko ditugu egunean zehar.",
            true,
            8000L
        ), Dialogo(
            "Hemengo mapan ikus ditzakezue ze leku bisitatuko ditugun.",
            false,
            6000L
        ), Dialogo(
            "Denak ezagutu nahi badituzue jarraitu zuen irakasleak!",
            false,
            6000L
        )
    )

    private val idleLoop = listOf(
        Dialogo(
            "Prest gaude! Sakatu botoia jolasten hasteko.",
            true,
            15000L
        ), Dialogo(
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

    private suspend fun emitMessage(message: Dialogo) {
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