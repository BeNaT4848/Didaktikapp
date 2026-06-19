package com.example.errenteriaapp.database.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.database.Puntuazioa
import com.example.errenteriaapp.database.PuntuazioaDao
import kotlinx.coroutines.launch

/**
 * Bertso jokoaren ViewModel-a.
 * Bi bertsoren (bertso1 eta bertso2) logika kudeatzen du.
 *
 * @param puntuazioaDao Puntuazioaren datu-basearen atzipenerako DAO (null izan daiteke)
 * @param configJuego Jokoaren konfigurazioa (lehen bertsoarekin hasieratzen da)
 */
class BertsoViewModel(
    private val puntuazioaDao: PuntuazioaDao?,
    private val configJuego: ConfigJuego = ConfigJuego.DEFAULT_BERTSOA_1
) : ViewModel() {

    /**
     * Jokoaren konfigurazioa.
     * @property minCorrectosRequeridos Aprobatutako minimo erantzun zuzenak
     * @property puntosPorCorrecto Erantzun zuzen bakoitzeko puntuak
     * @property puntosExtraPerfecto Guztiz zuzena denean bonus puntuak
     * @property necesitaTodosCorrectos Erantzun guztiak zuzenak izan behar dituen
     */
    data class ConfigJuego(
        val minCorrectosRequeridos: Int,
        val puntosPorCorrecto: Int,
        val puntosExtraPerfecto: Int = 0,
        val necesitaTodosCorrectos: Boolean = false
    ) {
        companion object {
            // Lehen bertsoaren konfigurazioa (7 galdera)
            val DEFAULT_BERTSOA_1 = ConfigJuego(
                minCorrectosRequeridos = 5,  // Zure kodeak 4 baino gehiago esaten duenez
                puntosPorCorrecto = 2,
                puntosExtraPerfecto = 3
            )

            // Bigarren bertsoaren konfigurazioa (5 galdera)
            val DEFAULT_BERTSOA_2 = ConfigJuego(
                minCorrectosRequeridos = 4,  // Zure kodeak 3 baino gehiago esaten duenez
                puntosPorCorrecto = 1,
                puntosExtraPerfecto = 2
            )
        }
    }

    // Erabiltzailearen izenerako aldagaia
    var currentUserName: String? = null

    // Jokoaren kontrolerako aldagaiak
    var correctCount by mutableStateOf(0)
        private set

    var hasNavigated by mutableStateOf(false)
        private set

    var answeredCount by mutableStateOf(0)
        private set

    var attempt by mutableStateOf(0)
        private set

    var showSuccessDialog by mutableStateOf(false)
        private set

    var showWrongDialog by mutableStateOf(false)
        private set

    // Lehen bertsoaren galdera kopurua
    val totalItemsBertso1 = 7
    // Bigarren bertsoaren galdera kopurua
    val totalItemsBertso2 = 5

    /**
     * Erantzun zuzen bat erregistratzen du.
     * @return Erantzun zuzen kopuru berria
     */
    fun registerCorrect(): Int {
        correctCount += 1
        return correctCount
    }

    /**
     * Erantzun bat erregistratzen du (zuzena edo okerra).
     * @return Erantzundako galdera kopuru berria
     */
    fun registerAnswer(): Int {
        answeredCount += 1
        return answeredCount
    }

    /**
    Nabigazioa markatzen du (bigarren bertsoarako pasatzean).
     */
    fun markNavigated() {
        hasNavigated = true
    }

    /**
     * Lehen bertsoaren osaketa egiaztatzen du.
     * @param onSuccessNavigate Arrakasta denean deitzen den funtzioa (bigarren bertsoara nabigatzeko)
     */
    fun checkBertso1Completion(onSuccessNavigate: () -> Unit) {
        if (configJuego != ConfigJuego.DEFAULT_BERTSOA_1) return
        if (answeredCount == totalItemsBertso1) {
            if (correctCount > 4 && !hasNavigated) {
                markNavigated()
                // Lehen bertsoaren puntuak gorde
                guardarPuntuacion(correctCount)
                // Bigarren bertsoara nabigatu
                onSuccessNavigate()
            } else if (!hasNavigated) {
                // Saiakera berrabiarazi ez badu gainditu
                restartAttempt()
                showWrongDialog = true
            }
        }
    }

    /**
     * Bigarren bertsoaren osaketa egiaztatzen du.
     */
    fun checkBertso2Completion() {
        if (configJuego != ConfigJuego.DEFAULT_BERTSOA_2) return
        if (answeredCount == totalItemsBertso2) {
            if (correctCount > 3) {
                // Bigarren bertsoaren puntuak gorde
                guardarPuntuacion(correctCount)
                showSuccessDialog = true
            } else {
                restartAttempt()
                showWrongDialog = true
            }
        }
    }

    /**
     * Saiakera berrabiarazten du.
     */
    fun restartAttempt() {
        correctCount = 0
        answeredCount = 0
        hasNavigated = false
        attempt += 1
    }

    /**
     * Puntuazio finala kalkulatzen du.
     * @param correctos Erantzun zuzen kopurua
     * @return Kalkulatutako puntuazioa
     */
    private fun calcularPuntuacionFinal(correctos: Int): Int {
        var puntos = correctos * configJuego.puntosPorCorrecto
        // Bonus erantzun perfektuagatik
        val totalItems = if (configJuego == ConfigJuego.DEFAULT_BERTSOA_1)
            totalItemsBertso1 else totalItemsBertso2

        if (correctos == totalItems && configJuego.puntosExtraPerfecto > 0) {
            puntos += configJuego.puntosExtraPerfecto
        }
        return puntos
    }

    /**
     * Puntuazioa datu-basean gordetzen du.
     * @param correctos Erantzun zuzen kopurua
     */
    fun guardarPuntuacion(correctos: Int) {
        viewModelScope.launch {
            currentUserName?.let { nombreUsuario ->
                puntuazioaDao?.let { dao ->
                    val puntuazioActual = dao.getByName(nombreUsuario)
                    val puntosFinales = calcularPuntuacionFinal(correctos)

                    if (puntuazioActual != null) {
                        // Puntuazioa eguneratu (gehitu)
                        val nuevaPuntuazio = puntuazioActual.copy(
                            puntuazioaBertso = puntuazioActual.puntuazioaBertso + puntosFinales
                        )
                        dao.insert(nuevaPuntuazio)
                    } else {
                        // Puntuazio berria sortu
                        val nuevaPuntuazio = Puntuazioa(
                            izenaAbizena = nombreUsuario,
                            puntuazioaBertso = puntosFinales,
                            puntuazioaGalderak = 0,
                            puntuazioaGurutzegrama = 0,
                            puntuazioaArropaBuruHandiak = 0,
                            puntuazioaPapresa = 0,
                            puntuazioaArrastrar = 0,
                            puntuazioaSopaLetra = 0
                        )
                        dao.insert(nuevaPuntuazio)
                    }
                }
            }
        }
    }

    /**
     * Arrakasta-dialogoa ezkutatzen du.
     */
    fun dismissSuccessDialog() {
        showSuccessDialog = false
    }

    /**
     * Errore-dialogoa ezkutatzen du.
     */
    fun dismissWrongDialog() {
        showWrongDialog = false
    }

    /**
     * Erabiltzailea ezartzen du.
     * @param nombre Erabiltzailearen izena
     */
    fun setUsuario(nombre: String) {
        currentUserName = nombre
    }


}