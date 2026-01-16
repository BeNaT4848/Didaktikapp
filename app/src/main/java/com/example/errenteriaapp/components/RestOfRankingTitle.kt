package com.example.errenteriaapp.components



import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.ui.theme.primaryLight

@Composable
fun RestOfRankingTitle(
    modifier: Modifier = Modifier,
    onSurfaceColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        // Línea decorativa superior
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(0.3f),
            color = primaryLight.copy(alpha = 0.5f),
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Título con iconos
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = primaryLight,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "RANKINGAREN GAINERAKOA",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = onSurfaceColor,
                fontSize = 18.sp
            )
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = primaryLight,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Línea decorativa inferior
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(0.3f),
            color = primaryLight.copy(alpha = 0.5f),
            thickness = 1.dp
        )
    }
}