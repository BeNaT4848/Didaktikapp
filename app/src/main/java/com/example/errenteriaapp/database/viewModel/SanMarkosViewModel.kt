// app/src/main/java/com/example/errenteriaapp/database/viewModel/SanMarkosViewModel.kt
package com.example.errenteriaapp.database.viewModel

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.R
import com.example.errenteriaapp.database.Puntuazioa
import com.example.errenteriaapp.database.PuntuazioaDao
import kotlinx.coroutines.launch

data class PreguntaConOpcionesMezcladas(
    @StringRes val texto: Int,
    @StringRes val opcionesOriginales: List<Int>,
    @StringRes val opcionesMezcladas: List<Int>,
    val respuestaCorrectaOriginal: Int,  // Índice en opcionesOriginales
    val respuestaCorrectaMezclada: Int   // Índice en opcionesMezcladas
)

class SanMarkosViewModel(
    private val puntuazioaDao: PuntuazioaDao?,
    private val configJuego: ConfigJuego = ConfigJuego.DEFAULT_SAN_MARKOS
) : ViewModel() {

    data class ConfigJuego(
        val minCorrectosRequeridos: Int,
        val puntosPorRespuestaCorrecta: Int = 5,
        val puntosExtraPerfecto: Int = 5,
        val puntosPorTodasCorrectas: Int = 5
    ) {
        companion object {
            val DEFAULT_SAN_MARKOS = ConfigJuego(
                minCorrectosRequeridos = 2,
                puntosPorRespuestaCorrecta = 5,
                puntosExtraPerfecto = 5,
                puntosPorTodasCorrectas = 5
            )
        }
    }

    var currentUserName: String? = null

    // Estado del juego
    var galderaIndex by mutableIntStateOf(0)
        private set

    var aukeraHautatua by mutableIntStateOf(-1)
        private set

    var erantzunZuzenak by mutableIntStateOf(0)
        private set

    var puntuacionTotal by mutableIntStateOf(0)
        private set

    val galderakErantzunda = mutableStateListOf<Int>()
    val erantzunak = mutableStateMapOf<Int, Pair<Int, Boolean>>()

    var showSuccessDialog by mutableStateOf(false)
        private set

    var showWrongDialog by mutableStateOf(false)
        private set

    // Preguntas base con respuestas fijas
    private val preguntasBase: List<Triple<Int, List<Int>, Int>> = listOf(
        Triple(
            R.string.sanmarkos_q1_text,
            listOf(
                R.string.sanmarkos_q1_opt1,
                R.string.sanmarkos_q1_opt2,
                R.string.sanmarkos_q1_opt3
            ),
            1
        ),
        Triple(
            R.string.sanmarkos_q2_text,
            listOf(
                R.string.sanmarkos_q2_opt1,
                R.string.sanmarkos_q2_opt2,
                R.string.sanmarkos_q2_opt3
            ),
            0
        ),
        Triple(
            R.string.sanmarkos_q3_text,
            listOf(
                R.string.sanmarkos_q3_opt1,
                R.string.sanmarkos_q3_opt2,
                R.string.sanmarkos_q3_opt3
            ),
            0
        )
    )

    // Preguntas con opciones mezcladas
    var galderak by mutableStateOf<List<PreguntaConOpcionesMezcladas>>(emptyList())
        private set

    init {
        mezclarTodo()
    }

    // Función para mezclar preguntas Y respuestas
    private fun mezclarTodo() {
        // 1. Mezclar el orden de las preguntas
        val preguntasMezcladas = preguntasBase.shuffled()

        // 2. Para cada pregunta, mezclar las opciones
        val nuevasGalderak = mutableListOf<PreguntaConOpcionesMezcladas>()

        preguntasMezcladas.forEach { (texto, opciones, respuestaCorrecta) ->
            // Crear lista de índices para mezclar
            val indices = opciones.indices.toList().shuffled()

            // Crear las opciones mezcladas
            val opcionesMezcladas = indices.map { opciones[it] }

            // Encontrar la nueva posición de la respuesta correcta
            val nuevaPosicionCorrecta = indices.indexOf(respuestaCorrecta)

            nuevasGalderak.add(
                PreguntaConOpcionesMezcladas(
                    texto = texto,
                    opcionesOriginales = opciones,
                    opcionesMezcladas = opcionesMezcladas,
                    respuestaCorrectaOriginal = respuestaCorrecta,
                    respuestaCorrectaMezclada = nuevaPosicionCorrecta
                )
            )
        }

        galderak = nuevasGalderak

        // Resetear estado
        resetGame()

        println("DEBUG - Preguntas y respuestas mezcladas:")
        galderak.forEachIndexed { index, pregunta ->
            println("Pregunta $index: ${pregunta.texto}")
            println("Opciones mezcladas: ${pregunta.opcionesMezcladas}")
            println("Respuesta correcta (posición mezclada): ${pregunta.respuestaCorrectaMezclada}")
        }
    }

    // Método para seleccionar una opción
    fun onOptionSelected(optionIndex: Int) {
        if (!galderakErantzunda.contains(galderaIndex)) {
            aukeraHautatua = optionIndex
            val preguntaActual = galderak[galderaIndex]
            val correct = optionIndex == preguntaActual.respuestaCorrectaMezclada

            erantzunak[galderaIndex] = Pair(optionIndex, correct)

            if (correct) {
                erantzunZuzenak++
                println("DEBUG - Respuesta CORRECTA!")
            } else {
                println("DEBUG - Respuesta INCORRECTA. La correcta era: ${preguntaActual.respuestaCorrectaMezclada}")
            }

            galderakErantzunda.add(galderaIndex)
        }
    }

    // Método para ir a la siguiente pregunta o terminar
    fun onNextQuestion() {
        if (galderaIndex < galderak.size - 1) {
            // Ir a la siguiente pregunta
            galderaIndex++
            aukeraHautatua = -1
        } else {
            // Verificar resultados
            verificarResultados()
        }
    }

    // Verificar resultados y calcular puntos
    private fun verificarResultados() {
        val haAprobado = erantzunZuzenak >= configJuego.minCorrectosRequeridos

        println("DEBUG === RESULTADOS FINALES ===")
        println("DEBUG - Respuestas correctas: $erantzunZuzenak/${galderak.size}")
        println("DEBUG - Mínimo requerido: ${configJuego.minCorrectosRequeridos}")
        println("DEBUG - Ha aprobado: $haAprobado")

        if (haAprobado) {
            // Calcular puntuación
            puntuacionTotal = calcularPuntuacion()

            // Guardar puntos en base de datos
            guardarPuntuacion()

            // Mostrar diálogo de éxito
            showSuccessDialog = true
        } else {
            // Mostrar diálogo de error
            showWrongDialog = true
        }
    }

    // Calcular puntuación
    private fun calcularPuntuacion(): Int {
        var puntos = 0

        // Puntos por respuestas correctas
        val puntosBase = erantzunZuzenak * configJuego.puntosPorRespuestaCorrecta
        puntos += puntosBase

        println("DEBUG - Puntos base: $erantzunZuzenak × ${configJuego.puntosPorRespuestaCorrecta} = $puntosBase")

        // Bonus por todas correctas
        if (erantzunZuzenak == galderak.size) {
            puntos += configJuego.puntosPorTodasCorrectas
            println("DEBUG - Bonus todas correctas: +${configJuego.puntosPorTodasCorrectas}")
        }

        // Bonus extra por desempeño perfecto
        if (erantzunZuzenak == galderak.size) {
            puntos += configJuego.puntosExtraPerfecto
            println("DEBUG - Bonus extra perfecto: +${configJuego.puntosExtraPerfecto}")
        }

        println("DEBUG - Puntos totales: $puntos")
        return puntos
    }

    // Guardar puntuación en base de datos - CORREGIDO
    private fun guardarPuntuacion() {
        viewModelScope.launch {
            try {
                println("DEBUG === GUARDANDO PUNTUACIÓN ===")
                println("DEBUG - Usuario actual: $currentUserName")
                println("DEBUG - Puntuación a guardar: $puntuacionTotal")

                currentUserName?.let { nombreUsuario ->
                    println("DEBUG - Usuario válido: $nombreUsuario")

                    puntuazioaDao?.let { dao ->
                        println("DEBUG - DAO disponible")

                        // Obtener la puntuación actual
                        val puntuazioActual = dao.getByName(nombreUsuario)
                        println("DEBUG - Puntuación actual en BD: ${puntuazioActual?.puntuazioaGalderak}")

                        val nuevaPuntuazio = if (puntuazioActual != null) {
                            // Sumar los nuevos puntos a los existentes
                            val puntosTotales = puntuazioActual.puntuazioaGalderak + puntuacionTotal
                            println("DEBUG - Sumando puntos: ${puntuazioActual.puntuazioaGalderak} + $puntuacionTotal = $puntosTotales")

                            puntuazioActual.copy(
                                puntuazioaGalderak = puntosTotales
                            )
                        } else {
                            println("DEBUG - Creando nuevo registro")
                            Puntuazioa(
                                izenaAbizena = nombreUsuario,
                                puntuazioaBertso = 0,
                                puntuazioaGalderak = puntuacionTotal,
                                puntuazioaGurutzegrama = 0,
                                puntuazioaArropaBuruHandiak = 0,
                                puntuazioaPapresa = 0,
                                puntuazioaArrastrar = 0,
                                puntuazioaSopaLetra = 0
                            )
                        }

                        // Insertar o actualizar
                        dao.insert(nuevaPuntuazio)
                        println("DEBUG - Puntuación guardada exitosamente: ${nuevaPuntuazio.puntuazioaGalderak}")

                    } ?: run {
                        println("DEBUG - ERROR: puntuazioaDao es null")
                    }
                } ?: run {
                    println("DEBUG - ERROR: currentUserName es null")
                }
            } catch (e: Exception) {
                println("DEBUG - ERROR al guardar puntuación: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // Reiniciar el juego
    fun resetGame() {
        galderaIndex = 0
        aukeraHautatua = -1
        erantzunZuzenak = 0
        puntuacionTotal = 0
        galderakErantzunda.clear()
        erantzunak.clear()
        showSuccessDialog = false
        showWrongDialog = false
        println("DEBUG - Juego reiniciado")
    }

    // Métodos para controlar diálogos
    fun dismissSuccessDialog() {
        showSuccessDialog = false
    }

    fun dismissWrongDialog() {
        showWrongDialog = false
    }

    // Método para establecer el usuario
    fun setUsuario(nombre: String) {
        currentUserName = nombre
        println("DEBUG - Usuario establecido: $nombre")
    }

    // Propiedades computadas
    val currentPregunta: PreguntaConOpcionesMezcladas
        get() = galderak.getOrNull(galderaIndex) ?: galderak.firstOrNull() ?:
        PreguntaConOpcionesMezcladas(
            texto = R.string.sanmarkos_q_error_text,
            opcionesOriginales = listOf(R.string.sanmarkos_q_error_opt),
            opcionesMezcladas = listOf(R.string.sanmarkos_q_error_opt),
            respuestaCorrectaOriginal = 0,
            respuestaCorrectaMezclada = 0
        )

    val todasPreguntasRespondidas: Boolean
        get() = galderakErantzunda.size == galderak.size
}