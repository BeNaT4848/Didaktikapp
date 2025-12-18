package com.example.errenteriaapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.classes.Character
import com.example.errenteriaapp.classes.DragState

@Composable
fun DraggingWordOverlay(dragState: DragState) {
    val density = LocalDensity.current

    dragState.draggingWord?.let { word ->
        dragState.dragStartBounds?.let { bounds ->
            Box(
                modifier = Modifier
                    .offset(
                        x = with(density) { (bounds.topLeft.x + dragState.dragOffsetPx.x).toDp() },
                        y = with(density) { (bounds.topLeft.y + dragState.dragOffsetPx.y).toDp() }
                    )
                    .size(140.dp, 80.dp)
                    .shadow(12.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (dragState.isDraggingFromSlot) {
                            when (dragState.draggingSlotCharacter) {
                                Character.XANTI -> Color(0xFFBBDEFB)
                                Character.MAIALEN -> Color(0xFFE1BEE7)
                                null -> Color(0xFFFFF3E0)
                            }
                        } else {
                            Color(0xFFFFF3E0).copy(alpha = 0.95f)
                        }
                    )
                    .border(2.dp, Color(0xFF2196F3), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = word,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}