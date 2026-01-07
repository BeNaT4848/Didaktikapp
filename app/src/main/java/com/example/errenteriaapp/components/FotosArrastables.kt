package com.example.errenteriaapp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// COMPONENTEA: Irudi bat erakusten du, arrastatu daitekeena eta klik egin daitekeena handitzeko.
// Automatikoki doitzen da pantailaren tamainaren arabera.

@Composable
fun DraggablePhoto(
    modifier: Modifier = Modifier,
    photoRes: Int,
    photoNumber: Int?,
    isUsed: Boolean = false,
    isInGrid: Boolean = true, // Grid batean dagoen ala ez
    gridColumns: Int = 2, // Grid-aren zutabe kopurua
    spacing: Dp = 8.dp, // Elementuen arteko tartea
    onPhotoPositioned: (Rect) -> Unit,
    onEnlargeClick: () -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    // Pantailaren konfigurazioa lortu
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    // Kalkulatu tamaina automatikoak
    val screenWidth = with(density) { configuration.screenWidthDp.dp }
    val screenHeight = with(density) { configuration.screenHeightDp.dp }

    // Zehaztu ea pantaila horizontala den
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    // Kalkulatu irudiaren tamaina
    var imageModifier by remember { mutableStateOf(modifier) }

    LaunchedEffect(screenWidth, screenHeight, isLandscape, isInGrid, gridColumns, spacing) {
        imageModifier = if (isInGrid) {
            // GRID moduan: kalkulatu tamaina automatikoki
            val totalSpacing = spacing * (gridColumns + 1)
            val availableWidth = screenWidth - totalSpacing
            val itemWidth = availableWidth / gridColumns

            modifier
                .fillMaxWidth(0.9f / gridColumns)
                .aspectRatio(1.3f)
                .padding(spacing / 2)
        } else {
            // BAKARRIK edo DRAG moduan: pantaila osoko zabalera
            val aspectRatio = if (isLandscape) 1.6f else 1.3f

            modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(aspectRatio)
        }
    }

    Image(
        painter = painterResource(id = photoRes),
        contentDescription = "Foto ${photoNumber ?: ""}",
        contentScale = ContentScale.Crop,
        alpha = if (isUsed) 0.4f else 1f,
        modifier = imageModifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isUsed) 1.dp else 2.dp,
                color = if (isUsed) Color.Gray else Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .onGloballyPositioned { coords ->
                onPhotoPositioned(coords.boundsInRoot())
            }
            .clickable { onEnlargeClick() }
            .pointerInput(photoRes) {
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragCancel() }
                )
            }
    )
}