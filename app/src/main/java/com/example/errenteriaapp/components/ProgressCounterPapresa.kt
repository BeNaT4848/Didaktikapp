package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Aurrerapen-kontagailua erakusten du.
 * Uneko posizioa eta elementu kopuru osoa erakusten ditu (adibidez, "3/8").
 *
 * @param currentIndex Uneko indizea (0tik hasita)
 * @param totalItems Elementu kopuru osoa
 * @param modifier Modifier gehigarria
 */
@Composable
fun ProgressCounter(
    currentIndex: Int,
    totalItems: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = "${currentIndex + 1}/$totalItems",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = modifier.padding(bottom = 8.dp)
    )
}