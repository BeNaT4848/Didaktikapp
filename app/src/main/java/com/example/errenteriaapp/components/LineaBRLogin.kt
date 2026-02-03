package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Login pantailarako banatzailea erakusten du.
 * "ERRENTERIA" testua du erdian, banatzaileekin bi aldeetan.
 *
 * @param verticalPadding Banatzailearen goiko eta beheko zabalera
 */
@Composable
fun LoginDivider(verticalPadding: Int = 12) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ezkerreko banatzailea
        Divider(
            modifier = Modifier.weight(1f),
            color = Color.White.copy(alpha = 0.3f),
            thickness = 0.5.dp
        )
        // "ERRENTERIA" testua
        Text(
            text = "ERRENTERIA",
            color = Color.White,
            fontSize = 14.sp, // Testu txikiagoa
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        // Eskuineko banatzailea
        Divider(
            modifier = Modifier.weight(1f),
            color = Color.White.copy(alpha = 0.3f),
            thickness = 0.5.dp
        )
    }
}