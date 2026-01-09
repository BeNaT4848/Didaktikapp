package com.example.errenteriaapp.components



import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PapresaTitle(
    modifier: Modifier = Modifier
) {
    Text(
        text = "PAPRESA JOLASA",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(16.dp)
            .padding(top = 30.dp)
    )
}