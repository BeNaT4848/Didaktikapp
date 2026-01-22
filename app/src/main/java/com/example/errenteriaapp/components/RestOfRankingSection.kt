package com.example.errenteriaapp.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.errenteriaapp.classes.RankingItem
import kotlinx.coroutines.delay


@Composable
fun RestOfRankingSection(
    isScreenLoaded: Boolean,
    rankingData: List<RankingItem>,
    onSurfaceColor: Color
) {
    val restItems = remember { rankingData.drop(3) }
    var showTitle by remember { mutableStateOf(false) }
    val visibleItems = remember { mutableStateListOf<Boolean>() }

    // Inicializar estados de visibilidad
    LaunchedEffect(Unit) {
        visibleItems.addAll(List(restItems.size) { false })
    }

    // Controlar animaciones escalonadas
    LaunchedEffect(isScreenLoaded) {
        if (isScreenLoaded) {
            delay(200) // Pequeño delay después del podio
            showTitle = true
            delay(100)

            // Animar items uno por uno muy rápido
            restItems.forEachIndexed { index, _ ->
                delay(25) // MUY RÁPIDO - solo 25ms entre items
                if (index < visibleItems.size) {
                    visibleItems[index] = true
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 8.dp)
    ) {
        // Título del resto del ranking
        AnimatedVisibility(
            visible = showTitle,
            enter = fadeIn(animationSpec = tween(200)) +
                    slideInVertically(
                        animationSpec = tween(250, easing = FastOutSlowInEasing),
                        initialOffsetY = { 15 }
                    ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            RestOfRankingTitle(
                modifier = Modifier.fillMaxWidth(),
                onSurfaceColor = onSurfaceColor
            )
        }

        // Lista optimizada sin animateItemPlacement
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            itemsIndexed(
                items = restItems,
                key = { _, item -> item.name }
            ) { index, item ->
                // Animación individual optimizada
                AnimatedVisibility(
                    visible = index < visibleItems.size && visibleItems[index],
                    enter = fadeIn(
                        animationSpec = tween(180, easing = LinearEasing)
                    ) + slideInVertically(
                        animationSpec = tween(220, easing = FastOutSlowInEasing),
                        initialOffsetY = { 25 }
                    )
                ) {
                    RankingCard(
                        position = index + 4,
                        item = item,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .fillMaxWidth()
                            .height(70.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}