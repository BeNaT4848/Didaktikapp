package com.example.errenteriaapp.components



import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.classes.RankingItem

import kotlinx.coroutines.delay

@Composable
fun PodiumItem(
    position: Int,
    item: RankingItem,
    height: Dp,
    color: Color,
    textColor: Color,
    pointsTextSize: TextUnit = 24.sp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bounce-animation")
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = when (position) {
            1 -> 6f
            2 -> 4f
            else -> 3f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    var showPoints by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500L + (position * 100L))
        showPoints = true
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Información del participante
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(bottom = 12.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                color,
                                color.copy(alpha = 0.8f),
                                color.copy(alpha = 0.6f)
                            ),
                            center = Offset(0.3f, 0.3f),
                            radius = 100f
                        )
                    )
                    .border(
                        width = when (position) {
                            1 -> 3.dp
                            2 -> 2.dp
                            else -> 1.5.dp
                        },
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.9f),
                                color.copy(alpha = 0.8f)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.name.split(" ").joinToString("") { it.first().toString() },
                    color = Color.Black,
                    fontSize = when (position) {
                        1 -> 22.sp
                        2 -> 20.sp
                        else -> 18.sp
                    },
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Nombre completo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.name.split(" ")[0],
                    fontWeight = FontWeight.SemiBold,
                    fontSize = when (position) {
                        1 -> 18.sp
                        2 -> 16.sp
                        else -> 15.sp
                    },
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    maxLines = 1
                )

                if (item.name.contains(" ")) {
                    Text(
                        text = item.name.split(" ")[1],
                        fontWeight = FontWeight.Medium,
                        fontSize = when (position) {
                            1 -> 16.sp
                            2 -> 14.sp
                            else -> 13.sp
                        },
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Puntos
            AnimatedVisibility(
                visible = showPoints,
                enter = scaleIn(animationSpec = spring(dampingRatio = 0.5f)) + fadeIn()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.7f),
                                    Color.Black.copy(alpha = 0.9f)
                                )
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            color = color.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "puntu",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${item.points}",
                            fontWeight = FontWeight.Black,
                            fontSize = when (position) {
                                1 -> 16.sp
                                2 -> 16.sp
                                else -> 16.sp
                            },
                            color = Color.White
                        )
                        Text(
                            text = "pts",
                            fontWeight = FontWeight.Bold,
                            fontSize = when (position) {
                                1 -> 14.sp
                                2 -> 14.sp
                                else -> 14.sp
                            },
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // Pedestal
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(height)
                .offset(y = (-bounce).dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            color,
                            color.copy(alpha = 0.95f),
                            color.copy(alpha = 0.85f),
                            color.copy(alpha = 0.7f),
                            color.copy(alpha = 0.5f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
                .border(
                    width = when (position) {
                        1 -> 2.5.dp
                        2 -> 2.dp
                        else -> 1.5.dp
                    },
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.9f),
                            Color.White.copy(alpha = 0.7f),
                            Color.White.copy(alpha = 0.4f)
                        )
                    ),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "$position°",
                    color = Color.White,
                    fontSize = when (position) {
                        1 -> 32.sp
                        2 -> 28.sp
                        else -> 24.sp
                    },
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .shadow(3.dp, shape = CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    color.copy(alpha = if (color == Color(0xFFFFC107)) 0.2f else 0.3f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )

                // Medalla decorativa
                if (position <= 3) {
                    val medalColor = when (position) {
                        1 -> "ORO"
                        2 -> "PLATA"
                        else -> "BRONCE"
                    }
                    Text(
                        text = medalColor,
                        color = Color.Black,
                        fontSize = when (position) {
                            1 -> 14.sp
                            2 -> 13.sp
                            else -> 12.sp
                        },
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}