package com.example.errenteriaapp.components



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CeldaSopa(
    letra: Char,
    encontrada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorFondo = if (encontrada) Color(0xFFC8E6C9)else Color.Transparent
    val colorTexto = if (encontrada) Color.Black else Color.Black

    Box(
        modifier = modifier
            .size(22.dp)
            .border(0.5.dp, Color(0xFF444444))
            .background(colorFondo)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letra.toString(),
            color = colorTexto,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp
        )
    }
}