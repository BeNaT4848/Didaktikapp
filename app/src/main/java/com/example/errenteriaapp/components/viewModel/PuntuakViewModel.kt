package com.example.errenteriaapp.components.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PuntuakViewModel : ViewModel() {
    var correctCount by mutableStateOf(0)
        private set

    var hasNavigated by mutableStateOf(false)
        private set

    var answeredCount by mutableStateOf(0)
        private set

    var attempt by mutableStateOf(0)
        private set

    fun registerCorrect(): Int {
        correctCount += 1
        return correctCount
    }

    fun registerAnswer(): Int {
        answeredCount += 1
        return answeredCount
    }

    fun markNavigated() {
        hasNavigated = true
    }

    fun restartAttempt() {
        correctCount = 0
        answeredCount = 0
        hasNavigated = false
        attempt += 1
    }
}
