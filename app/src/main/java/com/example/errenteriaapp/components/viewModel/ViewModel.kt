package com.example.errenteriaapp.components.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.database.Ikasle
import com.example.errenteriaapp.database.IkasleDao
import com.example.errenteriaapp.database.IrakasleDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val ikasleDao: IkasleDao,
    private val irakasleDao: IrakasleDao
) : ViewModel() {

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized

    init {
        viewModelScope.launch {
            irakasleDao.insertarIrakasleSiNoExiste()
            _isInitialized.value = true
        }
    }

    fun guardarNombre(nombreCompleto: String) {
        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = ""

            try {
                if (nombreCompleto.trim().split(" ").size < 2) {
                    _errorMessage.value = "Mesedez, idatzi zure izena eta abizena"
                } else {
                    val nuevoIkasle = Ikasle(
                        izenaAbizena = nombreCompleto.trim(),
                        rol = "Default"
                    )
                    ikasleDao.insert(nuevoIkasle)
                    _loginSuccess.value = true
                }
            } catch (e: Exception) {
                _errorMessage.value = "Errorea gertatu da: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }
}