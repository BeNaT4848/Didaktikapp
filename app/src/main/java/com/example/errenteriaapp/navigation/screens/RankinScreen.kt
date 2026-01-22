package com.example.errenteriaapp.screens.ranking

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.errenteriaapp.components.*
import com.example.errenteriaapp.classes.RankingItem
import com.example.errenteriaapp.ui.theme.onSurfaceLight
import kotlinx.coroutines.delay

@Composable
fun RankinScreen(
    navController: NavController
) {
    // Estados para controlar animaciones progresivas
    var isScreenLoaded by remember { mutableStateOf(false) }
    val rankingData = remember { RankingDataProvider.getRankingData() }

    // Animar entrada progresiva
    LaunchedEffect(Unit) {
        delay(100) // Muy corto para que cargue rápido
        isScreenLoaded = true
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = isScreenLoaded,
                enter = fadeIn(animationSpec = tween(200)) +
                        slideInVertically(
                            animationSpec = tween(250, easing = FastOutSlowInEasing),
                            initialOffsetY = { -30 }
                        )
            ) {
                RankingTopBar(
                    showContent = true,
                    navController = navController
                )
            }
        }
    ) { paddingValues ->
        RankingContent(
            isScreenLoaded = isScreenLoaded,
            rankingData = rankingData,
            paddingValues = paddingValues
        )
    }
}

@Composable
private fun RankingContent(
    isScreenLoaded: Boolean,
    rankingData: List<RankingItem>,
    paddingValues: PaddingValues
) {
    val onSurfaceColor = onSurfaceLight

    // Fondo inmediato
    RankingBackground(
        modifier = Modifier.padding(paddingValues)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // PODIO - Aparece primero
            PodiumSection(
                isVisible = isScreenLoaded,
                rankingData = rankingData,
                onSurfaceColor = onSurfaceColor
            )

            // RESTO DEL RANKING - Aparece después
            RestOfRankingSection(
                isScreenLoaded = isScreenLoaded,
                rankingData = rankingData,
                onSurfaceColor = onSurfaceColor
            )
        }
    }
}



