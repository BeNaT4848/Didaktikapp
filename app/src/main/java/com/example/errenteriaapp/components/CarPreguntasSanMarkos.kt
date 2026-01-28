package com.example.errenteriaapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionCard(
    galderaIndex: Int,
    galderaText: String,
    aukerak: List<String>,
    erantzunZuzena: Int,
    aukeraHautatua: Int,
    galderakErantzunda: List<Int>,
    erantzunak: Map<Int, Pair<Int, Boolean>>,
    onOptionSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Etiqueta de pregunta
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE3F2FD))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "GALDERA ${galderaIndex + 1}",
                    color = Color(0xFF2196F3),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Texto de la pregunta
            Text(
                text = galderaText,
                color = Color(0xFF1A2C4A),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Opciones
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                aukerak.forEachIndexed { index, aukera ->
                    val isSelected = aukeraHautatua == index
                    val estaErantzunda = galderakErantzunda.contains(galderaIndex)
                    val fueSeleccionada = erantzunak[galderaIndex]?.first == index
                    val esLaCorrecta = index == erantzunZuzena

                    OptionItem(
                        index = index,
                        text = aukera,
                        isSelected = isSelected,
                        estaErantzunda = estaErantzunda,
                        fueSeleccionada = fueSeleccionada,
                        esLaCorrecta = esLaCorrecta,
                        onClick = { onOptionSelected(index) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionItem(
    index: Int,
    text: String,
    isSelected: Boolean,
    estaErantzunda: Boolean,
    fueSeleccionada: Boolean,
    esLaCorrecta: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val noFueSeleccionada = estaErantzunda && !fueSeleccionada

    val backgroundColor = when {
        estaErantzunda && esLaCorrecta -> colorScheme.tertiaryContainer
        estaErantzunda && fueSeleccionada && !esLaCorrecta -> colorScheme.errorContainer
        isSelected -> colorScheme.primaryContainer
        noFueSeleccionada -> colorScheme.surfaceVariant.copy(alpha = 0.6f)
        else -> colorScheme.surfaceVariant
    }

    val borderColor = when {
        estaErantzunda && esLaCorrecta -> colorScheme.tertiary
        estaErantzunda && fueSeleccionada && !esLaCorrecta -> colorScheme.error
        isSelected -> colorScheme.primary
        noFueSeleccionada -> colorScheme.outlineVariant
        else -> Color.Transparent
    }

    val textColor = when {
        estaErantzunda && esLaCorrecta -> colorScheme.onTertiaryContainer
        estaErantzunda && fueSeleccionada && !esLaCorrecta -> colorScheme.onErrorContainer
        noFueSeleccionada -> colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        else -> colorScheme.onSurface
    }

    val circleColor = when {
        estaErantzunda && esLaCorrecta -> colorScheme.tertiary
        estaErantzunda && fueSeleccionada && !esLaCorrecta -> colorScheme.error
        isSelected -> colorScheme.primary
        noFueSeleccionada -> colorScheme.surfaceVariant.copy(alpha = 0.7f)
        else -> colorScheme.outlineVariant
    }

    val circleTextColor = when {
        estaErantzunda && esLaCorrecta -> colorScheme.onTertiary
        estaErantzunda && fueSeleccionada && !esLaCorrecta -> colorScheme.onError
        isSelected -> colorScheme.onPrimary
        noFueSeleccionada -> colorScheme.onSurfaceVariant
        else -> colorScheme.onSurface
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(
            width = if (borderColor != Color.Transparent) 2.dp else 0.dp,
            color = borderColor
        ),
        enabled = !estaErantzunda
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Letra de la opción (A, B, C)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(circleColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${'A' + index}",
                    color = circleTextColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Texto de la opción
            Text(
                text = text,
                color = textColor,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            // Indicador de acierto/error
            if (estaErantzunda) {
                if (esLaCorrecta) {
                    // Mostrar ✓ verde si es la respuesta correcta
                    Text(
                        text = "✓",
                        color = colorScheme.tertiary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else if (fueSeleccionada && !esLaCorrecta) {
                    // Mostrar ✗ rojo si seleccionó esta pero es incorrecta
                    Text(
                        text = "✗",
                        color = colorScheme.error,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}