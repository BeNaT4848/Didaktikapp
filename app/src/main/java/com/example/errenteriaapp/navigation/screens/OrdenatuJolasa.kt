package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.errenteriaapp.R
import kotlin.math.roundToInt

@Composable
fun GameScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val orderedPhotos = remember {
        listOf(
            R.drawable.errota_prozesua_3,
            R.drawable.errota_prozesua_1,
            R.drawable.errota_prozesua_5,
            R.drawable.errota_prozesua_2,
            R.drawable.errota_prozesua_6,
            R.drawable.errota_prozesua_4
        )
    }
    GameScreen(
        navController = navController,
        photos = orderedPhotos,
        modifier = modifier
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameScreen(
    navController: NavController,
    photos: List<Int>,
    modifier: Modifier = Modifier
) {
    val slotCount = photos.size
    val slotAssignments = remember(slotCount) {
        mutableStateListOf<Int?>().apply { repeat(slotCount) { add(null) } }
    }
    val dropZones = remember(slotCount) {
        mutableStateListOf<Rect?>().apply { repeat(slotCount) { add(null) } }
    }
    val photoBounds = remember(photos.size) {
        mutableStateListOf<Rect?>().apply { repeat(photos.size) { add(null) } }
    }

    var draggingPhotoIndex by remember { mutableStateOf<Int?>(null) }
    var dragStartBounds by remember { mutableStateOf<Rect?>(null) }
    var dragOffsetPx by remember { mutableStateOf(Offset.Zero) }
    var dragCenterPx by remember { mutableStateOf<Offset?>(null) }
    var enlargedPhoto by remember { mutableStateOf<Int?>(null) }

    val isComplete by remember {
        derivedStateOf { slotAssignments.all { it != null } }
    }
    val isCorrect by remember(photos) {
        derivedStateOf {
            isComplete && slotAssignments.zip(photos).all { (assigned, expected) ->
                assigned == expected
            }
        }
    }

    fun resetDragState() {
        draggingPhotoIndex = null
        dragStartBounds = null
        dragOffsetPx = Offset.Zero
        dragCenterPx = null
    }

    fun handleDrop() {
        val photoIndex = draggingPhotoIndex
        val dropPoint = dragCenterPx
        if (photoIndex == null || dropPoint == null) {
            resetDragState()
            return
        }
        val targetIndex = dropZones.indexOfFirst { rect -> rect?.contains(dropPoint) == true }
        if (targetIndex != -1) {
            val photoRes = photos[photoIndex]
            val previousSlot = slotAssignments.indexOf(photoRes)
            if (previousSlot != -1 && previousSlot != targetIndex) {
                slotAssignments[previousSlot] = null
            }
            slotAssignments[targetIndex] = photoRes
        }
        resetDragState()
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(photos.size) { index ->
                    val photoRes = photos[index]
                    val isUsed = slotAssignments.contains(photoRes)
                    Image(
                        painter = painterResource(id = photoRes),
                        contentDescription = "Foto ${index + 1}",
                        contentScale = ContentScale.Crop,
                        alpha = if (isUsed) 0.4f else 1f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.3f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(2.dp, Color.White, RoundedCornerShape(16.dp))
                            .onGloballyPositioned { coords ->
                                photoBounds[index] = coords.boundsInRoot()
                            }
                            .clickable { enlargedPhoto = photoRes }
                            .pointerInput(index) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        val bounds = photoBounds.getOrNull(index)
                                        if (bounds != null) {
                                            draggingPhotoIndex = index
                                            dragStartBounds = bounds
                                            dragOffsetPx = Offset.Zero
                                            dragCenterPx = bounds.center
                                        }
                                    },
                                    onDrag = { change, dragAmount ->
                                        if (draggingPhotoIndex == index && dragStartBounds != null) {
                                            change.consume()
                                            dragOffsetPx += dragAmount
                                            val bounds = dragStartBounds!!
                                            dragCenterPx = bounds.topLeft + dragOffsetPx +
                                                    Offset(bounds.width / 2f, bounds.height / 2f)
                                        }
                                    },
                                    onDragEnd = {
                                        if (draggingPhotoIndex == index) handleDrop()
                                    },
                                    onDragCancel = {
                                        if (draggingPhotoIndex == index) resetDragState()
                                    }
                                )
                            }
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(slotCount) { index ->
                    val dropRect = dropZones.getOrNull(index)
                    val isHighlighted = dropRect != null &&
                            (dragCenterPx?.let { dropRect.contains(it) } == true)
                    val assignedPhoto = slotAssignments[index]

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when {
                                    isHighlighted -> Color(0xFFFFE082)
                                    assignedPhoto != null -> Color(0xFFE0F7FA)
                                    else -> Color(0xFFEEEEEE)
                                }
                            )
                            .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                            .onGloballyPositioned { coords ->
                                val rect = coords.boundsInRoot()
                                if (dropZones.size > index) {
                                    dropZones[index] = rect
                                }
                            }
                    ) {
                        if (assignedPhoto != null) {
                            Image(
                                painter = painterResource(id = assignedPhoto),
                                contentDescription = "Foto colocada ${index + 1}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(10.dp))
                            )
                        } else {
                            Text(
                                text = (index + 1).toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (isComplete) {
                Text(
                    text = if (isCorrect) "¡Perfecto! Orden correcto." else "Orden incorrecto, revisa las fotos.",
                    color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        val density = LocalDensity.current

        if (draggingPhotoIndex != null && dragStartBounds != null) {
            val bounds = dragStartBounds!!
            val widthDp = with(density) { bounds.width.toDp() } * 0.7f
            val heightDp = with(density) { bounds.height.toDp() } * 0.7f

            Image(
                painter = painterResource(id = photos[draggingPhotoIndex!!]),
                contentDescription = "Foto en arrastre",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .offset {
                        val offsetPx = bounds.topLeft + dragOffsetPx
                        IntOffset(offsetPx.x.roundToInt(), offsetPx.y.roundToInt())
                    }
                    .width(widthDp)
                    .height(heightDp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, Color.White, RoundedCornerShape(12.dp))
                    .zIndex(1f)
            )
        }

        if (enlargedPhoto != null) {
            Dialog(onDismissRequest = { enlargedPhoto = null }) {
                Image(
                    painter = painterResource(id = enlargedPhoto!!),
                    contentDescription = "Foto ampliada",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GameScreenPreview() {
    val navController = rememberNavController()
    GameScreen(navController = navController)
}
