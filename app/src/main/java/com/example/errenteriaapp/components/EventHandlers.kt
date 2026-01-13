package com.example.errenteriaapp.components

import androidx.compose.ui.focus.FocusRequester
import com.example.errenteriaapp.database.viewModel.CrucigramaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun generateFocusRequesters(): MutableMap<Pair<Int, Int>, FocusRequester> {
    return mutableMapOf<Pair<Int, Int>, FocusRequester>().apply {
        for (fila in 0 until 11) {
            for (columna in 0 until 8) {
                this[Pair(fila, columna)] = FocusRequester()
            }
        }
    }
}

fun onLetraCambiada(
    fila: Int,
    columna: Int,
    nuevoCaracter: Char?,
    viewModel: CrucigramaViewModel,
    coroutineScope: CoroutineScope,
    focusRequesters: MutableMap<Pair<Int, Int>, FocusRequester>
) {
    if (nuevoCaracter == null) return

    viewModel.onLetraCambiada(fila, columna, nuevoCaracter)

    coroutineScope.launch {
        val siguienteCelda = viewModel.obtenerSiguienteCeldaVacia(fila, columna)
        siguienteCelda?.let { (nextFila, nextColumna) ->
            focusRequesters[Pair(nextFila, nextColumna)]?.requestFocus()
        }
    }
}

fun onBorrarYRetroceder(
    fila: Int,
    columna: Int,
    viewModel: CrucigramaViewModel,
    coroutineScope: CoroutineScope,
    focusRequesters: MutableMap<Pair<Int, Int>, FocusRequester>
) {
    coroutineScope.launch {
        viewModel.borrarLetra(fila, columna)

        val anteriorCelda = viewModel.obtenerCeldaAnterior(fila, columna)
        anteriorCelda?.let { (prevFila, prevColumna) ->
            focusRequesters[Pair(prevFila, prevColumna)]?.requestFocus()
        } ?: run {
            focusRequesters[Pair(fila, columna)]?.requestFocus()
        }
    }
}

fun onClickCelda(
    fila: Int,
    columna: Int,
    viewModel: CrucigramaViewModel
) {
    val palabrasEnCelda = viewModel.encontrarPalabraEnCelda(fila, columna)
    if (palabrasEnCelda.isNotEmpty()) {
        val celda = viewModel.obtenerCelda(fila, columna)
        if (celda != null && !celda.esNegra && !celda.esCorrecta) {
            viewModel.activarPalabraPorNumero(palabrasEnCelda.first().numero)
        }
    }
}