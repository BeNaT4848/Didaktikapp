package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.errenteriaapp.R
import com.example.errenteriaapp.components.DraggablePhoto
import com.example.errenteriaapp.components.DraggingImage
import com.example.errenteriaapp.components.GameResultDialogs
import com.example.errenteriaapp.components.GameSlot
import com.example.errenteriaapp.navigation.Routes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay

@Composable
fun OrdenatuJolasaScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Crear un mapa que defina qué foto va con qué número
    val photoNumberMap = remember {
        mapOf(
            R.drawable.errota_prozesua_1 to 1,
            R.drawable.errota_prozesua_2 to 2,
            R.drawable.errota_prozesua_3 to 3,
            R.drawable.errota_prozesua_4 to 4,
            R.drawable.errota_prozesua_5 to 5,
            R.drawable.errota_prozesua_6 to 6
        )
    }

    val photos = remember { photoNumberMap.keys.toList().shuffled() }
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
    val placedPhotoBounds = remember(slotCount) {
        mutableStateListOf<Rect?>().apply { repeat(slotCount) { add(null) } }
    }

    var draggingPhotoIndex by remember { mutableStateOf<Int?>(null) }
    var dragStartBounds by remember { mutableStateOf<Rect?>(null) }
    var dragOffsetPx by remember { mutableStateOf(Offset.Zero) }
    var dragCenterPx by remember { mutableStateOf<Offset?>(null) }
    var enlargedPhoto by remember { mutableStateOf<Int?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showWrongDialog by remember { mutableStateOf(false) }
    var isDraggingFromSlot by remember { mutableStateOf(false) }
    var draggingSlotIndex by remember { mutableStateOf<Int?>(null) }

    val isComplete by remember {
        derivedStateOf { slotAssignments.all { it != null } }
    }

    val correctCount by remember(slotAssignments) {
        derivedStateOf {
            slotAssignments.withIndex().count { (slotIndex, photoRes) ->
                photoRes != null && photoNumberMap[photoRes] == slotIndex + 1
            }
        }
    }

    fun isPhotoInCorrectSlot(photoRes: Int, slotIndex: Int): Boolean =
        photoNumberMap[photoRes] == slotIndex + 1

    LaunchedEffect(isComplete) {
        if (isComplete) {
            if (correctCount >= 3) {
                showSuccessDialog = true
            } else {
                showWrongDialog = true
            }
        }
    }

    fun resetDragState() {
        draggingPhotoIndex = null
        draggingSlotIndex = null
        dragStartBounds = null
        dragOffsetPx = Offset.Zero
        dragCenterPx = null
        isDraggingFromSlot = false
    }

    fun handleDrop() {
        val dropPoint = dragCenterPx ?: return resetDragState()
        val targetIndex = dropZones.indexOfFirst { rect -> rect?.contains(dropPoint) == true }

        if (targetIndex != -1) {
            if (isDraggingFromSlot) {
                val sourceSlot = draggingSlotIndex
                if (sourceSlot != null && sourceSlot != targetIndex) {
                    val photoRes = slotAssignments[sourceSlot]
                    if (photoRes != null) {
                        val currentInTarget = slotAssignments[targetIndex]
                        slotAssignments[sourceSlot] = currentInTarget
                        slotAssignments[targetIndex] = photoRes
                    }
                }
            } else {
                val photoIndex = draggingPhotoIndex
                if (photoIndex != null) {
                    val photoRes = photos[photoIndex]
                    val previousSlot = slotAssignments.indexOf(photoRes)
                    if (previousSlot != -1 && previousSlot != targetIndex) {
                        slotAssignments[previousSlot] = null
                    }
                    slotAssignments[targetIndex] = photoRes
                }
            }
        }
        resetDragState()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
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
                    DraggablePhoto(
                        photoRes = photoRes,
                        photoNumber = photoNumberMap[photoRes],
                        isUsed = slotAssignments.contains(photoRes),
                        onPhotoPositioned = { rect -> photoBounds[index] = rect },
                        onEnlargeClick = { enlargedPhoto = photoRes },
                        onDragStart = {
                            if (!slotAssignments.contains(photoRes)) {
                                draggingPhotoIndex = index
                                dragStartBounds = photoBounds[index]
                                dragOffsetPx = Offset.Zero
                                dragCenterPx = photoBounds[index]?.center
                                isDraggingFromSlot = false
                            }
                        },
                        onDrag = { x, y ->
                            dragOffsetPx += Offset(x, y)
                            dragStartBounds?.let { bounds ->
                                dragCenterPx = bounds.topLeft + dragOffsetPx +
                                        Offset(bounds.width / 2f, bounds.height / 2f)
                            }
                        },
                        onDragEnd = { handleDrop() },
                        onDragCancel = { resetDragState() }
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
                    val isHighlighted = dropZones.getOrNull(index)?.let { rect ->
                        dragCenterPx?.let { rect.contains(it) } == true
                    } ?: false
                    val assignedPhoto = slotAssignments[index]

                    GameSlot(
                        slotIndex = index,
                        assignedPhoto = assignedPhoto,
                        photoNumberMap = photoNumberMap,
                        isHighlighted = isHighlighted,
                        isCorrectPosition = assignedPhoto?.let {
                            isPhotoInCorrectSlot(it, index)
                        } ?: true,
                        onSlotPositioned = { rect ->
                            dropZones[index] = rect
                            if (assignedPhoto != null) {
                                placedPhotoBounds[index] = rect
                            }
                        },
                        onDragStart = {
                            placedPhotoBounds.getOrNull(index)?.let { bounds ->
                                draggingSlotIndex = index
                                dragStartBounds = bounds
                                dragOffsetPx = Offset.Zero
                                dragCenterPx = bounds.center
                                isDraggingFromSlot = true
                            }
                        },
                        onDrag = { x, y ->
                            dragOffsetPx += Offset(x, y)
                            dragStartBounds?.let { bounds ->
                                dragCenterPx = bounds.topLeft + dragOffsetPx +
                                        Offset(bounds.width / 2f, bounds.height / 2f)
                            }
                        },
                        onDragEnd = { handleDrop() },
                        onDragCancel = { resetDragState() }
                    )
                }
            }

            Text(
                text = "Abenturarekin jarraitzeko gutxienez 3 ondo izan behar dituzu.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        if ((draggingPhotoIndex != null || draggingSlotIndex != null) && dragStartBounds != null) {
            val photoRes = when {
                isDraggingFromSlot && draggingSlotIndex != null -> slotAssignments[draggingSlotIndex!!]
                !isDraggingFromSlot && draggingPhotoIndex != null -> photos[draggingPhotoIndex!!]
                else -> null
            }

            if (photoRes != null) {
                val bounds = dragStartBounds!!
                val widthDp = with(LocalDensity.current) { bounds.width.toDp() } * 0.7f
                val heightDp = with(LocalDensity.current) { bounds.height.toDp() } * 0.7f

                DraggingImage(
                    photoRes = photoRes,
                    boundsTopLeft = bounds.topLeft,
                    dragOffsetPx = dragOffsetPx,
                    widthDp = widthDp,
                    heightDp = heightDp,
                    modifier = Modifier.zIndex(1f)
                )
            }
        }

        GameResultDialogs(
            showSuccess = showSuccessDialog,
            showWrong = showWrongDialog,
            onDismissSuccess = { showSuccessDialog = false },
            onDismissWrong = { showWrongDialog = false },
            onSuccessButton = { navController.navigate(Routes.MAPA_SCREEN) },
            onWrongButton = { navController.navigate(Routes.ORDENATUJOLASA_SCREEN) }
        )

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