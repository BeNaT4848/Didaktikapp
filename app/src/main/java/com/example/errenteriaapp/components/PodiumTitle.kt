package com.example.errenteriaapp.components



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.R
import com.example.errenteriaapp.ui.theme.primaryLight

@Composable
fun PodiumTitle(
    modifier: Modifier = Modifier,
    onSurfaceColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        // Línea decorativa superior
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(2.dp)
                .clip(MaterialTheme.shapes.small)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            primaryLight,
                            Color.Transparent
                        )
                    )
                )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Título con iconos
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = primaryLight,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = stringResource(R.string.ranking_podium_honor_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = onSurfaceColor,
                letterSpacing = 1.sp,
                fontSize = 24.sp
            )
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = primaryLight,
                modifier = Modifier.size(28.dp)
            )
        }

        // Línea decorativa inferior
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(2.dp)
                .clip(MaterialTheme.shapes.small)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            primaryLight,
                            Color.Transparent
                        )
                    )
                )
        )
    }
}