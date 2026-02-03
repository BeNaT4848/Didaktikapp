package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.R

/**
 * Sopa-letren aurrerapen-barra erakusten du.
 * Topatutako hitzen kopurua erakusten du.
 *
 * @param encontradas Topatutako hitz kopurua
 * @param total Hitz kopuru osoa
 * @param modifier Modifier gehigarria
 */
@Composable
fun SopaProgressBar(
    encontradas: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                // "Aurkitutako hitzak:" testua
                Text(
                    text = stringResource(R.string.game_sopa_progress),
                    color = Color.Black,
                    fontSize = 12.sp
                )
                // Kopurua (adibidez, "3/8")
                Text(
                    text = "$encontradas/$total",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}