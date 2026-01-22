package com.example.errenteriaapp.navigation.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.layout.boundsInRoot
import com.example.errenteriaapp.database.viewModel.PuzzleViewModel
import kotlinx.coroutines.delay

@Composable
fun PuzzleScreen(
    onBack: () -> Unit,
    userName: String?,
    onPuzzleComplete: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: PuzzleViewModel = viewModel()
    val density = LocalDensity.current

    // Inicializar puzzle
    LaunchedEffect(Unit) {
        if (viewModel.pieces.isEmpty()) {
            viewModel.initializePuzzle(context)
        }
    }

    // Estados para arrastre
    var draggingPieceId by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    // Posiciones de los slots
    val slotBounds = remember { mutableMapOf<Int, Rect>() }

    var showCompleteScreen by remember { mutableStateOf(false) }

    // Mostrar pantalla de completado
    LaunchedEffect(viewModel.isPuzzleComplete) {
        if (viewModel.isPuzzleComplete) {
            delay(500)
            showCompleteScreen = true
        }
    }

    // Pantalla de carga
    if (viewModel.isLoading.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator()
                Text("Puzzlea kargatzen...", color = MaterialTheme.colorScheme.primary)
            }
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        if (showCompleteScreen) {
            // PANTALLA DE COMPLETADO
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Puzzlea osatuta!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))

                // IMAGEN CENTRADA
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = viewModel.fullPuzzleImageRes),
                        contentDescription = "Puzzlea osatuta",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onPuzzleComplete() },
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("Jarraitu", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        } else {
            // PANTALLA DE JUEGO
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // CABECERA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_revert),
                            contentDescription = "Atzera"
                        )
                    }

                    Text(
                        text = "Papresa Puzzlea",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Box(modifier = Modifier.size(48.dp))
                }

                // CONTADOR
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
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
                                text = if (viewModel.isPuzzleComplete) "Osatuta!" else "Osatzen...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (viewModel.isPuzzleComplete) Color.Green else Color.Gray
                            )
                        }

                        Text(
                            text = "${viewModel.correctCount}/${viewModel.totalPieces}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // CUADRICULA DE SLOTS
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            itemsIndexed(viewModel.slots) { index, pieceId ->
                                SlotComposable(
                                    slotIndex = index,
                                    pieceId = pieceId,
                                    viewModel = viewModel,
                                    onSlotPositioned = { bounds ->
                                        slotBounds[index] = bounds
                                    }
                                )
                            }
                        }
                    }
                }

               // ESPACIO PARA LA(S) PIEZA(S) SUELTA(S)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.4f)
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.getVisiblePieces().isEmpty()) {
                        Text(
                            text = "",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // RENDERIZAR PIEZAS SUELTAS (abajo del todo)
            val loosePieces = viewModel.pieces.filter {
                it.currentSlot == null && it.isVisible
            }

            loosePieces.forEach { piece ->
                val isDragging = draggingPieceId == piece.id

                SlowDraggablePiece(
                    piece = piece,
                    isDragging = isDragging,
                    dragOffset = if (isDragging) dragOffset else Offset.Zero,
                    onDragStart = {
                        draggingPieceId = piece.id
                        dragOffset = Offset.Zero
                    },
                    onDrag = { dragAmount ->
                        if (draggingPieceId == piece.id) {
                            dragOffset += dragAmount
                            // Actualizar posición en tiempo real
                            viewModel.updatePiecePosition(
                                piece.id,
                                piece.offsetX + dragOffset.x,
                                piece.offsetY + dragOffset.y
                            )
                        }
                    },
                    onDragEnd = {
                        if (draggingPieceId == piece.id) {
                            val currentPiece = viewModel.getPieceById(piece.id)
                            val pieceSizePx = with(density) { 100.dp.toPx() }

                            val centerX = currentPiece.offsetX + dragOffset.x + pieceSizePx / 2
                            val centerY = currentPiece.offsetY + dragOffset.y + pieceSizePx / 2

                            // Buscar slot donde cayó
                            var targetSlot: Int? = null
                            slotBounds.forEach { (slotIndex, bounds) ->
                                val dropPoint = Offset(centerX, centerY)
                                if (bounds.contains(dropPoint)) {
                                    targetSlot = slotIndex
                                }
                            }

                            if (targetSlot != null) {
                                // Intentar colocar o intercambiar
                                val success = viewModel.placePieceInSlot(piece.id, targetSlot)
                                if (success) {
                                    // Si se colocó con éxito, mostrar siguiente pieza
                                    viewModel.onPiecePlaced(context)
                                }
                            } else {
                                // Si no cayó en un slot, mantener posición actual
                                viewModel.updatePiecePosition(
                                    piece.id,
                                    currentPiece.offsetX + dragOffset.x,
                                    currentPiece.offsetY + dragOffset.y
                                )
                            }

                            draggingPieceId = null
                            dragOffset = Offset.Zero
                        }
                    }
                )
            }

            // RENDERIZAR PIEZAS COLOCADAS QUE SE ESTÁN ARRASTRANDO
            val draggingPlacedPiece = viewModel.pieces.firstOrNull {
                it.currentSlot != null && draggingPieceId == it.id
            }

            draggingPlacedPiece?.let { piece ->
                SlowDraggablePiece(
                    piece = piece,
                    isDragging = true,
                    dragOffset = dragOffset,
                    onDragStart = {
                        // El arrastre ya empezó
                    },
                    onDrag = { dragAmount ->
                        if (draggingPieceId == piece.id) {
                            dragOffset += dragAmount
                            // Actualizar posición en tiempo real
                            viewModel.updatePiecePosition(
                                piece.id,
                                piece.offsetX + dragOffset.x,
                                piece.offsetY + dragOffset.y
                            )
                        }
                    },
                    onDragEnd = {
                        if (draggingPieceId == piece.id) {
                            val currentPiece = viewModel.getPieceById(piece.id)
                            val pieceSizePx = with(density) { 100.dp.toPx() }

                            val centerX = currentPiece.offsetX + dragOffset.x + pieceSizePx / 2
                            val centerY = currentPiece.offsetY + dragOffset.y + pieceSizePx / 2

                            // Buscar slot donde cayó
                            var targetSlot: Int? = null
                            slotBounds.forEach { (slotIndex, bounds) ->
                                val dropPoint = Offset(centerX, centerY)
                                if (bounds.contains(dropPoint)) {
                                    targetSlot = slotIndex
                                }
                            }

                            if (targetSlot != null) {
                                // Intentar colocar o intercambiar
                                val success = viewModel.placePieceInSlot(piece.id, targetSlot)
                                if (!success) {
                                    // Si no se pudo intercambiar, volver al slot original
                                    // Primero restaurar en el slot original
                                    val originalSlot = piece.currentSlot
                                    if (originalSlot != null) {
                                        viewModel.placePieceInSlot(piece.id, originalSlot)
                                    }
                                }
                            } else {
                                // Si no cayó en un slot, volver al slot original
                                val originalSlot = piece.currentSlot
                                if (originalSlot != null) {
                                    viewModel.placePieceInSlot(piece.id, originalSlot)
                                }
                            }

                            draggingPieceId = null
                            dragOffset = Offset.Zero
                        }
                    }
                )
            }
        }
    }
}

// COMPOSABLE PARA SLOT (sin gestos de tap)
@Composable
fun SlotComposable(
    slotIndex: Int,
    pieceId: Int?,
    viewModel: PuzzleViewModel,
    onSlotPositioned: (Rect) -> Unit
) {
    val piece = pieceId?.let { viewModel.getPieceById(it) }
    val isCorrect = pieceId?.let { viewModel.getPieceById(it).correctSlot == slotIndex } ?: false

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 2.dp,
                color = when {
                    piece != null && isCorrect -> Color.Green
                    piece != null && !isCorrect -> Color.Red
                    else -> Color.Gray.copy(alpha = 0.5f)
                },
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = when {
                    piece != null && isCorrect -> Color.Green.copy(alpha = 0.1f)
                    piece != null && !isCorrect -> Color.Red.copy(alpha = 0.1f)
                    else -> Color.LightGray.copy(alpha = 0.2f)
                },
                shape = RoundedCornerShape(8.dp)
            )
            .onGloballyPositioned { coords ->
                val bounds = coords.boundsInRoot()
                onSlotPositioned(bounds)
            }
            .pointerInput(slotIndex) {
                // Gesto para iniciar arrastre desde una pieza colocada
                if (pieceId != null) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            // Cuando empiezan a arrastrar una pieza colocada
                            // No hacemos nada aquí, el drag se maneja en el composable principal
                        },
                        onDrag = { change, dragAmount ->
                            // Consumir el evento pero no hacer nada
                            change.consume()
                        },
                        onDragEnd = {},
                        onDragCancel = {}
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (piece != null) {
            Image(
                bitmap = piece.bitmap,
                contentDescription = "Pieza en slot $slotIndex",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .clip(RoundedCornerShape(6.dp))
            )

            // Indicador de corrección
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isCorrect) Color.Green else Color.Red)
                    .border(1.dp, Color.White, RoundedCornerShape(6.dp))
            )
        } else {
            // Número del slot
            Text(
                text = (slotIndex + 1).toString(),
                color = Color.Gray.copy(alpha = 0.8f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// COMPOSABLE PARA PIEZA ARRASTRABLE
@Composable
fun SlowDraggablePiece(
    piece: com.example.errenteriaapp.database.viewModel.PuzzlePiece,
    isDragging: Boolean,
    dragOffset: Offset,
    onDragStart: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    val density = LocalDensity.current

    // Calcular posición
    val displayX = piece.offsetX + if (isDragging) dragOffset.x else 0f
    val displayY = piece.offsetY + if (isDragging) dragOffset.y else 0f

    Box(
        modifier = Modifier
            .offset(
                x = with(density) { displayX.toDp() },
                y = with(density) { displayY.toDp() }
            )
            .size(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isDragging) 3.dp else 2.dp,
                color = if (isDragging) MaterialTheme.colorScheme.primary else Color.Gray,
                shape = RoundedCornerShape(12.dp)
            )
            .shadow(
                elevation = if (isDragging) 8.dp else 4.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .background(Color.White, RoundedCornerShape(12.dp))
            .pointerInput(piece.id) {
                detectDragGestures(
                    onDragStart = { offset: Offset ->
                        onDragStart()
                    },
                    onDrag = { change: PointerInputChange, dragAmount: Offset ->
                        change.consume()

                        // REDUCIR VELOCIDAD
                        val slowFactor = 0.6f
                        val slowDragAmount = Offset(
                            x = dragAmount.x * slowFactor,
                            y = dragAmount.y * slowFactor
                        )

                        onDrag(slowDragAmount)
                    },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = piece.bitmap,
            contentDescription = "Puzzle pieza ${piece.id + 1}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .clip(RoundedCornerShape(10.dp))
        )
    }
}