package com.example.errenteriaapp.components



import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InstructionText(
    modifier: Modifier = Modifier
) {
    Text(
        text = "Sakatu ontzi egokia:",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2E7D32),
        modifier = modifier.padding(bottom = 8.dp)
    )
}