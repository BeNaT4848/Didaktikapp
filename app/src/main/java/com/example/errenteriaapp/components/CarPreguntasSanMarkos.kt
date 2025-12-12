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
    // Determinar si esta opción no fue seleccionada pero ya se respondió
    val noFueSeleccionada = estaErantzunda && !fueSeleccionada

    val backgroundColor = when {
        estaErantzunda && esLaCorrecta -> Color(0xFFE8F5E9)      // Verde claro para correcta
        estaErantzunda && fueSeleccionada && !esLaCorrecta -> Color(0xFFFCE4EC)  // Rojo claro para incorrecta
        isSelected -> Color(0xFFE3F2FD)                           // Azul claro para seleccionada
        noFueSeleccionada -> Color(0xFFFAFAFA).copy(alpha = 0.5f) // Gris transparente para no seleccionadas
        else -> Color(0xFFF5F5F5)                                 // Gris normal para no seleccionada
    }

    val borderColor = when {
        estaErantzunda && esLaCorrecta -> Color(0xFF4CAF50)      // Verde para correcta
        estaErantzunda && fueSeleccionada && !esLaCorrecta -> Color(0xFFF44336)  // Rojo para incorrecta
        isSelected -> Color(0xFF2196F3)                           // Azul para seleccionada
        noFueSeleccionada -> Color(0xFFBDBDBD)                    // Gris para opciones no seleccionadas
        else -> Color.Transparent
    }

    val textColor = when {
        noFueSeleccionada -> Color(0xFF9E9E9E) // Gris para opciones no seleccionadas
        else -> Color(0xFF333333)              // Negro normal
    }

    val circleColor = when {
        estaErantzunda && esLaCorrecta -> Color(0xFF4CAF50)      // Verde para correcta
        estaErantzunda && fueSeleccionada && !esLaCorrecta -> Color(0xFFF44336)  // Rojo para incorrecta
        isSelected -> Color(0xFF2196F3)                           // Azul para seleccionada
        noFueSeleccionada -> Color(0xFFE0E0E0).copy(alpha = 0.6f) // Gris transparente para no seleccionadas
        else -> Color(0xFFE0E0E0)                                 // Gris normal
    }

    val circleTextColor = when {
        noFueSeleccionada -> Color(0xFF9E9E9E) // Gris para texto de círculo
        else -> Color.White                     // Blanco normal
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(
            width = if (borderColor != Color.Transparent) 2.dp else 0.dp,
            color = borderColor
        ),
        enabled = !estaErantzunda // Deshabilitar si ya se respondió
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
                        color = Color(0xFF4CAF50),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else if (fueSeleccionada && !esLaCorrecta) {
                    // Mostrar ✗ rojo si seleccionó esta pero es incorrecta
                    Text(
                        text = "✗",
                        color = Color(0xFFF44336),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}