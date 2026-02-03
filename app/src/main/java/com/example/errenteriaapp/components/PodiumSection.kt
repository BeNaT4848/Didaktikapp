package com.example.errenteriaapp.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.classes.RankingItem
import kotlinx.coroutines.delay

/**
 * Rankingaren podioaren atala erakusten du.
 * Lehenengo hiru postuen podio animatua erakusten du.
 *
 * @param isVisible Podioaren atala ikusgarri den
 * @param rankingData Rankingaren datuak (gutxienez 1 elementu)
 * @param totalItemsCount Elementu kopuru osoa (aukerakoa)
 */
@Composable
fun PodiumSection(
    isVisible: Boolean,
    rankingData: List<RankingItem>,
    totalItemsCount: Int? = null
) {
    // Podioaren elementuak banan-banan animatzeko egoerak
    var showTitle by remember { mutableStateOf(false) }
    var showFirstPlace by remember { mutableStateOf(false) }
    var showSecondPlace by remember { mutableStateOf(false) }
    var showThirdPlace by remember { mutableStateOf(false) }

    // Animazioak hasi ikusgarri badago
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
        // Podioaren izenburua
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

        // Podio animatua (gutxienez elementu bat badago)
        if (rankingData.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(280.dp)
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // Elementu kopuruaren arabera tratatu
                when (rankingData.size) {
                    1 -> {
                        // Kasu 1: Elementu bakarra - lehenengo postua zentratuta
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
                                color = Color(0xFFFFC107), // Urrea - mantentzen da
                                textColor = Color(0xFF333333),
                                pointsTextSize = 22.sp,
                                modifier = Modifier.fillMaxHeight()
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))
                    }

                    2 -> {
                        // Kasu 2: Bi elementu - bigarren eta lehenengo postuak
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
                                color = Color(0xFF9E9E9E), // Zilarra - mantentzen da
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
                                color = Color(0xFFFFC107), // Urrea - mantentzen da
                                textColor = Color(0xFF333333),
                                pointsTextSize = 22.sp,
                                modifier = Modifier.fillMaxHeight()
                            )
                        }

                        // Hirugarren posturako espazioa (hutsik)
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    else -> {
                        // Kasu 3: 3 elementu edo gehiago - lehenengo hiruak normalean
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
                                color = Color(0xFF9E9E9E), // Zilarra - mantentzen da
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
                                color = Color(0xFFFFC107), // Urrea - mantentzen da
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
                                color = Color(0xFF8D6E63), // Brontzea - mantentzen da
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