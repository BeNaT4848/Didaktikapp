package com.example.errenteriaapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.R
import com.example.errenteriaapp.classes.DragState

/**
 * Erabili daitezkeen hitzen atala erakusten du (eskuineko hiztegi-kutxa).
 * Hitzak LazyRow batean bistaratzen ditu arrastatzeko moduan.
 *
 * @param availableWords Erabili daitezkeen hitzen zerrenda
 * @param dragState Arrastatzearen egoera
 * @param onDrop Hitz bat eremu batean askatzean deitzen den funtzioa
 */
@Composable
fun AvailableWordsSection(
    availableWords: List<String>,
    dragState: DragState,
    onDrop: (Offset) -> Unit
) {
    // Atalaren izenburua
    Text(
        text = stringResource(R.string.game_drag_words),
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 8.dp),
        color = MaterialTheme.colorScheme.primary
    )

    // Hitzak erakusten dituen kutxa
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp)
    ) {
        // Hitzak lerro horizontalean
        LazyRow(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(availableWords.size) { index ->
                val word = availableWords[index]
                // Hitz arrastagarri bakoitza
                DraggableWordItem(
                    word = word,
                    onDragStart = { offset, bounds ->
                        dragState.startDragFromWord(word, offset, bounds)
                    },
                    onDrag = dragState::updateDrag,
                    onDragEnd = {
                        // Hitz askatzean, askatze-puntua pasatu
                        dragState.dragCenterPx?.let { dropPoint ->
                            onDrop(dropPoint)
                        }
                        dragState.reset()
                    },
                    onDragCancel = dragState::reset
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}