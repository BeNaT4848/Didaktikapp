package com.example.errenteriaapp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Jokoaren "slot" karratua erakusten du (zenbaki batekin edo irudi batekin).
 * "Ordenatu" jokoan erabiltzen da irudiak ordenatzeko.
 *
 * @param modifier Modifier gehigarria
 * @param slotIndex "Slot"-aren indizea (0tik hasita)
 * @param assignedPhoto Esleitutako irudiaren baliabide-identifikadorea (null bada, hutsik)
 * @param photoNumberMap Irudiaren baliabide-identifikadorearen zenbaki mapa
 * @param isHighlighted "Slot"-a nabarmentzen den (erabiltzailea gainean dagoenean)
 * @param isCorrectPosition Irudia posizio zuzenan dagoen
 * @param onSlotPositioned "Slot"-aren posizioa ezagutzen denean deitzen da
 * @param onDragStart Arrastatzea hasten denean deitzen den funtzioa (null bada, ez da arrastagarria)
 * @param onDrag Arrastatzean posizioa aldatzean deitzen den funtzioa
 * @param onDragEnd Arrastatzea amaitu denean deitzen den funtzioa
 * @param onDragCancel Arrastatzea bertan behera geratzean deitzen den funtzioa
 */
@Composable
fun GameSlot(
    modifier: Modifier = Modifier,
    slotIndex: Int,
    assignedPhoto: Int?,
    photoNumberMap: Map<Int, Int>,
    isHighlighted: Boolean = false,
    isCorrectPosition: Boolean = true,
    onSlotPositioned: (Rect) -> Unit,
    onDragStart: (() -> Unit)? = null,
    onDrag: ((Float, Float) -> Unit)? = null,
    onDragEnd: (() -> Unit)? = null,
    onDragCancel: (() -> Unit)? = null
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .padding(8.dp) // 🔹 tarte gehiago
            .clip(RoundedCornerShape(18.dp))
            // Atzeko kolorea egoeraren arabera
            .background(
                when {
                    isHighlighted -> Color(0xFFFFF176) // Hori (nabarmenduta)
                    assignedPhoto != null && !isCorrectPosition -> Color(0xFFEF9A9A) // Gorria (posizio okerra)
                    assignedPhoto != null -> Color(0xFF81C784) // Berdea (posizio zuzena)
                    else -> Color(0xFFBBDEFB) // Urdina (hutsik)
                }
            )
            .border(
                width = 2.dp,
                color = Color(0xFF1976D2),
                shape = RoundedCornerShape(18.dp)
            )
            // Posizioa ezagutu
            .onGloballyPositioned { coords ->
                onSlotPositioned(coords.boundsInRoot())
            }
    ) {
        if (assignedPhoto != null) {
            // Irudia erakutsi
            Image(
                painter = painterResource(id = assignedPhoto),
                contentDescription = "Foto colocada ${slotIndex + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    // Arrastatzeko gestuak baimendu
                    .pointerInput(slotIndex) {
                        if (onDragStart != null && onDrag != null && onDragEnd != null && onDragCancel != null) {
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
                    }
            )
        } else {
            // 🔢 Zenbakia erakutsi (hutsik dagoenean)
            Text(
                text = (slotIndex + 1).toString(),
                fontSize = 20.sp, // 🔹 txikiagoa
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}