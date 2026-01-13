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
fun CrucigramaHeader() {
    Text(
        text = "GURUTZEGRAMA",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2196F3),
        modifier = Modifier.padding(bottom = 4.dp, top = 10.dp)
    )

    Text(
        text = "Aizpitarte-Hitz gurutzatuak",
        fontSize = 12.sp,
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 20.dp)
    )
}