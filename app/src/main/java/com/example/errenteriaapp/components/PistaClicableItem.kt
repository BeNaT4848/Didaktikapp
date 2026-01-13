package com.example.errenteriaapp.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*

@Composable
fun PistaClicableItem(numero: Int, texto: String, esActiva: Boolean, esHorizontal: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (esActiva) Color(0xFFFFF3E0) else Color.Transparent
        ),
        border = if (esActiva) BorderStroke(2.dp, Color(0xFFFF9800)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(
                        color = if (esActiva) Color(0xFFFF9800) else
                            if (esHorizontal) Color(0xFFC8E6C9) else Color(0xFFBBDEFB),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                    .border(
                        1.dp,
                        if (esActiva) Color.Red else
                            if (esHorizontal) Color(0xFF4CAF50) else Color(0xFF2196F3),
                        androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$numero",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (esActiva) Color.White else
                        if (esHorizontal) Color(0xFF2E7D32) else Color(0xFF1565C0)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = texto,
                fontSize = 15.sp,
                color = if (esActiva) Color(0xFFE65100) else Color.Black,
                modifier = Modifier.weight(1f)
            )
        }
    }
}