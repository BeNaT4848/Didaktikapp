package com.example.errenteriaapp.screens.ranking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.errenteriaapp.components.*
import com.example.errenteriaapp.classes.RankingItem
import com.example.errenteriaapp.ui.theme.onSurfaceLight
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RankinScreen(
    navController: NavController
) {
    var showContent by remember { mutableStateOf(false) }
    val rankingData = remember { RankingDataProvider.getRankingData() }

    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
    }

    Scaffold(
        topBar = {
            RankingTopBar(
                showContent = showContent,
                navController = navController
            )
        }
    ) { paddingValues ->
        RankingContent(
            showContent = showContent,
            rankingData = rankingData,
            paddingValues = paddingValues
        )
    }
}

@Composable
private fun RankingContent(
    showContent: Boolean,
    rankingData: List<RankingItem>,
    paddingValues: PaddingValues
) {
    val onSurfaceColor = onSurfaceLight

    RankingBackground(
        modifier = Modifier.padding(paddingValues)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // PRIMERA PARTE: PODIO FIJO (no hace scroll)
            PodiumSection(
                showContent = showContent,
                rankingData = rankingData,
                onSurfaceColor = onSurfaceColor
            )

            // SEGUNDA PARTE: RESTO DEL RANKING (scroll independiente)
            RestOfRankingSection(
                showContent = showContent,
                rankingData = rankingData,
                onSurfaceColor = onSurfaceColor
            )
        }
    }
}

@Composable
private fun PodiumSection(
    showContent: Boolean,
    rankingData: List<RankingItem>,
    onSurfaceColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título del podio con padding horizontal
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) +
                    slideInVertically(animationSpec = tween(600, delayMillis = 200)) { it / 2 },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            PodiumTitle(
                modifier = Modifier.fillMaxWidth(),
                onSurfaceColor = onSurfaceColor
            )
        }

        // Podio con los tres primeros
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(500, delayMillis = 300)) +
                    scaleIn(animationSpec = tween(500, delayMillis = 300)),
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(280.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // SEGUNDO lugar (izquierda)
                PodiumItem(
                    position = 2,
                    item = rankingData[1],
                    height = 160.dp,
                    color = Color(0xFF9E9E9E),
                    textColor = onSurfaceColor,
                    pointsTextSize = 20.sp, // TAMAÑO REDUCIDO de puntos
                    modifier = Modifier.weight(1f)
                )

                // PRIMER lugar (centro)
                PodiumItem(
                    position = 1,
                    item = rankingData[0],
                    height = 200.dp,
                    color = Color(0xFFFFC107),
                    textColor = Color(0xFF333333),
                    pointsTextSize = 22.sp, // TAMAÑO REDUCIDO de puntos
                    modifier = Modifier.weight(1f)
                )

                // TERCER lugar (derecha)
                PodiumItem(
                    position = 3,
                    item = rankingData[2],
                    height = 120.dp,
                    color = Color(0xFF8D6E63),
                    textColor = Color(0xFFFFFFFF),
                    pointsTextSize = 18.sp, // TAMAÑO REDUCIDO de puntos
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun RestOfRankingSection(
    showContent: Boolean,
    rankingData: List<RankingItem>,
    onSurfaceColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 8.dp)
    ) {

        // Título del resto del ranking (también fijo)
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(500, delayMillis = 600)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            RestOfRankingTitle(
                modifier = Modifier.fillMaxWidth(),
                onSurfaceColor = onSurfaceColor
            )
        }

        // Lista del resto del ranking CON SCROLL INDEPENDIENTE
        // Usamos weight para que ocupe el espacio restante
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Lista de participantes (posiciones 4-10)
            itemsIndexed(
                items = rankingData.drop(3),
                key = { _, item -> item.name }
            ) { index, item ->
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(
                        animationSpec = tween(300, delayMillis = 700 + index * 70)
                    ) + slideInVertically(
                        animationSpec = tween(400, delayMillis = 700 + index * 70),
                        initialOffsetY = { 30 }
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

            // Espacio final para scroll
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}