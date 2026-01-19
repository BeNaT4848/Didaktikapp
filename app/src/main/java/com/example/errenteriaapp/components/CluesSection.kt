package com.example.errenteriaapp.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val colorScheme = MaterialTheme.colorScheme

    // Sección de pistas HORIZONTALES
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.secondaryContainer,
            contentColor = colorScheme.onSecondaryContainer
        ),
        border = BorderStroke(1.dp, colorScheme.secondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(colorScheme.secondary)
                )
                Text(
                    text = "HORIZONTALAK",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            pistasHorizontales.forEach { pista ->
                PistaClicableItem(
                    numero = pista.numero,
                    texto = pista.texto,
                    esActiva = palabraActiva?.numero == pista.numero,
                    esHorizontal = true,
                    onClick = { onActivateWord(pista.numero) }
                )
                if (pista != pistasHorizontales.last()) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Sección de pistas VERTICALES
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.tertiaryContainer,
            contentColor = colorScheme.onTertiaryContainer
        ),
        border = BorderStroke(1.dp, colorScheme.tertiary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(colorScheme.tertiary)
                )
                Text(
                    text = "BERTIKALAK",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            pistasVerticales.forEach { pista ->
                PistaClicableItem(
                    numero = pista.numero,
                    texto = pista.texto,
                    esActiva = palabraActiva?.numero == pista.numero,
                    esHorizontal = false,
                    onClick = { onActivateWord(pista.numero) }
                )
                if (pista != pistasVerticales.last()) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
