package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
 * Sopa-letren jokoaren goiburua erakusten du.
 * Izenburua eta azpizenburua erakusten ditu.
 *
 * @param modifier Modifier gehigarria
 */
@Composable
fun SopaHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        // Izenburua
        Text(
            text = stringResource(R.string.game_sopa_title),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 9.dp, bottom = 2.dp)
        )

        // Azpizenburua
        Text(
            text = stringResource(R.string.game_sopa_subtitle),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 13.dp)
        )
    }
}