package com.example.errenteriaapp.i18n

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue

/**
 * Hizkuntza aldatzeko egoera globala (memorian) berriz osatzera behartzeko.
 *
 * Ez du Activity-rik birsortzen: Compose-n `key(AppLanguageState.nonce)` erabiltzen dugu erroan.
 * Horrek `stringResource(...)` berriz ebaztea eragiten du UI Compose osoan.
 * @see key
 */
object AppLanguageState {
    /**
     * Balio ez-erlazionatu bat hizkuntza aldatu den zenbat aldiz kontatzeko
     */
    var nonce by mutableLongStateOf(0L)
        private set

    /**
     * Hizkuntza-aldaketa jakinarazten du nonce balioa handituz
     */
    fun bump() {
        nonce++
    }
}