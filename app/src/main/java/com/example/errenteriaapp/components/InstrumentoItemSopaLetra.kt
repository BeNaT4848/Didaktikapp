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

@Composable
fun InstrumentoItem(
    nombre: String,
    encontrado: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (encontrado) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.onPrimary
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
            // Indicador de estado
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

            // Nombre del instrumento
            Text(
                text = nombre,
                color = Color.Black,
                fontWeight = if (encontrado) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )

            // Checkmark si está encontrado
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