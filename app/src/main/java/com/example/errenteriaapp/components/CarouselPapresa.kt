package com.example.errenteriaapp.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.classes.WasteCategory
import com.example.errenteriaapp.classes.WasteItem


import androidx.compose.animation.slideInVertically

import androidx.compose.animation.AnimatedContent

import androidx.compose.animation.core.Spring
import androidx.compose.animation.expandHorizontally

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn

import androidx.compose.ui.draw.rotate
import androidx.compose.animation.AnimatedContentTransitionScope
import kotlinx.coroutines.delay
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PhotoCarousel(
    wasteItems: List<WasteItem>,
    currentIndex: Int,
    userAnswers: Map<Int, WasteCategory>,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var previousIndex by remember { mutableStateOf(currentIndex) }
    var direction by remember { mutableStateOf(0) } // -1: izquierda, 1: derecha, 0: inicial

    // Detectar dirección del cambio
    LaunchedEffect(currentIndex) {
        direction = if (currentIndex > previousIndex) 1 else -1
        previousIndex = currentIndex
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp),
        contentAlignment = Alignment.Center
    ) {
        if (wasteItems.isNotEmpty() && currentIndex < wasteItems.size) {
            val currentItem = wasteItems[currentIndex]

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Flecha izquierda con animación de pulso
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val pulseScale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulseScale"
                    )

                    IconButton(
                        onClick = onPreviousClick,
                        modifier = Modifier.scale(pulseScale)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Aurrekoa",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Contenedor para la imagen con animación de entrada/salida
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                    ) {
                        // Animación para cambio de imagen
                        AnimatedContent(
                            targetState = currentItem,
                            transitionSpec = {
                                // Animación de deslizamiento
                                slideIntoContainer(
                                    towards = if (direction == 1) AnimatedContentTransitionScope.SlideDirection.Left
                                    else AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(300)
                                ) with slideOutOfContainer(
                                    towards = if (direction == 1) AnimatedContentTransitionScope.SlideDirection.Right
                                    else AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(300)
                                )
                            },
                            label = "imageTransition"
                        ) { targetItem ->
                            // Animación de escala al cargar la imagen
                            var imageLoaded by remember { mutableStateOf(false) }

                            LaunchedEffect(targetItem) {
                                imageLoaded = false
                                // Simula una pequeña carga para la animación
                                delay(50)
                                imageLoaded = true
                            }

                            val scale by animateFloatAsState(
                                targetValue = if (imageLoaded) 1f else 0.8f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "scaleAnimation"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(
                                        2.dp,
                                        Color.LightGray,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .scale(scale)
                            ) {
                                Image(
                                    painter = painterResource(id = targetItem.imageResId),
                                    contentDescription = targetItem.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Indicador de respuesta con animación
                                userAnswers[targetItem.id]?.let { category ->
                                    var showCheckmark by remember { mutableStateOf(false) }

                                    LaunchedEffect(category) {
                                        showCheckmark = false
                                        delay(200) // Pequeño retraso
                                        showCheckmark = true
                                    }

                                    val checkmarkScale by animateFloatAsState(
                                        targetValue = if (showCheckmark) 1f else 0.5f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        ),
                                        label = "checkmarkScale"
                                    )

                                    val rotation by animateFloatAsState(
                                        targetValue = if (showCheckmark) 360f else 0f,
                                        animationSpec = tween(500, easing = FastOutSlowInEasing),
                                        label = "checkmarkRotation"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                            .size(24.dp)
                                            .background(category.color, CircleShape)
                                            .border(2.dp, Color.White, CircleShape)
                                            .scale(checkmarkScale)
                                    ) {
                                        this@Row.AnimatedVisibility(
                                            visible = showCheckmark,
                                            enter = fadeIn(animationSpec = tween(300)) + scaleIn(),
                                            exit = fadeOut()
                                        ) {
                                            Text(
                                                text = "✓",
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                modifier = Modifier
                                                    .align(Alignment.Center)
                                                    .rotate(rotation)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Flecha derecha con animación de pulso
                    IconButton(
                        onClick = onNextClick,
                        modifier = Modifier.scale(pulseScale)
                    ) {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Hurrengoa",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Animación para el nombre del objeto
                Spacer(modifier = Modifier.height(24.dp))

                var cardVisible by remember { mutableStateOf(false) }

                LaunchedEffect(currentItem) {
                    cardVisible = false
                    delay(100) // Pequeño retraso para sincronizar con imagen
                    cardVisible = true
                }

                AnimatedVisibility(
                    visible = cardVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) + fadeIn(),
                    exit = fadeOut()
                ) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .animateEnterExit(
                                enter = expandHorizontally(
                                    animationSpec = tween(400)
                                )
                            )
                    ) {
                        var textScale by remember { mutableStateOf(1f) }

                        LaunchedEffect(currentItem) {
                            textScale = 1.1f
                            delay(150)
                            textScale = 1f
                        }

                        val animatedScale by animateFloatAsState(
                            targetValue = textScale,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "textScale"
                        )

                        Text(
                            text = currentItem.name,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .scale(animatedScale),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

