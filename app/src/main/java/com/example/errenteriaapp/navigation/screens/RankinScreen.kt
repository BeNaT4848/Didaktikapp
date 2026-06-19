package com.example.errenteriaapp.screens.ranking

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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

/**
 * Ranking pantaila nagusia.
 * Erabiltzaileen puntuazioak erakusten ditu podio formatuan.
 *
 * @param navController Nabigazio kontroladorea
 * @param viewModel Ranking ViewModel (bertsio lehenetsia erabiltzen da)
 *
 * @see RankingViewModel
 * @see RankingTopBar
 * @see PodiumSection
 * @see RestOfRankingSection
 */
@Composable
fun RankinScreen(
    navController: NavController,
    viewModel: RankingViewModel = viewModel()
) {
    // Pantaila kargatu deneko animazio egoera
    var isScreenLoaded by remember { mutableStateOf(false) }

    // ViewModel-eko DATU ERREALAK lortu
    val rankingData by viewModel.rankingData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val primaryColor = MaterialTheme.colorScheme.primary

    // DATU ERREALAK RankingItem formatura bihurtu
    val rankingItems = remember(rankingData, primaryColor) {
        rankingData.mapIndexed { index, puntuazio ->
            val totalPoints = viewModel.calculateTotalPoints(puntuazio)

            // Lehenengo 3 postuentzako, podio koloreak mantendu
            val itemColor = when (index) {
                0 -> Color(0xFFFFC107) // Urrea - mantenduta
                1 -> Color(0xFF9E9E9E) // Zilarra - mantenduta
                2 -> Color(0xFF8D6E63) // Brontzea - mantenduta
                else -> primaryColor // Gaiaren kolore primarioa erabili
            }

            RankingItem(
                name = puntuazio.izenaAbizena,
                points = totalPoints,
                color = itemColor
            )
        }
    }

    // Pantaila hasieratzean datuak kargatu
    LaunchedEffect(Unit) {
        // DATU ERREALAK kargatu abian jarrita
        viewModel.loadRanking()
        delay(100)
        isScreenLoaded = true
    }

    // Alboko sagu-arrastea detektatzeko
    val edgeDp = 48.dp
    val edgePx = with(LocalDensity.current) { edgeDp.toPx() }
    var dragStartX by remember { mutableStateOf<Float?>(null) }

    // Pantaila egitura nagusia
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
                        // Ezkerreko ertzetik arrastea detektatu
                        val startedOnEdge = (dragStartX ?: Float.MAX_VALUE) <= edgePx
                        if (startedOnEdge && dragAmount > 40f) {
                            navController.navigate(Routes.GPS_SCREEN)
                            change.consume()
                        }
                    }
                }
        ) {
            if (isLoading) {
                // Datuak kargatzen ari diren bitartean kargaketa-indikatzailea erakutsi
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                RankingContent(
                    isScreenLoaded = isScreenLoaded,
                    rankingItems = rankingItems, // DATU ERREALAK bihurtuak erabiltzen dira
                    paddingValues = paddingValues
                )
            }
        }
    }
}

/**
 * Ranking edukiaren konposaketa.
 *
 * @param isScreenLoaded Pantaila kargatu den ala ez
 * @param rankingItems Ranking elementuen zerrenda
 * @param paddingValues Paddings balioak
 */
@Composable
private fun RankingContent(
    isScreenLoaded: Boolean,
    rankingItems: List<RankingItem>,
    paddingValues: PaddingValues
) {
    // Atzeko plano berehala
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // PODIO atala (lehenengo 3ak)
            val podiumItems = rankingItems.take(3)
            if (podiumItems.isNotEmpty()) {
                PodiumSection(
                    isVisible = isScreenLoaded,
                    rankingData = podiumItems,
                    totalItemsCount = rankingItems.size
                )
            }

            // GAINERAKO RANKING - Ondoren agertzen da
            val remainingItems = rankingItems.drop(3)
            if (remainingItems.isNotEmpty()) {
                RestOfRankingSection(
                    isScreenLoaded = isScreenLoaded,
                    rankingData = remainingItems
                )
            }
        }
    }
}