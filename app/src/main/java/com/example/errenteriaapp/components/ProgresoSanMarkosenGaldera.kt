package com.example.errenteriaapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.R

/**
 * Galdetegiaren aurrerapenaren adierazlea erakusten du.
 * Galdera bakoitzaren egoera erakusten du zirkulu adierazleekin.
 *
 * @param galderaIndex Uneko galderaren indizea (0tik hasita)
 * @param totalGalderak Galdera kopuru osoa
 * @param erantzunak Galderen erantzunen mapa (galderaIndex → (hautatutakoIndex, zuzena))
 */
@Composable
fun ProgressIndicator(
    galderaIndex: Int,
    totalGalderak: Int,
    erantzunak: Map<Int, Pair<Int, Boolean>>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // "X/Y" testua (adibidez, "2/3")
        Text(
            text = stringResource(
                R.string.game_quiz_question_of,
                galderaIndex + 1,
                totalGalderak
            ),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Egoeradun aurrerapen-puntuak
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(totalGalderak) { index ->
                Box(
                    modifier = Modifier.size(40.dp)
                ) {
                    // Zirkulu nagusia
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    index < galderaIndex -> Color(0xFF4CAF50)   // Berdea (gaindituta)
                                    index == galderaIndex -> Color(0xFF2196F3)  // Urdina (unekoa)
                                    else -> Color(0xFFE0E0E0)                  // Grisa (etorkizunekoa)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Galderaren zenbakia
                        Text(
                            text = "${index + 1}",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Erantzunaren adierazlea
                    if (erantzunak.containsKey(index)) {
                        val (_, esCorrecta) = erantzunak[index]!!
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(
                                    if (esCorrecta) Color(0xFF4CAF50) else Color(0xFFF44336) // Berdea/gorria
                                )
                                .border(
                                    width = 2.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            // Hutsik utzi, baina testua edo ikonoa gehi daiteke
                        }
                    }
                }
            }
        }
    }
}