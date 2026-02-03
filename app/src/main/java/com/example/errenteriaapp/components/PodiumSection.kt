package com.example.errenteriaapp.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.classes.RankingItem
import kotlinx.coroutines.delay


@Composable
fun PodiumSection(
    isVisible: Boolean,
    rankingData: List<RankingItem>,
    totalItemsCount: Int? = null
) {
    // Estados para animar cada elemento del podio individualmente
    var showTitle by remember { mutableStateOf(false) }
    var showFirstPlace by remember { mutableStateOf(false) }
    var showSecondPlace by remember { mutableStateOf(false) }
    var showThirdPlace by remember { mutableStateOf(false) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            showTitle = true
            delay(50)
            showFirstPlace = true
            delay(40)
            showSecondPlace = true
            delay(40)
            showThirdPlace = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título del podio
        AnimatedVisibility(
            visible = showTitle && rankingData.isNotEmpty(),
            enter = fadeIn(animationSpec = tween(200)) +
                    slideInVertically(
                        animationSpec = tween(250, easing = FastOutSlowInEasing),
                        initialOffsetY = { -20 }
                    ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            PodiumTitle(
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Podio con animaciones individuales más rápidas
        // Solo mostramos el podio si hay al menos un elemento
        if (rankingData.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(280.dp)
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // Manejar diferentes casos basados en el número de elementos
                when (rankingData.size) {
                    1 -> {
                        // Caso 1: Solo hay un elemento - mostrar solo el primer lugar centrado
                        Spacer(modifier = Modifier.weight(1f))

                        AnimatedVisibility(
                            visible = showFirstPlace,
                            enter = fadeIn(animationSpec = tween(200)) +
                                    scaleIn(
                                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                                        initialScale = 0.85f
                                    ),
                            modifier = Modifier.weight(2f)
                        ) {
                            PodiumItem(
                                position = 1,
                                item = rankingData[0],
                                height = 200.dp,
                                color = Color(0xFFFFC107), // Oro - Mantenido
                                textColor = Color(0xFF333333),
                                pointsTextSize = 22.sp,
                                modifier = Modifier.fillMaxHeight()
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))
                    }

                    2 -> {
                        // Caso 2: Hay dos elementos - mostrar segundo y primer lugar
                        AnimatedVisibility(
                            visible = showSecondPlace,
                            enter = fadeIn(animationSpec = tween(200, delayMillis = 20)) +
                                    scaleIn(
                                        animationSpec = tween(250, easing = FastOutSlowInEasing),
                                        initialScale = 0.9f
                                    ),
                            modifier = Modifier.weight(1f)
                        ) {
                            PodiumItem(
                                position = 2,
                                item = rankingData[1],
                                height = 160.dp,
                                color = Color(0xFF9E9E9E), // Plata - Mantenido
                                textColor = MaterialTheme.colorScheme.onSurface,
                                pointsTextSize = 20.sp,
                                modifier = Modifier.fillMaxHeight()
                            )
                        }

                        AnimatedVisibility(
                            visible = showFirstPlace,
                            enter = fadeIn(animationSpec = tween(200)) +
                                    scaleIn(
                                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                                        initialScale = 0.85f
                                    ),
                            modifier = Modifier.weight(1f)
                        ) {
                            PodiumItem(
                                position = 1,
                                item = rankingData[0],
                                height = 200.dp,
                                color = Color(0xFFFFC107), // Oro - Mantenido
                                textColor = Color(0xFF333333),
                                pointsTextSize = 22.sp,
                                modifier = Modifier.fillMaxHeight()
                            )
                        }

                        // Espacio para la tercera posición (vacía)
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    else -> {
                        // Caso 3: Hay 3 o más elementos - mostrar los tres primeros normalmente
                        AnimatedVisibility(
                            visible = showSecondPlace,
                            enter = fadeIn(animationSpec = tween(200, delayMillis = 20)) +
                                    scaleIn(
                                        animationSpec = tween(250, easing = FastOutSlowInEasing),
                                        initialScale = 0.9f
                                    ),
                            modifier = Modifier.weight(1f)
                        ) {
                            PodiumItem(
                                position = 2,
                                item = rankingData[1],
                                height = 160.dp,
                                color = Color(0xFF9E9E9E), // Plata - Mantenido
                                textColor = MaterialTheme.colorScheme.onSurface,
                                pointsTextSize = 20.sp,
                                modifier = Modifier.fillMaxHeight()
                            )
                        }

                        AnimatedVisibility(
                            visible = showFirstPlace,
                            enter = fadeIn(animationSpec = tween(200)) +
                                    scaleIn(
                                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                                        initialScale = 0.85f
                                    ),
                            modifier = Modifier.weight(1f)
                        ) {
                            PodiumItem(
                                position = 1,
                                item = rankingData[0],
                                height = 200.dp,
                                color = Color(0xFFFFC107), // Oro - Mantenido
                                textColor = Color(0xFF333333),
                                pointsTextSize = 22.sp,
                                modifier = Modifier.fillMaxHeight()
                            )
                        }

                        AnimatedVisibility(
                            visible = showThirdPlace,
                            enter = fadeIn(animationSpec = tween(200, delayMillis = 20)) +
                                    scaleIn(
                                        animationSpec = tween(250, easing = FastOutSlowInEasing),
                                        initialScale = 0.9f
                                    ),
                            modifier = Modifier.weight(1f)
                        ) {
                            PodiumItem(
                                position = 3,
                                item = rankingData[2],
                                height = 120.dp,
                                color = Color(0xFF8D6E63), // Bronce - Mantenido
                                textColor = MaterialTheme.colorScheme.onPrimary,
                                pointsTextSize = 18.sp,
                                modifier = Modifier.fillMaxHeight()
                            )
                        }
                    }
                }
            }
        }
    }
}