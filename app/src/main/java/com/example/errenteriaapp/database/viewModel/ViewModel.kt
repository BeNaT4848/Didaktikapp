package com.example.errenteriaapp.database.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.database.Ikasle
import com.example.errenteriaapp.database.IkasleDao
import com.example.errenteriaapp.database.Irakasle
import com.example.errenteriaapp.database.IrakasleDao
import com.example.errenteriaapp.database.Partida
import com.example.errenteriaapp.database.PartidaDao
import com.example.errenteriaapp.database.Puntuazioa
import com.example.errenteriaapp.database.PuntuazioaDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LoginViewModel(
    private val ikasleDao: IkasleDao,
    private val irakasleDao: IrakasleDao,
    private val partidaDao: PartidaDao,
    private val puntuazioaDao: PuntuazioaDao
) : ViewModel() {

    private val _currentUser = MutableStateFlow<String?>(null)
    val currentUser: StateFlow<String?> = _currentUser
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
                    val nombreTrimmed = nombreCompleto.trim()

                    // Guardar el usuario actual
                    _currentUser.value = nombreTrimmed



                    val nuevoIkasle = Ikasle(
                        izenaAbizena = nombreCompleto.trim(),
                        rol = "Ikasle"
                    )
                    ikasleDao.insert(nuevoIkasle)

                    // 2. Crear nueva puntuación inicial si no existe
                    val puntuazioExistente = puntuazioaDao.getByName(nombreTrimmed)
                    if (puntuazioExistente == null) {
                        val nuevaPuntuazioa = Puntuazioa(
                            izenaAbizena = nombreTrimmed,
                            puntuazioaBertso = 0,
                            puntuazioaGalderak = 0,
                            puntuazioaGurutzegrama = 0,
                            puntuazioaArropaBuruHandiak = 0,
                            puntuazioaPapresa = 0,
                            puntuazioaArrastrar = 0,
                            puntuazioaSopaLetra = 0
                        )
                        puntuazioaDao.insert(nuevaPuntuazioa)
                    }

                    // 3. Crear nueva partida con hora actual
                    val horaActual = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val horaFormateada = horaActual.format(formatter)

                    val nuevaPartida = Partida(
                        izenaAbizena = nombreCompleto.trim(),
                        ordua = horaFormateada
                    )
                    partidaDao.insert(nuevaPartida)

                    _loginSuccess.value = true
                }
            } catch (e: Exception) {
                _errorMessage.value = "Errorea gertatu da: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }
    fun getAllIrakasleak(): Flow<List<Irakasle>> {
        return irakasleDao.getAll()
    }

    fun getCurrentUser(): String? = _currentUser.value
}