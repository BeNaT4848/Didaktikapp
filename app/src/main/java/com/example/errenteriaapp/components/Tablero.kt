package com.example.errenteriaapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.errenteriaapp.classes.PalabraSopa

/**
 * Sopa-letren jokoaren taula erakusten du.
 * Letraz osatutako taula bat erakusten du eta hitzen atzipena kudeatzen du.
 *
 * @param tablero Letraz osatutako taula (array bikoitza)
 * @param palabras Hitz guztien zerrenda
 * @param palabrasEncontradas Topatutako hitzen zerrenda
 * @param onCeldaClick Zelda batean klik egitean deitzen den funtzioa (errenkada, zutabea, hitza)
 * @param modifier Modifier gehigarria
 */
@Composable
fun SopaDeLetrasTablero(
    tablero: Array<CharArray>,
    palabras: List<PalabraSopa>,
    palabrasEncontradas: List<String>,
    onCeldaClick: (fila: Int, columna: Int, palabraAsociada: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(2.dp, Color(0xFF3700B3), RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(6.dp)
    ) {
        Column {
            for (fila in tablero.indices) {
                Row {
                    for (columna in tablero[fila].indices) {
                        // Zelda hau zein hitzen parte den bilatu
                        var palabraAsociada: String? = null
                        var encontrada = false

                        palabras.forEach { palabra ->
                            if (palabra.posiciones.contains(Pair(fila, columna))) {
                                palabraAsociada = palabra.texto
                                encontrada = palabrasEncontradas.contains(palabra.texto)
                            }
                        }

                        CeldaSopa(
                            letra = tablero[fila][columna],
                            encontrada = encontrada,
                            onClick = { onCeldaClick(fila, columna, palabraAsociada) },
                            modifier = Modifier.padding(0.dp)
                        )
                    }
                }
            }
        }
    }
}