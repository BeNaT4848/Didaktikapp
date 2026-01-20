package com.example.errenteriaapp.database.viewModel

import com.example.errenteriaapp.classes.PalabraSopa



import androidx.lifecycle.ViewModel


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SopaGameState(
    val palabrasEncontradas: List<String> = emptyList(),
    val showSuccessDialog: Boolean = false,
    val showWrongDialog: Boolean = false,
    val mostrarExito: Boolean = false,
    val mostrarPista: Boolean = false
)

class SopaDeLetrasViewModel : ViewModel() {
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

    fun marcarPalabraEncontrada(palabra: String) {
        if (!_gameState.value.palabrasEncontradas.contains(palabra)) {
            val nuevasEncontradas = _gameState.value.palabrasEncontradas + palabra
            _gameState.update { it.copy(palabrasEncontradas = nuevasEncontradas) }

            // Verificar si se completó el juego
            if (nuevasEncontradas.size == palabras.size) {
                _gameState.update { it.copy(mostrarExito = true) }
            }
        }
    }
    fun hideSuccessDialog() {
        _gameState.update { it.copy(mostrarExito = false) }
    }
}