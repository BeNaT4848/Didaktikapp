package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.errenteriaapp.components.DraggingImage
import com.example.errenteriaapp.components.GameResultDialogs
import com.example.errenteriaapp.database.viewModel.PuzzlePieceData
import com.example.errenteriaapp.database.viewModel.PuzzleViewModel
import kotlin.math.roundToInt

@Composable
fun PuzzleScreen(
    onBack: () -> Unit, onPuzzleComplete: () -> Unit
) {
    val viewModel: PuzzleViewModel = viewModel()

    val pieces = viewModel.pieces
    val slots = viewModel.slots
    val showSuccessDialog = viewModel.showSuccessDialog
    val showWrongDialog = viewModel.showWrongDialog

    val dropZones = remember(slots.size) {
        mutableStateListOf<Rect?>().apply { repeat(slots.size) { add(null) } }
    }
    val pieceBounds = remember(pieces.size) {
        mutableStateListOf<Rect?>().apply { repeat(pieces.size) { add(null) } }
    }
    val placedPieceBounds = remember(slots.size) {
        mutableStateListOf<Rect?>().apply { repeat(slots.size) { add(null) } }
    }

    var draggingPieceIndex by remember { mutableStateOf<Int?>(null) }
    var dragStartBounds by remember { mutableStateOf<Rect?>(null) }
    var dragOffsetPx by remember { mutableStateOf(Offset.Zero) }
    var dragCenterPx by remember { mutableStateOf<Offset?>(null) }
    var isDraggingFromSlot by remember { mutableStateOf(false) }
    var draggingSlotIndex by remember { mutableStateOf<Int?>(null) }

    // Para mostrar el puzzle completo cuando esté terminado
    val isPuzzleComplete = viewModel.isPuzzleComplete && viewModel.correctCount == pieces.size

    fun resetDragState() {
        draggingPieceIndex = null
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
                val pieceIndex = draggingPieceIndex
                if (pieceIndex != null) {
                    viewModel.assignPieceToSlot(pieces[pieceIndex].imageRes, targetIndex)
                }
            }
        }
        resetDragState()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cabecera
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Papresa Puzzlea",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            // Contador de piezas correctas
            Card(
                modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ), shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Puzzlea",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isPuzzleComplete) "Osatuta!" else "Osatu gabe",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isPuzzleComplete) Color.Green else Color.Gray
                        )
                    }

                    Text(
                        text = "${viewModel.correctCount}/${pieces.size}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Área del puzzle (grid 3x2) - MÁS GRANDE
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.5f), // Más espacio para el puzzle
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                ), shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Si el puzzle está completo, mostrar imagen completa
                    if (isPuzzleComplete) {
                        Image(
                            painter = painterResource(id = com.example.errenteriaapp.R.drawable.papresa_azalpena),
                            contentDescription = "Papresa puzzle completo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    } else {
                        // Grid de slots (3x2)
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp), // Menos espacio para que encajen
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(slots.size) { index ->
                                val isHighlighted = dropZones.getOrNull(index)?.let { rect ->
                                    dragCenterPx?.let { rect.contains(it) } == true
                                } ?: false
                                val assignedPiece = slots[index]

                                PuzzleSlot(
                                    slotIndex = index,
                                    assignedPiece = assignedPiece,
                                    isHighlighted = isHighlighted,
                                    isCorrectPosition = assignedPiece?.let {
                                        viewModel.isPieceInCorrectSlot(it, index)
                                    } ?: true,
                                    onSlotPositioned = { rect ->
                                        dropZones[index] = rect
                                        if (assignedPiece != null) {
                                            placedPieceBounds[index] = rect
                                        }
                                    },
                                    onDragStart = {
                                        placedPieceBounds.getOrNull(index)?.let { bounds ->
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
                                            dragCenterPx = bounds.topLeft + dragOffsetPx + Offset(
                                                bounds.width / 2f,
                                                bounds.height / 2f
                                            )
                                        }
                                    },
                                    onDragEnd = { handleDrop() },
                                    onDragCancel = { resetDragState() })
                            }
                        }
                    }
                }
            }

            // Piezas disponibles (grid 2x3) - SOLO si no está completo
            if (!isPuzzleComplete) {
                Text(
                    text = "Puzzle zatiak:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp), // Un poco más grande
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(pieces.size) { index ->
                        val piece = pieces[index]
                        val isUsed = slots.contains(piece.imageRes)

                        PuzzlePiece(
                            piece = piece,
                            isUsed = isUsed,
                            onPiecePositioned = { rect -> pieceBounds[index] = rect },
                            onDragStart = {
                                if (!isUsed) {
                                    draggingPieceIndex = index
                                    dragStartBounds = pieceBounds[index]
                                    dragOffsetPx = Offset.Zero
                                    dragCenterPx = pieceBounds[index]?.center
                                    isDraggingFromSlot = false
                                }
                            },
                            onDrag = { x, y ->
                                dragOffsetPx += Offset(x, y)
                                dragStartBounds?.let { bounds ->
                                    dragCenterPx = bounds.topLeft + dragOffsetPx + Offset(
                                        bounds.width / 2f,
                                        bounds.height / 2f
                                    )
                                }
                            },
                            onDragEnd = { handleDrop() },
                            onDragCancel = { resetDragState() })
                    }
                }
            }

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.resetGame() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Berrabiarazi", fontSize = 16.sp)
                }

                Button(
                    onClick = {
                        if (isPuzzleComplete) {
                            onPuzzleComplete()
                        } else {
                            viewModel.checkPuzzleCompletion()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = isPuzzleComplete || viewModel.isPuzzleComplete
                ) {
                    Text(
                        text = when {
                            isPuzzleComplete -> "Jarraitu"
                            viewModel.isPuzzleComplete -> "Egiaztatu"
                            else -> "Osatu puzzlea"
                        }, fontSize = 16.sp
                    )
                }
            }
        }

        // Imagen arrastrándose
        if ((draggingPieceIndex != null || draggingSlotIndex != null) && dragStartBounds != null) {
            val imageRes = when {
                isDraggingFromSlot && draggingSlotIndex != null -> slots[draggingSlotIndex!!]
                !isDraggingFromSlot && draggingPieceIndex != null -> pieces[draggingPieceIndex!!].imageRes
                else -> null
            }

            if (imageRes != null) {
                val bounds = dragStartBounds!!
                val widthDp =
                    with(LocalDensity.current) { bounds.width.toDp() } * 1.1f // Un poco más grande
                val heightDp = with(LocalDensity.current) { bounds.height.toDp() } * 1.1f

                DraggingImage(
                    photoRes = imageRes,
                    boundsTopLeft = bounds.topLeft,
                    dragOffsetPx = dragOffsetPx,
                    widthDp = widthDp,
                    heightDp = heightDp,
                    modifier = Modifier.zIndex(1f)
                )
            }
        }

        // Diálogos de resultado
        GameResultDialogs(
            showSuccess = showSuccessDialog,
            showWrong = showWrongDialog,
            onDismissSuccess = { viewModel.dismissDialogs() },
            onDismissWrong = { viewModel.dismissDialogs() },
            onSuccessButton = {
                viewModel.dismissDialogs()
                onPuzzleComplete()
            },
            onWrongButton = {
                viewModel.resetGame()
                viewModel.dismissDialogs()
            })
    }
}

@Composable
fun PuzzlePiece(
    piece: PuzzlePieceData,
    isUsed: Boolean,
    onPiecePositioned: (Rect) -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isUsed) 1.dp else 2.dp,
                color = if (isUsed) Color.Gray else MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            )
            .onGloballyPositioned { coords ->
                onPiecePositioned(coords.boundsInRoot())
            }
            .pointerInput(piece.id) {
                detectDragGestures(
                    onDragStart = { onDragStart() },
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
        // Contenedor con el mismo fondo blanco que la imagen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = piece.imageRes),
                contentDescription = "Puzzle pieza ${piece.id}",
                contentScale = ContentScale.Fit, // Ajustar manteniendo proporción
                modifier = Modifier
                    .fillMaxSize(0.9f) // Un poco más pequeño para que no toque bordes
            )
        }
    }
}

@Composable
fun PuzzleSlot(
    slotIndex: Int,
    assignedPiece: Int?,
    isHighlighted: Boolean,
    isCorrectPosition: Boolean,
    onSlotPositioned: (Rect) -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = when {
                    isHighlighted -> 3.dp
                    assignedPiece != null -> 1.dp // Borde sutil
                    else -> 1.dp
                },
                color = when {
                    isHighlighted -> MaterialTheme.colorScheme.primary
                    assignedPiece != null -> Color.LightGray // Borde gris claro
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                },
                shape = RoundedCornerShape(8.dp)
            )
            .onGloballyPositioned { coords ->
                onSlotPositioned(coords.boundsInRoot())
            }
            .clickable(enabled = assignedPiece != null) {
                if (assignedPiece != null) onDragStart()
            }
            .pointerInput(slotIndex) {
                if (assignedPiece != null) {
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
            },
        contentAlignment = Alignment.Center
    ) {
        if (assignedPiece != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = assignedPiece),
                    contentDescription = "Pieza colocada",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize(0.9f)
                )
            }
        }
    }
}