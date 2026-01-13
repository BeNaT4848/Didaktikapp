package com.example.errenteriaapp.database.viewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.classes.CeldaEstado
import com.example.errenteriaapp.classes.CrucigramaEstado
import com.example.errenteriaapp.classes.PalabraInfo

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CrucigramaViewModel : ViewModel() {
    // Estado del crucigrama
    private val _celdas = mutableStateOf(crearCeldasEstado())
    val celdas: State<List<CeldaEstado>> = _celdas

    private val _crucigramaEstado = mutableStateOf(CrucigramaEstado())
    val crucigramaEstado: State<CrucigramaEstado> = _crucigramaEstado

    private val _verificacionRealizada = mutableStateOf(false)
    val verificacionRealizada: State<Boolean> = _verificacionRealizada

    private val _mostrarDialogoExito = mutableStateOf(false)
    val mostrarDialogoExito: State<Boolean> = _mostrarDialogoExito

    private val _palabraActiva = mutableStateOf<PalabraInfo?>(null)
    val palabraActiva: State<PalabraInfo?> = _palabraActiva

    private val _mostrarInstruccionesIniciales = mutableStateOf(true)
    val mostrarInstruccionesIniciales: State<Boolean> = _mostrarInstruccionesIniciales

    // Función para obtener una celda específica
    fun obtenerCelda(fila: Int, columna: Int): CeldaEstado? {
        return _celdas.value.find { it.fila == fila && it.columna == columna }
    }

    // Función para actualizar una celda
    private fun actualizarCelda(fila: Int, columna: Int, actualizacion: (CeldaEstado) -> CeldaEstado) {
        val index = _celdas.value.indexOfFirst { it.fila == fila && it.columna == columna }
        if (index != -1) {
            val nuevaLista = _celdas.value.toMutableList()
            nuevaLista[index] = actualizacion(nuevaLista[index])
            _celdas.value = nuevaLista
        }
    }

    // Función para encontrar la palabra en una celda
    fun encontrarPalabraEnCelda(fila: Int, columna: Int): List<PalabraInfo> {
        return _crucigramaEstado.value.mapaCeldas[Pair(fila, columna)] ?: emptyList()
    }

    fun juegoCompletado(): Boolean {
        return _celdas.value
            .filter { !it.esNegra }
            .all { it.letraUsuario != null && it.esCorrecta }
    }

    // Función para activar una palabra por su número
    fun activarPalabraPorNumero(numero: Int) {
        viewModelScope.launch {
            // Buscar la palabra por número
            val palabra = _crucigramaEstado.value.mapaPalabrasPorNumero[numero]
            palabra?.let {
                // Si la misma palabra ya está activa, desactivarla
                if (_palabraActiva.value?.numero == numero) {
                    _palabraActiva.value = null
                } else {
                    // Activar la nueva palabra
                    _palabraActiva.value = it

                    // Pequeño delay para asegurar que la UI se actualice
                    delay(50)
                }
            }
        }
    }

    fun onLetraCambiada(fila: Int, columna: Int, nuevoCaracter: Char?) {
        if (nuevoCaracter == null) return

        // 1. Actualizar letra
        actualizarCelda(fila, columna) { celda ->
            if (!celda.esNegra && !celda.esCorrecta) {
                celda.copy(
                    letraUsuario = nuevoCaracter.uppercaseChar(),
                    esCorrecta = false
                )
            } else celda
        }
    }

    fun borrarLetra(fila: Int, columna: Int) {
        val celdaActual = obtenerCelda(fila, columna)
        if (celdaActual?.letraUsuario != null && !celdaActual.esCorrecta) {
            actualizarCelda(fila, columna) { it.copy(letraUsuario = null, esCorrecta = false) }
        }
    }

    // Función para verificar si una celda pertenece a la palabra activa
    fun esCeldaDePalabraActiva(fila: Int, columna: Int): Boolean {
        val palabrasEnCelda = _crucigramaEstado.value.mapaCeldas[Pair(fila, columna)] ?: return false
        return palabrasEnCelda.any { it.numero == _palabraActiva.value?.numero }
    }

    // Función para encontrar la siguiente celda vacía en la palabra activa
    fun obtenerSiguienteCeldaVacia(filaActual: Int, columnaActual: Int): Pair<Int, Int>? {
        val palabra = _palabraActiva.value ?: return null

        val posActual = if (palabra.direccion == "HORIZONTAL") {
            columnaActual - palabra.columnaInicio
        } else {
            filaActual - palabra.filaInicio
        }

        // Buscar la siguiente casilla VACÍA dentro de la palabra
        for (i in posActual + 1 until palabra.longitud) {
            val nextFila = if (palabra.direccion == "HORIZONTAL") {
                palabra.filaInicio
            } else {
                palabra.filaInicio + i
            }

            val nextColumna = if (palabra.direccion == "HORIZONTAL") {
                palabra.columnaInicio + i
            } else {
                palabra.columnaInicio
            }

            val celda = obtenerCelda(nextFila, nextColumna)
            if (celda != null && !celda.esNegra && celda.letraUsuario == null) {
                return Pair(nextFila, nextColumna)
            }
        }

        return null
    }

    // Función para obtener la celda anterior en la palabra activa
    fun obtenerCeldaAnterior(filaActual: Int, columnaActual: Int): Pair<Int, Int>? {
        val palabra = _palabraActiva.value ?: return null

        val posActual = if (palabra.direccion == "HORIZONTAL") {
            columnaActual - palabra.columnaInicio
        } else {
            filaActual - palabra.filaInicio
        }

        // Buscar la casilla anterior
        if (posActual > 0) {
            val prevFila = if (palabra.direccion == "HORIZONTAL") {
                palabra.filaInicio
            } else {
                palabra.filaInicio + (posActual - 1)
            }

            val prevColumna = if (palabra.direccion == "HORIZONTAL") {
                palabra.columnaInicio + (posActual - 1)
            } else {
                palabra.columnaInicio
            }

            val celda = obtenerCelda(prevFila, prevColumna)
            if (celda != null && !celda.esNegra && !celda.esCorrecta) {
                return Pair(prevFila, prevColumna)
            }
        }

        // Si no hay anterior, devolver la primera
        return Pair(palabra.filaInicio, palabra.columnaInicio)
    }

    // Función para verificar respuestas
    fun verificarRespuestas() {
        // Resetear verificaciones previas
        val nuevaLista = _celdas.value.toMutableList()
        nuevaLista.forEachIndexed { index, celda ->
            if (!celda.esNegra) {
                nuevaLista[index] = celda.copy(
                    esCorrecta = false
                )
            }
        }
        _celdas.value = nuevaLista

        // Verificar respuestas
        verificarHorizontal(1, 0, 2, "LEIZEA")
        verificarHorizontal(5, 3, 0, "ZUTABEA")
        verificarVertical(2, 0, 3, "ESTALAGMITA")
        verificarVertical(4, 2, 1, "RUPESTRE")
        verificarVertical(3, 0, 6, "ESTALAKTITA")

        _verificacionRealizada.value = true

        if (juegoCompletado()) {
            _mostrarDialogoExito.value = true
        }
    }

    // Funciones de verificación
    private fun verificarHorizontal(
        numeroPista: Int,
        filaInicio: Int,
        columnaInicio: Int,
        respuesta: String
    ) {
        var columna = columnaInicio
        val nuevaLista = _celdas.value.toMutableList()

        for (letra in respuesta) {
            val index = nuevaLista.indexOfFirst {
                it.fila == filaInicio && it.columna == columna
            }
            if (index != -1) {
                val celda = nuevaLista[index]
                val coincide = celda.letraUsuario?.equals(letra, ignoreCase = true) ?: false
                nuevaLista[index] = celda.copy(
                    esCorrecta = celda.esCorrecta || coincide
                )
            }
            columna++
        }

        _celdas.value = nuevaLista
    }

    private fun verificarVertical(
        numeroPista: Int,
        filaInicio: Int,
        columnaInicio: Int,
        respuesta: String
    ) {
        var fila = filaInicio
        val nuevaLista = _celdas.value.toMutableList()

        for (letra in respuesta) {
            val index = nuevaLista.indexOfFirst {
                it.fila == fila && it.columna == columnaInicio
            }
            if (index != -1) {
                val celda = nuevaLista[index]
                val esCorrecta = celda.letraUsuario?.equals(letra, ignoreCase = true) ?: false
                nuevaLista[index] = celda.copy(esCorrecta = esCorrecta)
            }
            fila++
        }

        _celdas.value = nuevaLista
    }

    // Métodos para controlar UI
    fun cerrarInstrucciones() {
        _mostrarInstruccionesIniciales.value = false
    }

    fun cerrarDialogoExito() {
        _mostrarDialogoExito.value = false
    }

    fun desactivarPalabra() {
        _palabraActiva.value = null
    }

    // Función para crear celdas
    private fun crearCeldasEstado(): List<CeldaEstado> {
        val celdas = mutableListOf<CeldaEstado>()

        // Fila 0
        celdas.add(CeldaEstado(0, 0, esNegra = true))
        celdas.add(CeldaEstado(0, 1, esNegra = true))
        celdas.add(CeldaEstado(0, 2, esNegra = false, numeroPista = 1, letraCorrecta = 'L'))
        celdas.add(CeldaEstado(0, 3, esNegra = false, numeroPista = 2, letraCorrecta = 'E'))
        celdas.add(CeldaEstado(0, 4, esNegra = false, letraCorrecta = 'I'))
        celdas.add(CeldaEstado(0, 5, esNegra = false, letraCorrecta = 'Z'))
        celdas.add(CeldaEstado(0, 6, esNegra = false, numeroPista = 3, letraCorrecta = 'E'))
        celdas.add(CeldaEstado(0, 7, esNegra = false, letraCorrecta = 'A'))

        // Fila 1
        celdas.add(CeldaEstado(1, 0, esNegra = true))
        celdas.add(CeldaEstado(1, 1, esNegra = true))
        celdas.add(CeldaEstado(1, 2, esNegra = true))
        celdas.add(CeldaEstado(1, 3, esNegra = false, letraCorrecta = 'S'))
        celdas.add(CeldaEstado(1, 4, esNegra = true))
        celdas.add(CeldaEstado(1, 5, esNegra = true))
        celdas.add(CeldaEstado(1, 6, esNegra = false, letraCorrecta = 'S'))
        celdas.add(CeldaEstado(1, 7, esNegra = true))

        // Fila 2
        celdas.add(CeldaEstado(2, 0, esNegra = true))
        celdas.add(CeldaEstado(2, 1, esNegra = false, numeroPista = 4, letraCorrecta = 'R'))
        celdas.add(CeldaEstado(2, 2, esNegra = true))
        celdas.add(CeldaEstado(2, 3, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(2, 4, esNegra = true))
        celdas.add(CeldaEstado(2, 5, esNegra = true))
        celdas.add(CeldaEstado(2, 6, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(2, 7, esNegra = true))

        // Fila 3
        celdas.add(CeldaEstado(3, 0, esNegra = false, numeroPista = 5, letraCorrecta = 'Z'))
        celdas.add(CeldaEstado(3, 1, esNegra = false, letraCorrecta = 'U'))
        celdas.add(CeldaEstado(3, 2, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(3, 3, esNegra = false, letraCorrecta = 'A'))
        celdas.add(CeldaEstado(3, 4, esNegra = false, letraCorrecta = 'B'))
        celdas.add(CeldaEstado(3, 5, esNegra = false, letraCorrecta = 'E'))
        celdas.add(CeldaEstado(3, 6, esNegra = false, letraCorrecta = 'A'))
        celdas.add(CeldaEstado(3, 7, esNegra = true))

        // Fila 4
        celdas.add(CeldaEstado(4, 0, esNegra = true))
        celdas.add(CeldaEstado(4, 1, esNegra = false, letraCorrecta = 'P'))
        celdas.add(CeldaEstado(4, 2, esNegra = true))
        celdas.add(CeldaEstado(4, 3, esNegra = false, letraCorrecta = 'L'))
        celdas.add(CeldaEstado(4, 4, esNegra = true))
        celdas.add(CeldaEstado(4, 5, esNegra = true))
        celdas.add(CeldaEstado(4, 6, esNegra = false, letraCorrecta = 'L'))
        celdas.add(CeldaEstado(4, 7, esNegra = true))

        // Fila 5
        celdas.add(CeldaEstado(5, 0, esNegra = true))
        celdas.add(CeldaEstado(5, 1, esNegra = false, letraCorrecta = 'E'))
        celdas.add(CeldaEstado(5, 2, esNegra = true))
        celdas.add(CeldaEstado(5, 3, esNegra = false, letraCorrecta = 'A'))
        celdas.add(CeldaEstado(5, 4, esNegra = true))
        celdas.add(CeldaEstado(5, 5, esNegra = true))
        celdas.add(CeldaEstado(5, 6, esNegra = false, letraCorrecta = 'A'))
        celdas.add(CeldaEstado(5, 7, esNegra = true))

        // Fila 6
        celdas.add(CeldaEstado(6, 0, esNegra = true))
        celdas.add(CeldaEstado(6, 1, esNegra = false, letraCorrecta = 'S'))
        celdas.add(CeldaEstado(6, 2, esNegra = true))
        celdas.add(CeldaEstado(6, 3, esNegra = false, letraCorrecta = 'G'))
        celdas.add(CeldaEstado(6, 4, esNegra = true))
        celdas.add(CeldaEstado(6, 5, esNegra = true))
        celdas.add(CeldaEstado(6, 6, esNegra = false, letraCorrecta = 'K'))
        celdas.add(CeldaEstado(6, 7, esNegra = true))

        // Fila 7
        celdas.add(CeldaEstado(7, 0, esNegra = true))
        celdas.add(CeldaEstado(7, 1, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(7, 2, esNegra = true))
        celdas.add(CeldaEstado(7, 3, esNegra = false, letraCorrecta = 'M'))
        celdas.add(CeldaEstado(7, 4, esNegra = true))
        celdas.add(CeldaEstado(7, 5, esNegra = true))
        celdas.add(CeldaEstado(7, 6, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(7, 7, esNegra = true))

        // Fila 8
        celdas.add(CeldaEstado(8, 0, esNegra = true))
        celdas.add(CeldaEstado(8, 1, esNegra = false, letraCorrecta = 'R'))
        celdas.add(CeldaEstado(8, 2, esNegra = true))
        celdas.add(CeldaEstado(8, 3, esNegra = false, letraCorrecta = 'I'))
        celdas.add(CeldaEstado(8, 4, esNegra = true))
        celdas.add(CeldaEstado(8, 5, esNegra = true))
        celdas.add(CeldaEstado(8, 6, esNegra = false, letraCorrecta = 'I'))
        celdas.add(CeldaEstado(8, 7, esNegra = true))

        // Fila 9
        celdas.add(CeldaEstado(9, 0, esNegra = true))
        celdas.add(CeldaEstado(9, 1, esNegra = false, letraCorrecta = 'E'))
        celdas.add(CeldaEstado(9, 2, esNegra = true))
        celdas.add(CeldaEstado(9, 3, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(9, 4, esNegra = true))
        celdas.add(CeldaEstado(9, 5, esNegra = true))
        celdas.add(CeldaEstado(9, 6, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(9, 7, esNegra = true))

        // Fila 10
        celdas.add(CeldaEstado(10, 0, esNegra = true))
        celdas.add(CeldaEstado(10, 1, esNegra = true))
        celdas.add(CeldaEstado(10, 2, esNegra = true))
        celdas.add(CeldaEstado(10, 3, esNegra = false, letraCorrecta = 'A'))
        celdas.add(CeldaEstado(10, 4, esNegra = true))
        celdas.add(CeldaEstado(10, 5, esNegra = true))
        celdas.add(CeldaEstado(10, 6, esNegra = false, letraCorrecta = 'A'))
        celdas.add(CeldaEstado(10, 7, esNegra = true))

        return celdas
    }
}