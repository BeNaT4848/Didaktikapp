package com.example.errenteriaapp.classes

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

@Stable
class DragState {
    var draggingWord by mutableStateOf<String?>(null)
    var dragStartBounds by mutableStateOf<Rect?>(null)
    var dragOffsetPx by mutableStateOf(Offset.Zero)
    var dragCenterPx by mutableStateOf<Offset?>(null)
    var isDraggingFromSlot by mutableStateOf(false)
    var draggingSlotCharacter by mutableStateOf<Character?>(null)
    var draggingSlotIndex by mutableStateOf<Int?>(null)
    var dragStartPosition by mutableStateOf(Offset.Zero)

    fun startDragFromWord(word: String, offset: Offset, bounds: Rect?) {
        draggingWord = word
        dragStartPosition = offset
        dragStartBounds = bounds
        dragOffsetPx = Offset.Zero
        dragCenterPx = bounds?.center
        isDraggingFromSlot = false
    }

    fun startDragFromSlot(
        character: Character,
        index: Int,
        startPosition: Offset,
        bounds: Rect?
    ) {
        isDraggingFromSlot = true
        draggingSlotCharacter = character
        draggingSlotIndex = index
        dragStartPosition = startPosition
        dragStartBounds = bounds
    }

    fun updateDrag(x: Float, y: Float) {
        dragOffsetPx += Offset(x, y)
        dragStartBounds?.let { bounds ->
            dragCenterPx = bounds.topLeft + dragOffsetPx +
                    Offset(bounds.width / 2f, bounds.height / 2f)
        }
    }

    fun reset() {
        draggingWord = null
        draggingSlotCharacter = null
        draggingSlotIndex = null
        dragStartBounds = null
        dragOffsetPx = Offset.Zero
        dragCenterPx = null
        isDraggingFromSlot = false
        dragStartPosition = Offset.Zero
    }
}

@Composable
fun rememberDragState(): DragState {
    return remember { DragState() }
}