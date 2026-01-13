package com.example.errenteriaapp.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.classes.PalabraInfo


data class PistaInfo(
    val numero: Int,
    val texto: String
)

@Composable
fun CluesSection(
    pistasHorizontales: List<PistaInfo>,
    pistasVerticales: List<PistaInfo>,
    palabraActiva: PalabraInfo?,
    onActivateWord: (Int) -> Unit
) {
    // Sección de pistas HORIZONTALES
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9)
        ),
        border = BorderStroke(1.dp, Color(0xFF4CAF50))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "HORIZONTALAK",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            pistasHorizontales.forEach { pista ->
                PistaClicableItem(
                    numero = pista.numero,
                    texto = pista.texto,
                    esActiva = palabraActiva?.numero == pista.numero,
                    esHorizontal = true,
                    onClick = { onActivateWord(pista.numero) }
                )
                if (pista != pistasHorizontales.last()) {
                    Spacer(modifier = Modifier.height(3.dp))
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Sección de pistas VERTICALES
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        ),
        border = BorderStroke(1.dp, Color(0xFF2196F3))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "BERTIKALAK",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            pistasVerticales.forEach { pista ->
                PistaClicableItem(
                    numero = pista.numero,
                    texto = pista.texto,
                    esActiva = palabraActiva?.numero == pista.numero,
                    esHorizontal = false,
                    onClick = { onActivateWord(pista.numero) }
                )
                if (pista != pistasVerticales.last()) {
                    Spacer(modifier = Modifier.height(3.dp))
                }
            }
        }
    }
}