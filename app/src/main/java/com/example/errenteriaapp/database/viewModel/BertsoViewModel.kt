
package com.example.errenteriaapp.database.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.database.Puntuazioa
import com.example.errenteriaapp.database.PuntuazioaDao
import kotlinx.coroutines.launch

class BertsoViewModel(
    private val puntuazioaDao: PuntuazioaDao?,
    private val configJuego: ConfigJuego = ConfigJuego.DEFAULT_BERTSOA_1
) : ViewModel() {

    data class ConfigJuego(
        val minCorrectosRequeridos: Int,
        val puntosPorCorrecto: Int,
        val puntosExtraPerfecto: Int = 0,
        val necesitaTodosCorrectos: Boolean = false
    ) {
        companion object {
            // Configuración para el primer bertso (7 preguntas)
            val DEFAULT_BERTSOA_1 = ConfigJuego(
                minCorrectosRequeridos = 5,  // Más de 4 como dice tu código
                puntosPorCorrecto = 1,
                puntosExtraPerfecto = 2
            )

            // Configuración para el segundo bertso (5 preguntas)
            val DEFAULT_BERTSOA_2 = ConfigJuego(
                minCorrectosRequeridos = 4,  // Más de 3 como dice tu código
                puntosPorCorrecto = 1,
                puntosExtraPerfecto = 2
            )
        }
    }

    // Añade esta variable para el nombre del usuario
    var currentUserName: String? = null

    // Variables para el control del juego
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

    // Para el primer bertso
    val totalItemsBertso1 = 7
    // Para el segundo bertso
    val totalItemsBertso2 = 5

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

    // Verificar si el primer bertso está completado
    fun checkBertso1Completion(onSuccessNavigate: () -> Unit) {
        if (answeredCount == totalItemsBertso1) {
            if (correctCount > 4 && !hasNavigated) {
                markNavigated()
                // Guardar puntos del primer bertso
                guardarPuntuacion(correctCount)
                // Navegar al segundo bertso
                onSuccessNavigate()
            } else if (!hasNavigated) {
                // Reiniciar intento si no aprobó
                restartAttempt()
                showWrongDialog = true
            }
        }
    }

    // Verificar si el segundo bertso está completado
    fun checkBertso2Completion() {
        if (answeredCount == totalItemsBertso2) {
            if (correctCount > 3) {
                // Guardar puntos del segundo bertso
                guardarPuntuacion(correctCount)
                showSuccessDialog = true
            } else {
                restartAttempt()
                showWrongDialog = true
            }
        }
    }

    fun restartAttempt() {
        correctCount = 0
        answeredCount = 0
        hasNavigated = false
        attempt += 1
    }

    private fun calcularPuntuacionFinal(correctos: Int): Int {
        var puntos = correctos * configJuego.puntosPorCorrecto
        // Bonus por respuesta perfecta
        val totalItems = if (configJuego == ConfigJuego.DEFAULT_BERTSOA_1)
            totalItemsBertso1 else totalItemsBertso2

        if (correctos == totalItems && configJuego.puntosExtraPerfecto > 0) {
            puntos += configJuego.puntosExtraPerfecto
        }
        return puntos
    }

    fun guardarPuntuacion(correctos: Int) {
        viewModelScope.launch {
            currentUserName?.let { nombreUsuario ->
                puntuazioaDao?.let { dao ->
                    val puntuazioActual = dao.getByName(nombreUsuario)
                    val puntosFinales = calcularPuntuacionFinal(correctos)

                    if (puntuazioActual != null) {
                        val nuevaPuntuazio = puntuazioActual.copy(
                            puntuazioaBertso = puntuazioActual.puntuazioaBertso + puntosFinales
                        )
                        dao.insert(nuevaPuntuazio)
                    } else {
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

    fun dismissSuccessDialog() {
        showSuccessDialog = false
    }

    fun dismissWrongDialog() {
        showWrongDialog = false
    }

    // Método para establecer el usuario
    fun setUsuario(nombre: String) {
        currentUserName = nombre
    }

    // Método para cambiar la configuración (para el segundo bertso)
    fun cambiarConfiguracion(nuevaConfig: ConfigJuego) {
        // Reiniciar el estado para el nuevo juego
        restartAttempt()
    }
}

