package com.example.errenteriaapp.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Pistaren elementu klikagarria erakusten du.
 * Erabiltzaileak pista batean klik egin dezake hitz aktiboa aukeratzeko.
 *
 * @param numero Pistaren zenbakia
 * @param texto Pistaren testua
 * @param esActiva Pista hau aktibatuta dagoen
 * @param esHorizontal Pista horizontala den (false bada, bertikala da)
 * @param onClick Pistaren elementuan klik egitean deitzen den funtzioa
 */
@Composable
fun PistaClicableItem(
    numero: Int,
    texto: String,
    esActiva: Boolean,
    esHorizontal: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    // Koloreak horizontala/bertikala denaren arabera zehaztu
    val (colorPrincipal, colorContenedor, colorSobreContenedor) = if (esHorizontal) {
        Triple(
            colorScheme.secondary,
            colorScheme.secondaryContainer,
            colorScheme.onSecondaryContainer
        )
    } else {
        Triple(
            colorScheme.tertiary,
            colorScheme.tertiaryContainer,
            colorScheme.onTertiaryContainer
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = if (esActiva) colorPrincipal else colorContenedor,
        contentColor = if (esActiva) colorScheme.onSecondary else colorSobreContenedor,
        shape = MaterialTheme.shapes.small,
        tonalElevation = if (esActiva) 4.dp else 1.dp,
        border = if (esActiva) BorderStroke(1.dp, colorPrincipal) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Adierazle bisuala H/V (norabidea)
            Surface(
                shape = MaterialTheme.shapes.extraSmall,
                color = if (esActiva) colorScheme.onSecondary else colorPrincipal,
                contentColor = if (esActiva) colorPrincipal else colorScheme.onSecondary,
                modifier = Modifier.size(20.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (esHorizontal) "→" else "↓",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Pistaren zenbakia
            Surface(
                shape = MaterialTheme.shapes.small,
                color = if (esActiva) colorScheme.onSecondary else colorScheme.surface,
                contentColor = if (esActiva) colorPrincipal else colorPrincipal,
                modifier = Modifier.size(26.dp),
                tonalElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "$numero",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Pistaren testua
            Text(
                text = texto,
                fontSize = 14.sp,
                fontWeight = if (esActiva) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            // Aktibo adierazlea
            if (esActiva) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(colorScheme.surface)
                )
            }
        }
    }
}