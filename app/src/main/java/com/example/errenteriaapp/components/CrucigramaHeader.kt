package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.R

/**
 * Gurutze-hitzaren pantailarako goiburua erakusten du.
 * Izenburua eta azpizenburua erakusten ditu.
 */
@Composable
fun CrucigramaHeader() {
    // Izenburua
    Text(
        text = stringResource(R.string.game_crucigrama_title),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2196F3),
        modifier = Modifier.padding(bottom = 4.dp, top = 10.dp)
    )

    // Azpizenburua
    Text(
        text = stringResource(R.string.game_crucigrama_subtitle),
        fontSize = 12.sp,
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 20.dp)
    )
}