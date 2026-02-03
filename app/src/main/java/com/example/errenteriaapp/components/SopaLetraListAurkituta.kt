package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.R

/**
 * Sopa-letren jokoaren hitzen zerrenda erakusten du.
 * Topatutako hitzak markatuta erakusten ditu.
 *
 * @param palabras Hitz guztien zerrenda
 * @param palabrasEncontradas Topatutako hitzen zerrenda
 * @param modifier Modifier gehigarria
 */
@Composable
fun SopaPalabrasList(
    palabras: List<com.example.errenteriaapp.classes.PalabraSopa>,
    palabrasEncontradas: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Goiburua
            Text(
                text = stringResource(R.string.game_sopa_found_title),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 2 zutabetan antolatutako zerrenda
            val columnas = 2
            val filasNecesarias = (palabras.size + columnas - 1) / columnas

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                for (fila in 0 until filasNecesarias) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (col in 0 until columnas) {
                            val index = fila * columnas + col
                            if (index < palabras.size) {
                                val palabra = palabras[index]
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp, vertical = 4.dp)
                                ) {
                                    InstrumentoItem(
                                        nombre = palabra.texto,
                                        encontrado = palabrasEncontradas.contains(palabra.texto),
                                        color = Color.Black
                                    )
                                }
                            } else {
                                // Hutsunea betetzeko
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}