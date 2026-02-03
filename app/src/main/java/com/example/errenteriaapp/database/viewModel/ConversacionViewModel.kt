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

/**
 * Hasierako elkarrizketa kudeatzen duen ViewModel-a.
 * Xanti eta Maialen pertsonaiaren arteko elkarrizketa erakusten du.
 * Erabiltzailea "Hasi" botoia sakatu arte itzuli egiten da.
 */
class ConversacionViewModel : ViewModel() {
    private val _state = MutableStateFlow(ConversationState())
    val state: StateFlow<ConversationState> = _state

    // Elkarrizketa nagusiaren mezuak (hasieran bakarrik erakusten dira)
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

    // Zirkuluan errepikatutako mezuak ("Hasi" botoia sakatu arte)
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

    /**
     * Elkarrizketa hastea.
     * Elkarrizketa nagusia erakusten du eta ondoren zirkuluan errepikatzen da.
     */
    fun startConversation() {
        viewModelScope.launch {
            // Elkarrizketa nagusia erakutsi
            conversation.forEach { emitMessage(it) }
            _state.value = _state.value.copy(showStartButton = true)

            // "Hasi" botoia sakatu arte zirkuluan errepikatu
            while (!_state.value.startButtonPressed) {
                idleLoop.forEach {
                    if (_state.value.startButtonPressed) return@launch
                    emitMessage(it)
                }
            }
        }
    }

    /**
     * Mezu bat igortzen du egoeran.
     * @param message Igortzeko mezua
     */
    private suspend fun emitMessage(message: Dialogo) {
        _state.value = _state.value.copy(
            currentMessage = message,
            currentMessageIndex = _state.value.currentMessageIndex + 1,
            messages = _state.value.messages + message
        )
        delay(message.duration)
    }

    /**
     * "Hasi" botoian klik egitean deitzen da.
     * Elkarrizketa zirkulutik irteten da.
     */
    fun onStartButtonClicked() {
        _state.value = _state.value.copy(startButtonPressed = true)
    }
}