package com.example.errenteriaapp.classes

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

/**
 * Arrastratze-operazioaren egoera gordetzen du.
 * @Stable Compose-ri egonkorra dela adierazteko
 */
@Stable
class DragState {
    /** Arrastratzen ari den hitza (null bada, ezer ez dago arrastratzen) */
    var draggingWord by mutableStateOf<String?>(null)

    /** Arrastratu aurretik elementuaren mugak */
    var dragStartBounds by mutableStateOf<Rect?>(null)

    /** Arrastratzearen desplazamendua pixeletan */
    var dragOffsetPx by mutableStateOf(Offset.Zero)

    /** Arrastratzen ari den elementuaren erdigunea pixeletan */
    var dragCenterPx by mutableStateOf<Offset?>(null)

    /** Elementua "slot" batetik arrastratzen ari den */
    var isDraggingFromSlot by mutableStateOf(false)

    /** "Slot" hori zein pertsonairen parte den */
    var draggingSlotCharacter by mutableStateOf<Character?>(null)

    /** "Slot"-aren indizea pertsonaia horren zerrendan */
    var draggingSlotIndex by mutableStateOf<Int?>(null)

    /** Arrastatzearen hasierako posizioa pantailan */
    var dragStartPosition by mutableStateOf(Offset.Zero)

    /**
     * Hitz batetik arrastatzea hasten du.
     * @param word Arrastatzen hasi den hitza
     * @param offset Saguaren hasierako posizioa
     * @param bounds Hitza duen elementuaren mugak
     */
    fun startDragFromWord(word: String, offset: Offset, bounds: Rect?) {
        draggingWord = word
        dragStartPosition = offset
        dragStartBounds = bounds
        dragOffsetPx = Offset.Zero
        dragCenterPx = bounds?.center
        isDraggingFromSlot = false
    }

    /**
     * "Slot" batetik arrastatzea hasten du (lehendik esleitutako hitza).
     * @param character Pertsonaia zeinen "slot"-a den
     * @param index "Slot"-aren indizea pertsonaia horren zerrendan
     * @param startPosition Saguaren hasierako posizioa
     * @param bounds "Slot" elementuaren mugak
     */
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

    /**
     * Arrastatzearen posizioa eguneratzen du.
     * @param x X ardatzean mugitutako distantzia
     * @param y Y ardatzean mugitutako distantzia
     */
    fun updateDrag(x: Float, y: Float) {
        dragOffsetPx += Offset(x, y)
        dragStartBounds?.let { bounds ->
            dragCenterPx = bounds.topLeft + dragOffsetPx +
                    Offset(bounds.width / 2f, bounds.height / 2f)
        }
    }

    /**
     * Arrastatzearen egoera berrezartzen du hasierako balioetara.
     */
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

/**
 * DragState bat gogoratzen du Composable baten barruan.
 * @return DragState instantzia berria edo gogoratutakoa
 */
@Composable
fun rememberDragState(): DragState {
    return remember { DragState() }
}