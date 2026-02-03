package com.example.errenteriaapp.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
 * Instrumentu baten elementua erakusten du.
 * Egoera erakusten du (aurkitu den edo ez) eta kolore bereizgarria.
 *
 * @param nombre Instrumentuaren izena
 * @param encontrado Instrumentua aurkitu den
 * @param color Instrumentuaren kolore bereizgarria
 * @param modifier Modifier gehigarria
 */
@Composable
fun InstrumentoItem(
    nombre: String,
    encontrado: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (encontrado) Color(0xFFC8E6C9) else MaterialTheme.colorScheme.onPrimary
        ),
        border = BorderStroke(
            1.dp,
            if (encontrado) color else Color.Gray.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Egoera-adierazlea
            Box(
                modifier = Modifier
                    .size(11.dp)
                    .clip(CircleShape)
                    .background(if (encontrado) color else Color.Transparent)
                    .border(
                        1.5.dp,
                        if (encontrado) color else Color.Black,
                        CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Instrumentuaren izena
            Text(
                text = nombre,
                color = Color.Black,
                fontWeight = if (encontrado) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )

            // Egiaztatze-marka aurkitu bada
            if (encontrado) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "✓",
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}