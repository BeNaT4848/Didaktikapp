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

/**
 * Irudi arrastagarri bat erakusten du.
 * Erabiltzaileak irudia arrastatu, klik egin eta handitu dezake.
 * Automatikoki doitzen da pantailaren tamainaren arabera.
 *
 * @param modifier Modifier gehigarria
 * @param photoRes Irudiaren baliabide-identifikadorea
 * @param photoNumber Irudiaren zenbakia (aukerakoa)
 * @param isUsed Irudia erabili den (berriro erabili ezin daiteke)
 * @param isInGrid Irudia sare batean dagoen (eguneratu tamaina)
 * @param gridColumns Sarearen zutabe kopurua
 * @param spacing Sarearen arteko tartea
 * @param onPhotoPositioned Irudiaren posizioa ezagutzen denean deitzen da
 * @param onEnlargeClick Irudia handitzeko klik egitean deitzen da
 * @param onDragStart Arrastatzea hasten denean deitzen da
 * @param onDrag Arrastatzean posizioa aldatzean deitzen da
 * @param onDragEnd Arrastatzea amaitu denean deitzen da
 * @param onDragCancel Arrastatzea bertan behera geratzean deitzen da
 */
@Composable
fun DraggablePhoto(
    modifier: Modifier = Modifier,
    photoRes: Int,
    photoNumber: Int?,
    isUsed: Boolean = false,
    isInGrid: Boolean = true,
    gridColumns: Int = 2,
    spacing: Dp = 8.dp,
    onPhotoPositioned: (Rect) -> Unit,
    onEnlargeClick: () -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    // Oinarrizko Modifier-a: sarean edo pantaila osoko moduan
    val baseModifier = if (isInGrid) Modifier.fillMaxSize() else Modifier.fillMaxWidth(0.9f)

    Image(
        painter = painterResource(id = photoRes),
        contentDescription = "Foto ${photoNumber ?: ""}",
        contentScale = ContentScale.Crop,
        alpha = if (isUsed) 0.4f else 1f,
        modifier = modifier
            .then(baseModifier)
            .padding(spacing / 3)
            .clip(RoundedCornerShape(16.dp))
            // Ertza: erabili dena grisa, bestela zuria
            .border(
                width = if (isUsed) 1.dp else 2.dp,
                color = if (isUsed) Color.Gray else Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            // Posizioa ezagutu
            .onGloballyPositioned { coords -> onPhotoPositioned(coords.boundsInRoot()) }
            // Handitzeko klik
            .clickable { onEnlargeClick() }
            // Arrastatzeko gestuak
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