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

/**
 * ViewModela erabili saioa hasteko eta erabiltzailearen egoera kudeatzeko
 * @see ViewModel
 * @param ikasleDao Ikasleak datu-basean gordetzeko erabiltzen den DAOa
 * @param irakasleDao Irakasleak datu-basean gordetzeko erabiltzen den DAOa
 * @param partidaDao Partidak datu-basean gordetzeko erabiltzen den DAOa
 * @param puntuazioaDao Puntuazioak datu-basean gordetzeko erabiltzen den DAOa
 * @param izenTaldeaDao Izenak eta taldeak datu-basean gordetzeko erabiltzen den DAOa
 */
class LoginViewModel(
    private val ikasleDao: IkasleDao,
    private val irakasleDao: IrakasleDao,
    private val partidaDao: PartidaDao,
    private val puntuazioaDao: PuntuazioaDao,
    private val izenTaldeaDao: IzenTaldeaDao
) : ViewModel() {

    /**
     * Oraingo erabiltzailearen izena gordetzeko
     */
    private val _currentUser = MutableStateFlow<String?>(null)
    val currentUser: StateFlow<String?> = _currentUser

    /**
     * Datuak gordetzen ari diren egoera kontrolatzeko
     */
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    /**
     * Saioa hastea arrakastatsua izan den egiaztatzeko
     */
    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    /**
     * Errore mezuak gordetzeko
     */
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    /**
     * ViewModela hasieratuta dagoen egiaztatzeko
     */
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized

    /**
     * Irakasle modua aktibatuta dagoen kontrolatzeko
     */
    private val _isTeacherMode = MutableStateFlow(false)
    val isTeacherMode: StateFlow<Boolean> = _isTeacherMode

    init {
        viewModelScope.launch {
            irakasleDao.insertarIrakasleSiNoExiste()
            _isInitialized.value = true
        }
    }

    /**
     * Irakasle modua aktibatu edo desaktibatzen du
     * @param enabled Irakasle modua aktibatu behar den ala ez
     */
    fun setTeacherMode(enabled: Boolean) {
        _isTeacherMode.value = enabled
    }

    /**
     * Erabiltzailearen izena datu-basean gordetzen du
     * @param nombreCompleto Erabiltzailearen izen osoa
     * @param asTeacher Irakasle moduan gorde behar den ala ez
     * @param taldea Ikaslearen taldea (aukerakoa)
     */
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

                        // IzenTaldean sartu klasea eman bada
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

    /**
     * Ranking osoa ezabatzen du (ikasle, partida eta puntuazio guztiak)
     */
    suspend fun deleteEntireRanking() {
        ikasleDao.deleteAll()
        partidaDao.deleteAll()
        puntuazioaDao.deleteAll()
    }

    /**
     * Rankingaren puntuazio guztiak berrabiarazten ditu (zerora itzultzen ditu)
     */
    suspend fun resetRankingScores() {
        puntuazioaDao.resetScores()
    }

    /**
     * Irakasle guztiak lortzen ditu
     * @return Irakasleen zerrenda
     */
    fun getAllIrakasleak(): Flow<List<Irakasle>> {
        return irakasleDao.getAll()
    }

    /**
     * Oraingo erabiltzailea lortzen du
     * @return Oraingo erabiltzailearen izena edo null
     */
    fun getCurrentUser(): String? = _currentUser.value
}