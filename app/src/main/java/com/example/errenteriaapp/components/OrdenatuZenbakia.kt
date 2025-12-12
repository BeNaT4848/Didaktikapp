package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot


// COMPONENTE PARA LOS CUADRADOS CON NUMEROS

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
            .clip(RoundedCornerShape(16.dp))
            .background(
                when {
                    isHighlighted -> Color(0xFFFFF176)
                    assignedPhoto != null && !isCorrectPosition -> Color(0xFFEF9A9A)
                    assignedPhoto != null -> Color(0xFF81C784)
                    else -> Color(0xFFBBDEFB)
                }
            )
            .border(
                width = 2.dp,
                color = Color(0xFF1976D2),
                shape = RoundedCornerShape(16.dp),
            )
            .onGloballyPositioned { coords ->
                onSlotPositioned(coords.boundsInRoot())
            }
    ) {
        if (assignedPhoto != null) {
            Image(
                painter = painterResource(id = assignedPhoto),
                contentDescription = "Foto colocada ${slotIndex + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(14.dp))
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = (slotIndex + 1).toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0D47A1)
                )
            }
        }
    }
}