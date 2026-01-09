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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.classes.DragState

@Composable
fun AvailableWordsSection(
    availableWords: List<String>,
    dragState: DragState,
    onDrop: (Offset) -> Unit
) {
    Text(
        text = "Hitzak arrastatu:",
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 8.dp),
        color = MaterialTheme.colorScheme.primary
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp)
    ) {
        LazyRow(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(availableWords.size) { index ->
                val word = availableWords[index]
                DraggableWordItem(
                    word = word,
                    onDragStart = { offset, bounds ->
                        dragState.startDragFromWord(word, offset, bounds)
                    },
                    onDrag = dragState::updateDrag,
                    onDragEnd = {
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