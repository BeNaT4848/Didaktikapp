package com.example.errenteriaapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import com.example.errenteriaapp.classes.GameWords

@Composable
fun DraggableWordItem(
    word: String,
    onDragStart: (Offset, Rect) -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    var bounds by remember { mutableStateOf<Rect?>(null) }

    Box(
        modifier = Modifier
            .size(140.dp, 80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFFF3E0))
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .onGloballyPositioned { coords ->
                bounds = coords.boundsInWindow()
            }
            .pointerInput(word) {
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
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(GameWords.labelRes(word)),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            maxLines = 2,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )
    }
}