package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.dp
import com.example.errenteriaapp.classes.Character
import com.example.errenteriaapp.classes.DragState

@Composable
fun CharactersRow(
    xantiAssignments: List<String?>,
    maialenAssignments: List<String?>,
    onXantiSlotPositioned: (Int, Rect) -> Unit,
    onMaialenSlotPositioned: (Int, Rect) -> Unit,
    dragState: DragState,
    onDrop: (Offset) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CharacterArea(
            character = Character.MAIALEN,
            assignments = maialenAssignments,
            onSlotPositioned = onMaialenSlotPositioned,
            onDragStart = { character, index, startPosition, bounds ->
                maialenAssignments.getOrNull(index)?.let { word ->
                    dragState.isDraggingFromSlot = true
                    dragState.draggingSlotCharacter = character
                    dragState.draggingSlotIndex = index
                    dragState.draggingWord = word
                    dragState.dragStartPosition = startPosition
                    dragState.dragStartBounds = bounds
                }
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

        Spacer(modifier = Modifier.width(12.dp))

        CharacterArea(
            character = Character.XANTI,
            assignments = xantiAssignments,
            onSlotPositioned = onXantiSlotPositioned,
            onDragStart = { character, index, startPosition, bounds ->
                xantiAssignments.getOrNull(index)?.let { word ->
                    dragState.isDraggingFromSlot = true
                    dragState.draggingSlotCharacter = character
                    dragState.draggingSlotIndex = index
                    dragState.draggingWord = word
                    dragState.dragStartPosition = startPosition
                    dragState.dragStartBounds = bounds
                }
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
    Spacer(modifier = Modifier.height(20.dp))
}