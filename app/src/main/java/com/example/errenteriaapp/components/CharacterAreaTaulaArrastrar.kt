package com.example.errenteriaapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.classes.Character

/**
 * Pertsonaiaren eremua erakusten du, bere "slot"-ekin.
 * Pertsonaiaren izena eta hitz "slot" guztiak erakusten ditu.
 * "Slot"-ak hitzak hartzeko eta arrastatzeko modukoak dira.
 *
 * @param character Pertsonaiaren informazioa (Character enum)
 * @param assignments Pertsonaiari esleitutako hitzen zerrenda (null = hutsik)
 * @param onSlotPositioned "Slot"-aren posizioa ezagutu denean deitzen da (UI elementuaren mugak)
 * @param onDragStart "Slot"-etik arrastatzea hasten denean deitzen da
 * @param onDrag Arrastatzean posizioa aldatzean deitzen da
 * @param onDragEnd Arrastatzea amaitu denean deitzen da
 * @param onDragCancel Arrastatzea bertan behera geratzean deitzen da
 */
@Composable
fun CharacterArea(
    character: Character,
    assignments: List<String?>,
    onSlotPositioned: (Int, Rect) -> Unit,
    onDragStart: (Character, Int, Offset, Rect) -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(character.backgroundColor)
            .border(2.dp, Color(0xFF757575), RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pertsonaiaren izena
        Text(
            text = character.displayName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Hitz "slot"-en zerrenda
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            assignments.forEachIndexed { index, word ->
                WordSlot(
                    slotIndex = index,
                    assignedWord = word,
                    character = character,
                    onSlotPositioned = { rect -> onSlotPositioned(index, rect) },
                    onDragStart = { offset, bounds ->
                        onDragStart(character, index, offset, bounds)
                    },
                    onDrag = onDrag,
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragCancel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                )
            }
        }
    }
}