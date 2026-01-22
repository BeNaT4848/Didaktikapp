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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
    onSurfaceColor: Color
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
            visible = showTitle,
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
                modifier = Modifier.fillMaxWidth(),
                onSurfaceColor = onSurfaceColor
            )
        }

        // Podio con animaciones individuales más rápidas
        Row(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(280.dp)
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            // SEGUNDO lugar (izquierda)
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
                    color = Color(0xFF9E9E9E),
                    textColor = onSurfaceColor,
                    pointsTextSize = 20.sp,
                    modifier = Modifier.fillMaxHeight()
                )
            }

            // PRIMER lugar (centro) - Aparece primero y más grande
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
                    color = Color(0xFFFFC107),
                    textColor = Color(0xFF333333),
                    pointsTextSize = 22.sp,
                    modifier = Modifier.fillMaxHeight()
                )
            }

            // TERCER lugar (derecha)
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
                    color = Color(0xFF8D6E63),
                    textColor = Color(0xFFFFFFFF),
                    pointsTextSize = 18.sp,
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}