package com.example.errenteriaapp.i18n

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue

/**
 * Estado global (en memoria) para forzar recomposición cuando cambia el idioma.
 *
 * No recrea la Activity: en Compose usamos `key(AppLanguageState.nonce)` en el root.
 * Esto hace que `stringResource(...)` se vuelva a resolver en toda la UI Compose.
 */
object AppLanguageState {
    var nonce by mutableLongStateOf(0L)
        private set

    fun bump() {
        nonce++
    }
}

