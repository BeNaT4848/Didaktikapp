package com.example.errenteriaapp.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
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
    // Estado para la animación de entrada
    var isCardVisible by remember { mutableStateOf(false) }

    LaunchedEffect(galderaIndex) {
        isCardVisible = false
        // Pequeño delay antes de mostrar la animación
        kotlinx.coroutines.delay(50)
        isCardVisible = true
    }

    AnimatedVisibility(
        visible = isCardVisible,
        enter = slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )
        ) + fadeIn(
            animationSpec = tween(500)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(300)
        ) + fadeOut(tween(300))
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
                // Etiqueta de pregunta con animación
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(),
                    exit = fadeOut() + scaleOut()
                ) {
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Texto de la pregunta con animación de aparición
                AnimatedContent(
                    targetState = galderaText,
                    transitionSpec = {
                        slideInVertically(
                            animationSpec = tween(400)
                        ) { height -> -height } + fadeIn() with
                                slideOutVertically(
                                    animationSpec = tween(400)
                                ) { height -> height } + fadeOut()
                    },
                    label = "QuestionTextAnimation"
                ) { text ->
                    Text(
                        text = text,
                        color = Color(0xFF1A2C4A),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Opciones con animaciones escalonadas
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    aukerak.forEachIndexed { index, aukera ->
                        val isSelected = aukeraHautatua == index
                        val estaErantzunda = galderakErantzunda.contains(galderaIndex)
                        val fueSeleccionada = erantzunak[galderaIndex]?.first == index
                        val esLaCorrecta = index == erantzunZuzena

                        // Animación escalonada para las opciones
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                initialOffsetY = { 50 },
                                animationSpec = tween(
                                    durationMillis = 400,
                                    delayMillis = index * 100,
                                    easing = FastOutSlowInEasing
                                )
                            ) + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 400,
                                    delayMillis = index * 100
                                )
                            ),
                            exit = fadeOut() + slideOutVertically()
                        ) {
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
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
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
    // Animación de escala cuando se selecciona
    val scale by animateFloatAsState(
        targetValue = if (isSelected && !estaErantzunda) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "OptionScaleAnimation"
    )

    // Animación de color de borde
    val borderColor by animateColorAsState(
        targetValue = when {
            estaErantzunda && esLaCorrecta -> Color(0xFF4CAF50)
            estaErantzunda && fueSeleccionada && !esLaCorrecta -> Color(0xFFF44336)
            isSelected -> Color(0xFF2196F3)
            estaErantzunda && !fueSeleccionada -> Color(0xFFBDBDBD)
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 300),
        label = "BorderColorAnimation"
    )

    // Animación de color de fondo
    val backgroundColor by animateColorAsState(
        targetValue = when {
            estaErantzunda && esLaCorrecta -> Color(0xFFE8F5E9)
            estaErantzunda && fueSeleccionada && !esLaCorrecta -> Color(0xFFFCE4EC)
            isSelected -> Color(0xFFE3F2FD)
            estaErantzunda && !fueSeleccionada -> Color(0xFFFAFAFA).copy(alpha = 0.5f)
            else -> Color(0xFFF5F5F5)
        },
        animationSpec = tween(durationMillis = 300),
        label = "BackgroundColorAnimation"
    )

    // Animación para el círculo de la opción
    val circleColor by animateColorAsState(
        targetValue = when {
            estaErantzunda && esLaCorrecta -> Color(0xFF4CAF50)
            estaErantzunda && fueSeleccionada && !esLaCorrecta -> Color(0xFFF44336)
            isSelected -> Color(0xFF2196F3)
            estaErantzunda && !fueSeleccionada -> Color(0xFFE0E0E0).copy(alpha = 0.6f)
            else -> Color(0xFFE0E0E0)
        },
        animationSpec = tween(durationMillis = 300),
        label = "CircleColorAnimation"
    )

    // Efecto de pulso para opciones correctas/incorrectas
    val pulseScale by animateFloatAsState(
        targetValue = if (estaErantzunda && (esLaCorrecta || fueSeleccionada)) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                1f at 0
                1.1f at 250
                1f at 500
                1.1f at 750
            },
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAnimation"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
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
            // Letra de la opción con efecto de pulso si es correcta/incorrecta
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(circleColor)
                    .scale(if (estaErantzunda && (esLaCorrecta || fueSeleccionada)) pulseScale else 1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${'A' + index}",
                    color = if (estaErantzunda && !fueSeleccionada && !esLaCorrecta) Color(0xFF9E9E9E) else Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Texto de la opción
            Text(
                text = text,
                color = if (estaErantzunda && !fueSeleccionada && !esLaCorrecta) Color(0xFF9E9E9E) else Color(0xFF333333),
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            // Indicador de acierto/error con animación
            if (estaErantzunda) {
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    if (esLaCorrecta) {
                        Text(
                            text = "✓",
                            color = Color(0xFF4CAF50),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.scale(pulseScale)
                        )
                    } else if (fueSeleccionada && !esLaCorrecta) {
                        Text(
                            text = "✗",
                            color = Color(0xFFF44336),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.scale(pulseScale)
                        )
                    }
                }
            }
        }
    }
}