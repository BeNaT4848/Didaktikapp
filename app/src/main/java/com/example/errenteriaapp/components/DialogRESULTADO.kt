package com.example.errenteriaapp.components



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.R
import com.example.errenteriaapp.classes.WasteCategory
import com.example.errenteriaapp.classes.WasteItem

import kotlin.math.roundToInt

@Composable
fun ResultsDialog(
    wasteItems: List<WasteItem>,
    userAnswers: Map<Int, WasteCategory>,
    onDismiss: () -> Unit,
    onNext: () -> Unit
) {
    // Calcular puntuación
    val correctAnswers = wasteItems.count { item ->
        userAnswers[item.id] == item.correctCategory
    }
    val total = wasteItems.size
    val score = (correctAnswers.toFloat() / total * 100).roundToInt()
    val resultImage = when {
        score == 100 -> R.drawable.ondo_egina
        score >= 80 -> R.drawable.parte1
        score >= 60 -> R.drawable.parte2
        score >= 40 -> R.drawable.parte3
        else -> R.drawable.saiatu_berriro
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(onClick = onDismiss, enabled = false),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Imagen de resultado
                Image(
                    painter = painterResource(id = resultImage),
                    contentDescription = "Emaitza",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = when {
                        score == 100 -> "ONDO EGINA! 🎉"
                        score >= 80 -> "OSO ONDO! 👍"
                        score >= 60 -> "ONDO 😊"
                        score >= 40 -> "FALTAN ZENBAIT 💪"
                        else -> "SAIATU BERRIRO! 💪"
                    },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        score == 100 -> Color(0xFFFF9800)
                        score >= 80 -> Color(0xFF4CAF50)
                        score >= 60 -> Color(0xFF2196F3)
                        score >= 40 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Puntuación
                Text(
                    text = "$score%",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(
                    text = "$correctAnswers/$total zuzen",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Berrikusi")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = onNext,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Hurrengoa")
                    }
                }
            }
        }
    }
}