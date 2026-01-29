package com.example.errenteriaapp.database.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.R
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
            R.string.home_dialog_1,
            true,
            8000L
        ), Dialogo(
            R.string.home_dialog_2,
            false,
            6000L
        ), Dialogo(
            R.string.home_dialog_3,
            false,
            6000L
        ), Dialogo(
            R.string.home_dialog_4,
            true,
            8000L
        ), Dialogo(
            R.string.home_dialog_5,
            false,
            6000L
        ), Dialogo(
            R.string.home_dialog_6,
            false,
            6000L
        )
    )

    private val idleLoop = listOf(
        Dialogo(
            R.string.home_idle_1,
            true,
            15000L
        ), Dialogo(
            R.string.home_idle_2,
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