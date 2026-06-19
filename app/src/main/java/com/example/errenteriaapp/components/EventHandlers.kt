package com.example.errenteriaapp.components

import androidx.compose.ui.focus.FocusRequester
import com.example.errenteriaapp.database.viewModel.CrucigramaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Fokus-eskakizunen mapa bat sortzen du gurutze-hitzaren zelda guztietarako.
 * Zelda bakoitzak bere FocusRequester duen mapa bat itzultzen du.
 *
 * @return (Errenkada, Zutabea) bikoteetatik FocusRequester-era mapeoa
 */
fun generateFocusRequesters(): MutableMap<Pair<Int, Int>, FocusRequester> {
    return mutableMapOf<Pair<Int, Int>, FocusRequester>().apply {
        for (fila in 0 until 11) {
            for (columna in 0 until 8) {
                this[Pair(fila, columna)] = FocusRequester()
            }
        }
    }
}

/**
 * Zelda bateko letra aldatzean kudeatzen du.
 * Letra ViewModel-ean gordetzen du eta hurrengo zelda hutsera fokua eramaten du.
 *
 * @param fila Zelda zein errenkadatan dagoen
 * @param columna Zelda zein zutabean dagoen
 * @param nuevoCaracter Idatzitako letra berria
 * @param viewModel CrucigramaViewModel
 * @param coroutineScope Koroutineen esparrua
 * @param focusRequesters Fokus-eskakizunen mapa
 */
fun onLetraCambiada(
    fila: Int,
    columna: Int,
    nuevoCaracter: Char?,
    viewModel: CrucigramaViewModel,
    coroutineScope: CoroutineScope,
    focusRequesters: MutableMap<Pair<Int, Int>, FocusRequester>
) {
    if (nuevoCaracter == null) return

    // Letra ViewModel-ean gorde
    viewModel.onLetraCambiada(fila, columna, nuevoCaracter)

    // Hurrengo zelda hutsera joan
    coroutineScope.launch {
        val siguienteCelda = viewModel.obtenerSiguienteCeldaVacia(fila, columna)
        siguienteCelda?.let { (nextFila, nextColumna) ->
            focusRequesters[Pair(nextFila, nextColumna)]?.requestFocus()
        }
    }
}

/**
 * Zelda batetik letra ezabatu eta atzera mugitzea kudeatzen du.
 * Letra ezabatu eta fokua aurreko zelda batera eramaten du.
 *
 * @param fila Zelda zein errenkadatan dagoen
 * @param columna Zelda zein zutabean dagoen
 * @param viewModel CrucigramaViewModel
 * @param coroutineScope Koroutineen esparrua
 * @param focusRequesters Fokus-eskakizunen mapa
 */
fun onBorrarYRetroceder(
    fila: Int,
    columna: Int,
    viewModel: CrucigramaViewModel,
    coroutineScope: CoroutineScope,
    focusRequesters: Map<Pair<Int, Int>, FocusRequester>
) {
    // 1. Lehenik, zelda AKTUALEKO letra ezabatu (baldin badu)
    val celda = viewModel.obtenerCelda(fila, columna)
    if (celda != null && !celda.esNegra && !celda.esCorrecta && celda.letraUsuario != null) {
        viewModel.borrarLetraInteligente(fila, columna)
    }

    // 2. Ondoren, fokua aurreko zelda batera mugitu (logika adimentsua erabiliz)
    viewModel.moverFocoAnteriorDesdeBorrar(
        fila,
        columna,
        coroutineScope,
        focusRequesters
    )
}

/**
 * Zelda batean klik egitean kudeatzen du.
 * Zelda horretan dagoen hitza aktibatzen du, hitza aukeratu ahal izateko.
 *
 * @param fila Zelda zein errenkadatan dagoen
 * @param columna Zelda zein zutabean dagoen
 * @param viewModel CrucigramaViewModel
 */
fun onClickCelda(
    fila: Int,
    columna: Int,
    viewModel: CrucigramaViewModel
) {
    val palabrasEnCelda = viewModel.encontrarPalabraEnCelda(fila, columna)
    if (palabrasEnCelda.isNotEmpty()) {
        val celda = viewModel.obtenerCelda(fila, columna)
        // Zelda beltza edo zuzena ez bada, hitza aktibatu
        if (celda != null && !celda.esNegra && !celda.esCorrecta) {
            viewModel.activarPalabraPorNumero(palabrasEnCelda.first().numero)
        }
    }
}