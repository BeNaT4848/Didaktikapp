package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.errenteriaapp.components.*
import com.example.errenteriaapp.database.viewModel.OrdenatuJolasaViewModel
import com.example.errenteriaapp.navigation.Routes
import com.example.errenteriaapp.progress.KokapenaProgressRepository
import com.example.errenteriaapp.R

@Composable
fun OrdenatuJolasaScreen(
    navController: NavController,
    userName: String?,
    viewModel: OrdenatuJolasaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sessionPrefs = remember { context.getSharedPreferences("session", android.content.Context.MODE_PRIVATE) }
    val effectiveUserName = userName ?: sessionPrefs.getString("active_user_name", null)
    val progressRepo = remember(effectiveUserName) {
        KokapenaProgressRepository(context, effectiveUserName ?: "default")
    }

    LaunchedEffect(effectiveUserName) {
        effectiveUserName?.let { viewModel.setUsuario(it) }
    }

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
    var draggingSlotIndex by remember { mutableStateOf<Int?>(null) }
    var dragStartBounds by remember { mutableStateOf<Rect?>(null) }
    var dragOffsetPx by remember { mutableStateOf(Offset.Zero) }
    var dragCenterPx by remember { mutableStateOf<Offset?>(null) }
    var enlargedPhoto by remember { mutableStateOf<Int?>(null) }
    var isDraggingFromSlot by remember { mutableStateOf(false) }

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
        val targetIndex = dropZones.indexOfFirst { it?.contains(dropPoint) == true }

        if (targetIndex != -1) {
            if (isDraggingFromSlot) {
                draggingSlotIndex?.let {
                    if (it != targetIndex) viewModel.swapSlots(it, targetIndex)
                }
            } else {
                draggingPhotoIndex?.let {
                    viewModel.assignPhotoToSlot(photos[it], targetIndex)
                }
            }
        }
        resetDragState()
    }

    val photoGridHeight: Dp = 210.dp
    val slotGridHeight: Dp = 260.dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val isTablet = maxWidth >= 600.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.game_ordenatu_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.game_ordenatu_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (isTablet) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            tonalElevation = 4.dp,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.game_ordenatu_photos),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )

                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    userScrollEnabled = false,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    items(photos.size) { index ->
                                        val photoRes = photos[index]
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(1.1f),
                                            shape = RoundedCornerShape(24.dp),
                                            elevation = CardDefaults.cardElevation(6.dp)
                                        ) {
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
                                                        dragCenterPx =
                                                            bounds.topLeft + dragOffsetPx +
                                                                    Offset(bounds.width / 2, bounds.height / 2)
                                                    }
                                                },
                                                onDragEnd = { handleDrop() },
                                                onDragCancel = { resetDragState() }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            tonalElevation = 4.dp,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.game_ordenatu_locations),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )

                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    userScrollEnabled = false,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    items(slotCount) { index ->
                                        val assignedPhoto = slotAssignments[index]
                                        val isHighlighted = dropZones[index]?.let { rect ->
                                            dragCenterPx?.let { rect.contains(it) } == true
                                        } ?: false

                                        GameSlot(
                                            modifier = Modifier.aspectRatio(0.9f),
                                            slotIndex = index,
                                            assignedPhoto = assignedPhoto,
                                            photoNumberMap = photoNumberMap,
                                            isHighlighted = isHighlighted,
                                            isCorrectPosition = assignedPhoto?.let {
                                                isPhotoInCorrectSlot(it, index)
                                            } ?: true,
                                            onSlotPositioned = { rect ->
                                                dropZones[index] = rect
                                                if (assignedPhoto != null) placedPhotoBounds[index] = rect
                                            },
                                            onDragStart = {
                                                placedPhotoBounds[index]?.let { bounds ->
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
                                                    dragCenterPx =
                                                        bounds.topLeft + dragOffsetPx +
                                                                Offset(bounds.width / 2, bounds.height / 2)
                                                }
                                            },
                                            onDragEnd = { handleDrop() },
                                            onDragCancel = { resetDragState() }
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 4.dp,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.game_ordenatu_photos),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                userScrollEnabled = false,
                                modifier = Modifier
                                    .height(photoGridHeight)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(photos.size) { index ->
                                    val photoRes = photos[index]
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1.15f),
                                        shape = RoundedCornerShape(24.dp),
                                        elevation = CardDefaults.cardElevation(6.dp)
                                    ) {
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
                                                    dragCenterPx =
                                                        bounds.topLeft + dragOffsetPx +
                                                                Offset(bounds.width / 2, bounds.height / 2)
                                                }
                                            },
                                            onDragEnd = { handleDrop() },
                                            onDragCancel = { resetDragState() }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 4.dp,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.game_ordenatu_locations),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                userScrollEnabled = false,
                                modifier = Modifier
                                    .height(slotGridHeight)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(slotCount) { index ->
                                    val assignedPhoto = slotAssignments[index]
                                    val isHighlighted = dropZones[index]?.let { rect ->
                                        dragCenterPx?.let { rect.contains(it) } == true
                                    } ?: false

                                    GameSlot(
                                        modifier = Modifier.aspectRatio(0.9f),
                                        slotIndex = index,
                                        assignedPhoto = assignedPhoto,
                                        photoNumberMap = photoNumberMap,
                                        isHighlighted = isHighlighted,
                                        isCorrectPosition = assignedPhoto?.let {
                                            isPhotoInCorrectSlot(it, index)
                                        } ?: true,
                                        onSlotPositioned = { rect ->
                                            dropZones[index] = rect
                                            if (assignedPhoto != null) placedPhotoBounds[index] = rect
                                        },
                                        onDragStart = {
                                            placedPhotoBounds[index]?.let { bounds ->
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
                                                dragCenterPx =
                                                    bounds.topLeft + dragOffsetPx +
                                                            Offset(bounds.width / 2, bounds.height / 2)
                                            }
                                        },
                                        onDragEnd = { handleDrop() },
                                        onDragCancel = { resetDragState() }
                                    )
                                }
                            }
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.game_ordenatu_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if ((draggingPhotoIndex != null || draggingSlotIndex != null) && dragStartBounds != null) {
            val photoRes = if (isDraggingFromSlot)
                slotAssignments[draggingSlotIndex!!]
            else
                photos[draggingPhotoIndex!!]

            photoRes?.let {
                val bounds = dragStartBounds!!
                val widthDp = with(LocalDensity.current) { bounds.width.toDp() } * 0.75f
                val heightDp = with(LocalDensity.current) { bounds.height.toDp() } * 0.75f

                DraggingImage(
                    photoRes = it,
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
                progressRepo.markCompleted(Routes.ORDENATUJOLASA_SCREEN)
                navController.navigate(Routes.GPS_SCREEN)
            },
            onWrongButton = {
                viewModel.resetGame()
                viewModel.dismissDialogs()
                navController.navigate(Routes.ORDENATUJOLASA_SCREEN)
            }
        )

        enlargedPhoto?.let {
            Dialog(onDismissRequest = { enlargedPhoto = null }) {
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}