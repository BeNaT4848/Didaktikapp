package com.example.errenteriaapp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

/**
 * Arrastatzen ari den irudia erakusten du.
 * Erabiltzailea irudi bat arrastatzen ari denean agertzen da.
 *
 * @param photoRes Irudiaren baliabide-identifikadorea
 * @param boundsTopLeft Irudiaren jatorrizko goi-ezkerreko posizioa (pixeletan)
 * @param dragOffsetPx Arrastatzearen desplazamendua (pixeletan)
 * @param widthDp Irudiaren zabalera (dp-tan)
 * @param heightDp Irudiaren altuera (dp-tan)
 * @param modifier Modifier gehigarria
 */
@Composable
fun DraggingImage(
    photoRes: Int,
    boundsTopLeft: Offset,
    dragOffsetPx: Offset,
    widthDp: Dp,
    heightDp: Dp,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = photoRes),
        contentDescription = "Foto en arrastre",
        contentScale = ContentScale.Crop,
        modifier = modifier
            // Posizioa kalkulatu desplazamendua kontuan hartuta
            .offset {
                val offsetPx = boundsTopLeft + dragOffsetPx
                IntOffset(offsetPx.x.roundToInt(), offsetPx.y.roundToInt())
            }
            .width(widthDp * 1.2f)   // 🔹 handiagoa
            .height(heightDp * 1.2f) // 🔹 handiagoa
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, Color.White, RoundedCornerShape(16.dp))
            .zIndex(1f) // Gainetik egoteko
    )
}