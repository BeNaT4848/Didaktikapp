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
    focusRequesters: Map<Pair<Int, Int>, FocusRequester>
) {
    // 1. Primero borra la letra de la celda ACTUAL si tiene
    val celda = viewModel.obtenerCelda(fila, columna)
    if (celda != null && !celda.esNegra && !celda.esCorrecta && celda.letraUsuario != null) {
        viewModel.borrarLetraInteligente(fila, columna)
    }

    // 2. Luego mueve el foco a la celda anterior (según la lógica inteligente)
    viewModel.moverFocoAnteriorDesdeBorrar(
        fila,
        columna,
        coroutineScope,
        focusRequesters
    )
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