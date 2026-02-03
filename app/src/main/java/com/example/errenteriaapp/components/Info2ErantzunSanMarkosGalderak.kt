package com.example.errenteriaapp.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Galdetegiaren baldintza informazioa erakusten du.
 * Zenbat erantzun zuzen behar diren proba gainditzeko erakusten du.
 *
 * @param erantzunZuzenak Uneko erantzun zuzen kopurua
 */
@Composable
fun RequirementInfo(
    erantzunZuzenak: Int,
) {
    Text(
        text = "2 erantzun zuzen behar dira proba gainditzeko",
        // Kolorea: baldintza bete bada berdea, bestela urdina
        color = if (erantzunZuzenak >= 2) Color(0xFF4CAF50) else Color(0xFF2196F3),
        fontSize = 12.sp,
        fontWeight = if (erantzunZuzenak >= 2) FontWeight.Bold else FontWeight.Normal,
    )
}