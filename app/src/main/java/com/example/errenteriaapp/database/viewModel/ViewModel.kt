package com.example.errenteriaapp.database.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.database.Ikasle
import com.example.errenteriaapp.database.IkasleDao
import com.example.errenteriaapp.database.Irakasle
import com.example.errenteriaapp.database.IrakasleDao
import com.example.errenteriaapp.database.IzenTaldea
import com.example.errenteriaapp.database.IzenTaldeaDao
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
    private val puntuazioaDao: PuntuazioaDao,
    private val izenTaldeaDao: IzenTaldeaDao
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

    private val _isTeacherMode = MutableStateFlow(false)
    val isTeacherMode: StateFlow<Boolean> = _isTeacherMode

    init {
        viewModelScope.launch {
            irakasleDao.insertarIrakasleSiNoExiste()
            _isInitialized.value = true
        }
    }

    fun setTeacherMode(enabled: Boolean) {
        _isTeacherMode.value = enabled
    }

    fun guardarNombre(nombreCompleto: String, asTeacher: Boolean = false, taldea: String? = null) {
        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = ""

            try {
                val nombreTrimmed = nombreCompleto.trim()
                if (nombreTrimmed.split(" ").size < 2 && !asTeacher) {
                    _errorMessage.value = "Mesedez, idatzi zure izena eta abizena"
                } else {
                    _currentUser.value = nombreTrimmed
                    _isTeacherMode.value = asTeacher

                    if (!asTeacher) {
                        val nuevoIkasle = Ikasle(
                            izenaAbizena = nombreTrimmed,
                            rol = "Ikasle"
                        )
                        ikasleDao.insert(nuevoIkasle)

                        // Insertar en IzenTaldea si se proporcionó una clase
                        taldea?.let { clase ->
                            val nuevoIzenTaldea = IzenTaldea(
                                izenaAbizena = nombreTrimmed,
                                taldea = clase
                            )
                            izenTaldeaDao.insert(nuevoIzenTaldea)
                        }

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
                        val horaActual = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val horaFormateada = horaActual.format(formatter)
                        val nuevaPartida = Partida(
                            izenaAbizena = nombreTrimmed,
                            ordua = horaFormateada
                        )
                        partidaDao.insert(nuevaPartida)
                    }

                    _loginSuccess.value = true
                }
            } catch (e: Exception) {
                _errorMessage.value = "Errorea gertatu da: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }
    // Añade estas funciones de validación al principio de LoginScreen:

    suspend fun deleteEntireRanking() {
        ikasleDao.deleteAll()
        partidaDao.deleteAll()
        puntuazioaDao.deleteAll()
    }

    suspend fun resetRankingScores() {
        puntuazioaDao.resetScores()
    }

    fun getAllIrakasleak(): Flow<List<Irakasle>> {
        return irakasleDao.getAll()
    }

    fun getCurrentUser(): String? = _currentUser.value
}