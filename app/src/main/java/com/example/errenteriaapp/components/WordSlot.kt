package com.example.errenteriaapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.R
import com.example.errenteriaapp.classes.GameWords
import com.example.errenteriaapp.classes.Character

/**
 * Hitzaren "slot"-a erakusten du (pertsonaia bati esleitzeko lekua).
 * Hutsik egon daiteke edo hitz bat izan dezake.
 * Hitzak arrastatzeko modukoak dira "slot"-etik.
 *
 * @param modifier Modifier gehigarria
 * @param slotIndex "Slot"-aren indizea pertsonaia horren zerrendan
 * @param assignedWord Esleitutako hitza (null bada, hutsik)
 * @param character Pertsonaia zeinen "slot"-a den
 * @param onSlotPositioned "Slot"-aren posizioa ezagutzen denean deitzen da (UI elementuaren mugak)
 * @param onDragStart "Slot"-etik arrastatzea hasten denean deitzen da
 * @param onDrag Arrastatzean posizioa aldatzean deitzen da
 * @param onDragEnd Arrastatzea amaitu denean deitzen da
 * @param onDragCancel Arrastatzea bertan behera geratzean deitzen da
 */
@Composable
fun WordSlot(
    modifier: Modifier = Modifier,
    slotIndex: Int,
    assignedWord: String?,
    character: Character,
    onSlotPositioned: (Rect) -> Unit,
    onDragStart: (Offset, Rect) -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    var bounds by remember { mutableStateOf<Rect?>(null) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (assignedWord == null) Color.White.copy(alpha = 0.9f)
                else Color(0xFFFFCC80)
            )
            .border(
                width = 2.dp,
                color = if (assignedWord == null) Color(0xFFBDBDBD) else Color(0xFFFF9800),
                shape = RoundedCornerShape(10.dp),
            )
            .onGloballyPositioned { coords ->
                // GAKOA HEMEN DA: coords.boundsInWindow() Compose-ko Rect itzultzen du
                bounds = coords.boundsInWindow()
                onSlotPositioned(coords.boundsInWindow())
            }
    ) {
        if (assignedWord != null) {
            // Hitz esleituta: arrastagarria da
            Text(
                text = stringResource(GameWords.labelRes(assignedWord)),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 2,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .pointerInput(slotIndex) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                bounds?.let { onDragStart(offset, it) }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                onDrag(dragAmount.x, dragAmount.y)
                            },
                            onDragEnd = { onDragEnd() },
                            onDragCancel = { onDragCancel() }
                        )
                    }
            )
        } else {
            // Hutsik: "arrastatu hona" testua
            Text(
                text = stringResource(R.string.game_drag_here),
                fontSize = 13.sp,
                color = Color(0xFF757575),
                fontWeight = FontWeight.Medium
            )
        }
    }
}