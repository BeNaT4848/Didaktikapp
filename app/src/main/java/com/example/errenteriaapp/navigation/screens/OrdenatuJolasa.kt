package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.errenteriaapp.components.DraggablePhoto
import com.example.errenteriaapp.components.DraggingImage
import com.example.errenteriaapp.components.GameResultDialogs
import com.example.errenteriaapp.components.GameSlot
import com.example.errenteriaapp.database.viewModel.OrdenatuJolasaViewModel
import com.example.errenteriaapp.navigation.Routes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

@Composable
fun OrdenatuJolasaScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: OrdenatuJolasaViewModel = viewModel()
) {
    val photoNumberMap = viewModel.photoNumberMap
    val photos = viewModel.photos
    val slotAssignments = viewModel.slotAssignments
    val slotCount = photos.size
    val showSuccessDialog = viewModel.showSuccessDialog
    val showWrongDialog = viewModel.showWrongDialog

    val dropZones = remember(slotCount) {
        mutableStateListOf<Rect?>().apply { repeat(slotCount) { add(null) } }
    }
    val photoBounds = remember(photos) {
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
    var isDraggingFromSlot by remember { mutableStateOf(false) }
    var draggingSlotIndex by remember { mutableStateOf<Int?>(null) }

    val isPhotoInCorrectSlot: (Int, Int) -> Boolean = { photoRes, slotIndex ->
        photoNumberMap[photoRes] == slotIndex + 1
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
                    viewModel.swapSlots(sourceSlot, targetIndex)
                }
            } else {
                val photoIndex = draggingPhotoIndex
                if (photoIndex != null) {
                    viewModel.assignPhotoToSlot(photos[photoIndex], targetIndex)
                }
            }
        }
        resetDragState()
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black), // fondo que cubre toda la pantalla
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                verticalArrangement = Arrangement.spacedBy(6.dp)
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
                color = MaterialTheme.colorScheme.primary,
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
            onDismissSuccess = { viewModel.dismissDialogs() },
            onDismissWrong = { viewModel.dismissDialogs() },
            onSuccessButton = {
                viewModel.dismissDialogs()
                navController.navigate(Routes.MAPA_SCREEN)
            },
            onWrongButton = {
                viewModel.resetGame()
                viewModel.dismissDialogs()
                navController.navigate(Routes.ORDENATUJOLASA_SCREEN)
            }
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