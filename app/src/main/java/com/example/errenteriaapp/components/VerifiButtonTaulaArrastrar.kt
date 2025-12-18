package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VerifyButton(
    allSlotsFilled: Boolean,
    onVerifyClick: () -> Unit
) {
    Button(
        onClick = onVerifyClick,
        enabled = allSlotsFilled,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (allSlotsFilled) Color(0xFF2196F3) else Color(0xFF90CAF9),
            contentColor = Color.White
        )
    ) {
        Text(text = "EGIAZTATU", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}