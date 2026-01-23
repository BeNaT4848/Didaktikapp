// app/src/main/java/com/example/errenteriaapp/database/viewModel/SopaDeLetrasViewModel.kt
package com.example.errenteriaapp.database.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.classes.PalabraSopa
import com.example.errenteriaapp.database.Puntuazioa
import com.example.errenteriaapp.database.PuntuazioaDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SopaGameState(
    val palabrasEncontradas: List<String> = emptyList(),
    val showSuccessDialog: Boolean = false,
    val showWrongDialog: Boolean = false,
    val mostrarExito: Boolean = false,
    val mostrarPista: Boolean = false,
    val puntos: Int = 0
)

class SopaDeLetrasViewModel(
    private val puntuazioaDao: PuntuazioaDao?,
    private val configJuego: ConfigJuego = ConfigJuego.DEFAULT_SOPA
) : ViewModel() {

    data class ConfigJuego(
        val minPalabrasRequeridas: Int,
        val puntosPorPalabra: Int = 5,
        val puntosPorLetra: Int = 1,
        val puntosExtraPerfecto: Int = 1,
        val puntosExtraTodasPalabras: Int = 2
    ) {
        companion object {
            val DEFAULT_SOPA = ConfigJuego(
                minPalabrasRequeridas = 5, // Mínimo 5 de las 8 palabras
                puntosPorPalabra = 5,
                puntosPorLetra = 1,
                puntosExtraPerfecto = 1,
                puntosExtraTodasPalabras = 2
            )
        }
    }

    // Añade esta variable para el nombre del usuario
    var currentUserName: String? = null

    private val _gameState = MutableStateFlow(SopaGameState())
    val gameState = _gameState.asStateFlow()

    val palabras = listOf(
        PalabraSopa(
            texto = "SAXOFOIA",
            posiciones = listOf(
                Pair(0, 3), Pair(0, 4), Pair(0, 5), Pair(0, 6),
                Pair(0, 7), Pair(0, 8), Pair(0, 9), Pair(0, 10)
            )
        ),
        PalabraSopa(
            texto = "ZEHARTXIRULA",
            posiciones = listOf(
                Pair(6, 0), Pair(6, 1), Pair(6, 2), Pair(6, 3),
                Pair(6, 4), Pair(6, 5), Pair(6, 6), Pair(6, 7),
                Pair(6, 8), Pair(6, 9), Pair(6, 10), Pair(6, 11)
            )
        ),
        PalabraSopa(
            texto = "KLARINETEA",
            posiciones = listOf(
                Pair(10, 0), Pair(10, 1), Pair(10, 2), Pair(10, 3),
                Pair(10, 4), Pair(10, 5), Pair(10, 6), Pair(10, 7),
                Pair(10, 8), Pair(10, 9)
            )
        ),
        PalabraSopa(
            texto = "DANBORRA",
            posiciones = listOf(
                Pair(11, -1), Pair(11, 0), Pair(11, 1), Pair(11, 2),
                Pair(11, 3), Pair(11, 4), Pair(11, 5), Pair(11, 6), Pair(11, 7)
            )
        ),
        PalabraSopa(
            texto = "TXINDATAK",
            posiciones = listOf(
                Pair(0, 12), Pair(1, 12), Pair(2, 12), Pair(3, 12),
                Pair(4, 12), Pair(5, 12), Pair(6, 12), Pair(7, 12), Pair(8, 12)
            )
        ),
        PalabraSopa(
            texto = "TRONPETA",
            posiciones = listOf(
                Pair(8, 3), Pair(8, 4), Pair(8, 5), Pair(8, 6),
                Pair(8, 7), Pair(8, 8), Pair(8, 9), Pair(8, 10)
            )
        ),
        PalabraSopa(
            texto = "TRONPA",
            posiciones = listOf(
                Pair(3, 6), Pair(3, 7), Pair(3, 8), Pair(3, 9),
                Pair(3, 10), Pair(3, 11)
            )
        ),
        PalabraSopa(
            texto = "TRONBOIA",
            posiciones = listOf(
                Pair(13, 5), Pair(13, 6), Pair(13, 7), Pair(13, 8),
                Pair(13, 9), Pair(13, 10), Pair(13, 11), Pair(13, 12)
            )
        )
    )

    val tablero = arrayOf(
        charArrayOf('T', 'H', 'H', 'S', 'A', 'X', 'O', 'F', 'O', 'I', 'A', 'Z', 'T', 'A'),
        charArrayOf('Q', 'B', 'P', 'B', 'W', 'U', 'G', 'S', 'Y', 'P', 'R', 'J', 'X', 'A'),
        charArrayOf('P', 'M', 'K', 'J', 'G', 'X', 'E', 'G', 'K', 'O', 'E', 'R', 'I', 'C'),
        charArrayOf('I', 'Q', 'Q', 'L', 'B', 'S', 'T', 'R', 'O', 'N', 'P', 'A', 'N', 'K'),
        charArrayOf('U', 'H', 'M', 'L', 'N', 'E', 'R', 'F', 'I', 'G', 'M', 'D', 'D', 'I'),
        charArrayOf('O', 'A', 'G', 'R', 'H', 'G', 'M', 'G', 'X', 'D', 'H', 'X', 'A', 'M'),
        charArrayOf('Z', 'E', 'H', 'A', 'R', 'T', 'X', 'I', 'R', 'U', 'L', 'A', 'T', 'C'),
        charArrayOf('X', 'C', 'Y', 'O', 'U', 'E', 'A', 'K', 'V', 'V', 'T', 'B', 'A', 'J'),
        charArrayOf('C', 'N', 'C', 'T', 'R', 'O', 'N', 'P', 'E', 'T', 'A', 'A', 'K', 'C'),
        charArrayOf('I', 'L', 'V', 'Y', 'G', 'G', 'E', 'Y', 'G', 'S', 'L', 'G', 'J', 'I'),
        charArrayOf('K', 'L', 'A', 'R', 'I', 'N', 'E', 'T', 'E', 'A', 'J', 'I', 'D', 'U'),
        charArrayOf('D', 'A', 'N', 'B', 'O', 'R', 'R', 'A', 'U', 'F', 'X', 'H', 'I', 'D'),
        charArrayOf('I', 'B', 'A', 'W', 'E', 'W', 'H', 'K', 'C', 'X', 'C', 'S', 'W', 'X'),
        charArrayOf('M', 'Z', 'X', 'C', 'T', 'T', 'R', 'O', 'N', 'B', 'O', 'I', 'A', 'T')
    )

    // Calcular puntuación total
    private fun calcularPuntuacion(): Int {
        val palabrasEncontradas = _gameState.value.palabrasEncontradas.size
        var puntos = 0

        // Puntos por palabras encontradas
        puntos += palabrasEncontradas * configJuego.puntosPorPalabra

        // Puntos por letras en las palabras encontradas
        val totalLetras = palabras.sumOf { if (it.texto in _gameState.value.palabrasEncontradas) it.texto.length else 0 }
        puntos += totalLetras * configJuego.puntosPorLetra

        // Bonus por encontrar todas las palabras
        if (palabrasEncontradas == palabras.size) {
            puntos += configJuego.puntosExtraTodasPalabras
        }

        // Bonus extra si se encuentra una cantidad significativa
        if (palabrasEncontradas >= configJuego.minPalabrasRequeridas * 1.5) {
            puntos += configJuego.puntosExtraPerfecto
        }

        return puntos
    }

    fun marcarPalabraEncontrada(palabra: String) {
        if (!_gameState.value.palabrasEncontradas.contains(palabra)) {
            val nuevasEncontradas = _gameState.value.palabrasEncontradas + palabra

            // Calcular nuevos puntos
            val nuevosPuntos = calcularPuntuacionParaPalabra(palabra)

            _gameState.update {
                it.copy(
                    palabrasEncontradas = nuevasEncontradas,
                    puntos = it.puntos + nuevosPuntos
                )
            }

            // Verificar si se completó el juego
            if (nuevasEncontradas.size == palabras.size) {
                // Guardar puntos cuando se completan todas las palabras
                guardarPuntuacionFinal()
                _gameState.update { it.copy(mostrarExito = true) }
            } else if (nuevasEncontradas.size >= configJuego.minPalabrasRequeridas) {
                // Si alcanza el mínimo, guardar puntos parciales
                guardarPuntuacionParcial(nuevasEncontradas.size)
            }
        }
    }

    private fun calcularPuntuacionParaPalabra(palabra: String): Int {
        val palabraInfo = palabras.find { it.texto == palabra }
        return if (palabraInfo != null) {
            // Puntos por palabra + puntos por letras
            configJuego.puntosPorPalabra + (palabraInfo.texto.length * configJuego.puntosPorLetra)
        } else {
            0
        }
    }

    private fun guardarPuntuacionParcial(palabrasEncontradas: Int) {
        viewModelScope.launch {
            currentUserName?.let { nombreUsuario ->
                puntuazioaDao?.let { dao ->
                    val puntuazioActual = dao.getByName(nombreUsuario)
                    val puntosParciales = calcularPuntuacion()

                    if (puntuazioActual != null) {
                        // Si ya hay puntos, sumar los nuevos
                        val nuevaPuntuazio = puntuazioActual.copy(
                            puntuazioaSopaLetra = puntosParciales
                        )
                        dao.insert(nuevaPuntuazio)
                    } else {
                        val nuevaPuntuazio = Puntuazioa(
                            izenaAbizena = nombreUsuario,
                            puntuazioaBertso = 0,
                            puntuazioaGalderak = 0,
                            puntuazioaGurutzegrama = 0,
                            puntuazioaArropaBuruHandiak = 0,
                            puntuazioaPapresa = 0,
                            puntuazioaArrastrar = 0,
                            puntuazioaSopaLetra = puntosParciales
                        )
                        dao.insert(nuevaPuntuazio)
                    }
                }
            }
        }
    }

    private fun guardarPuntuacionFinal() {
        viewModelScope.launch {
            currentUserName?.let { nombreUsuario ->
                puntuazioaDao?.let { dao ->
                    val puntuazioActual = dao.getByName(nombreUsuario)
                    val puntosFinales = calcularPuntuacion()

                    if (puntuazioActual != null) {
                        val nuevaPuntuazio = puntuazioActual.copy(
                            puntuazioaSopaLetra = puntosFinales
                        )
                        dao.insert(nuevaPuntuazio)
                    } else {
                        val nuevaPuntuazio = Puntuazioa(
                            izenaAbizena = nombreUsuario,
                            puntuazioaBertso = 0,
                            puntuazioaGalderak = 0,
                            puntuazioaGurutzegrama = 0,
                            puntuazioaArropaBuruHandiak = 0,
                            puntuazioaPapresa = 0,
                            puntuazioaArrastrar = 0,
                            puntuazioaSopaLetra = puntosFinales
                        )
                        dao.insert(nuevaPuntuazio)
                    }
                }
            }
        }
    }

    fun hideSuccessDialog() {
        _gameState.update { it.copy(mostrarExito = false) }
    }

    // Método para establecer el usuario
    fun setUsuario(nombre: String) {
        currentUserName = nombre
    }
}

