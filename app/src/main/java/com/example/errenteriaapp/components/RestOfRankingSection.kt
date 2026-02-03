package com.example.errenteriaapp.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.errenteriaapp.classes.RankingItem
import kotlinx.coroutines.delay

/**
 * Rankingaren gainerako atala erakusten du (4. postutik aurrera).
 * Elementuak animazio eskalonatuetan erakusten ditu.
 *
 * @param isScreenLoaded Pantaila kargatuta dagoen
 * @param rankingData Rankingaren datuak (4. postutik aurrera)
 */
@Composable
fun RestOfRankingSection(
    isScreenLoaded: Boolean,
    rankingData: List<RankingItem>
) {
    var showTitle by remember { mutableStateOf(false) }
    val visibleItems = remember { mutableStateListOf<Boolean>() }

    // Ikusgarritasunaren zerrenda hasieratu
    LaunchedEffect(rankingData) {
        visibleItems.clear()
        visibleItems.addAll(List(rankingData.size) { false })
    }

    // Animazio eskalonatuak kontrolatu
    LaunchedEffect(isScreenLoaded) {
        if (isScreenLoaded) {
            delay(200)
            showTitle = true
            delay(100)

            // Elementuak banan-banan animatu
            rankingData.forEachIndexed { index, _ ->
                delay(25)
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
        // Rankingaren gainerako izenburua
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
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Optimizatutako zerrenda (animateItemPlacement gabe)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            itemsIndexed(
                items = rankingData,
                key = { _, item -> item.name }
            ) { index, item ->
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
                        position = index + 4, // 4. postutik hasten da
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