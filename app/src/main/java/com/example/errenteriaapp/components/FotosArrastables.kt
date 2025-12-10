package com.example.errenteriaapp.components

// DraggablePhoto.kt
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.unit.dp


// COMPONENTEA: Irudi bat erakusten du, arrastatu daitekeena eta klik egin daitekeena handitzeko.

@Composable
fun DraggablePhoto(
    modifier: Modifier = Modifier,
    photoRes: Int,
    photoNumber: Int?,
    isUsed: Boolean = false,
    onPhotoPositioned: (Rect) -> Unit,
    onEnlargeClick: () -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    Image(
        painter = painterResource(id = photoRes),
        contentDescription = "Foto ${photoNumber ?: ""}",
        contentScale = ContentScale.Crop,
        alpha = if (isUsed) 0.4f else 1f,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.3f)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, Color.White, RoundedCornerShape(16.dp))
            .onGloballyPositioned { coords ->
                onPhotoPositioned(coords.boundsInRoot())
            }
            .clickable { onEnlargeClick() }
            .pointerInput(photoRes) {
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
    )
}