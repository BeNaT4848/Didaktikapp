package com.example.errenteriaapp.screens.ranking

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.errenteriaapp.components.*
import com.example.errenteriaapp.classes.RankingItem
import com.example.errenteriaapp.database.viewModel.RankingViewModel
import com.example.errenteriaapp.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun RankinScreen(
    navController: NavController,
    viewModel: RankingViewModel = viewModel()
) {
    // Estados para controlar animaciones progresivas
    var isScreenLoaded by remember { mutableStateOf(false) }

    // Obtener datos REALES del ViewModel
    val rankingData by viewModel.rankingData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Convertir datos REALES de la base de datos a RankingItem
    val rankingItems = remember(rankingData) {
        rankingData.mapIndexed { index, puntuazio ->
            val totalPoints = viewModel.calculateTotalPoints(puntuazio)
            RankingItem(
                name = puntuazio.izenaAbizena, // Esto ahora debería funcionar
                points = totalPoints,
                color = Color.Red,
            )
        }
    }

    LaunchedEffect(Unit) {
        // Cargar datos REALES al iniciar
        viewModel.loadRanking()
        delay(100)
        isScreenLoaded = true
    }

    val edgeDp = 48.dp
    val edgePx = with(LocalDensity.current) { edgeDp.toPx() }
    var dragStartX by remember { mutableStateOf<Float?>(null) }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = isScreenLoaded && !isLoading,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { offset -> dragStartX = offset.x },
                        onDragEnd = { dragStartX = null },
                        onDragCancel = { dragStartX = null }
                    ) { change, dragAmount ->
                        val startedOnEdge = (dragStartX ?: Float.MAX_VALUE) <= edgePx
                        if (startedOnEdge && dragAmount > 40f) {
                            navController.navigate(Routes.GPS_SCREEN)
                            change.consume()
                        }
                    }
                }
        ) {
            if (isLoading) {
                // Mostrar indicador de carga mientras se cargan los datos
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                RankingContent(
                    isScreenLoaded = isScreenLoaded,
                    rankingItems = rankingItems, // Usamos los datos REALES convertidos
                    paddingValues = paddingValues
                )
            }
        }
    }
}

@Composable
private fun RankingContent(
    isScreenLoaded: Boolean,
    rankingItems: List<RankingItem>, // Solo necesitamos esta lista
    paddingValues: PaddingValues
) {
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    // Fondo inmediato
    RankingBackground(
        modifier = Modifier.padding(paddingValues)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            val podiumItems = rankingItems.take(3)
            if (podiumItems.isNotEmpty()) {

                PodiumSection(
                    isVisible = isScreenLoaded,
                    rankingData = podiumItems,
                    onSurfaceColor = onSurfaceColor,
                    totalItemsCount = rankingItems.size
                )
            }

            // RESTO DEL RANKING - Aparece después
            val remainingItems = rankingItems.drop(3)
            if (remainingItems.isNotEmpty()) {
                RestOfRankingSection(
                    isScreenLoaded = isScreenLoaded,
                    rankingData = remainingItems,
                    onSurfaceColor = onSurfaceColor
                )
            }
        }
    }
}
