package com.example.errenteriaapp.navigation.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.errenteriaapp.database.viewModel.PuzzleViewModel
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.toSize
import kotlin.math.min
import kotlin.math.roundToInt

private const val PuzzleRows = 3
private const val PuzzleCols = 3
private const val DragSlowFactor = 1f

@Composable
fun PuzzleScreen(
    onBack: () -> Unit,
    onPuzzleComplete: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: PuzzleViewModel = viewModel()
    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        if (viewModel.pieces.isEmpty()) {
            viewModel.initializePuzzle(context)
        }
    }

    val slotBounds = remember { mutableStateMapOf<Int, Rect>() }
    val loosePieceBounds = remember { mutableStateMapOf<Int, Rect>() }

    var draggingPieceId by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var dragStartPosition by remember { mutableStateOf<Offset?>(null) }
    var dragPieceSize by remember { mutableStateOf<Size?>(null) }
    var dragCenter by remember { mutableStateOf<Offset?>(null) }
    var draggingFromSlot by remember { mutableStateOf(false) }
    var originSlotIndex by remember { mutableStateOf<Int?>(null) }

    var showCompleteScreen by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.isPuzzleComplete) {
        if (viewModel.isPuzzleComplete) {
            delay(400)
            showCompleteScreen = true
        }
    }

    fun resetDragState() {
        draggingPieceId = null
        dragOffset = Offset.Zero
        dragStartPosition = null
        dragPieceSize = null
        dragCenter = null
        draggingFromSlot = false
        originSlotIndex = null
    }

    fun beginDragFromSlot(pieceId: Int, slotIndex: Int) {
        val bounds = slotBounds[slotIndex] ?: return
        draggingPieceId = pieceId
        draggingFromSlot = true
        originSlotIndex = slotIndex
        dragStartPosition = bounds.topLeft
        dragPieceSize = Size(bounds.width, bounds.height)
        dragOffset = Offset.Zero
        dragCenter = bounds.center
    }

    fun beginDragFromLoose(pieceId: Int) {
        val bounds = loosePieceBounds[pieceId] ?: return
        draggingPieceId = pieceId
        draggingFromSlot = false
        originSlotIndex = null
        dragStartPosition = bounds.topLeft
        dragPieceSize = Size(bounds.width, bounds.height)
        dragOffset = Offset.Zero
        dragCenter = bounds.center
    }

    fun updateDrag(dragAmount: Offset) {
        if (draggingPieceId == null) return
        dragOffset = dragOffset + (dragAmount * DragSlowFactor)
        val start = dragStartPosition
        val size = dragPieceSize
        if (start != null && size != null) {
            dragCenter = start + dragOffset + Offset(size.width / 2f, size.height / 2f)
        }
    }

    fun finishDrag() {
        val pieceId = draggingPieceId ?: return resetDragState()
        val center = dragCenter
        if (center != null) {
            val targetSlot = slotBounds.entries.firstOrNull { it.value.contains(center) }?.key
            if (targetSlot != null) {
                if (draggingFromSlot) {
                    originSlotIndex?.let { source ->
                        viewModel.movePieceBetweenSlots(source, targetSlot)
                    }
                } else {
                    val placed = viewModel.placePieceInSlot(pieceId, targetSlot)
                    if (placed) {
                        viewModel.onPiecePlaced(context)
                    }
                }
            }
        }
        resetDragState()
    }

    if (viewModel.isLoading.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
                Text("Puzzlea kargatzen…")
            }
        }
        return
    }

    val hoveredSlotIndex = slotBounds.entries.firstOrNull { entry ->
        val center = dragCenter
        center != null && entry.value.contains(center)
    }?.key

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        if (showCompleteScreen) {
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
                Image(
                    painter = painterResource(id = viewModel.fullPuzzleImageRes),
                    contentDescription = "Puzzlea osatuta",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .aspectRatio(1.2f)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onPuzzleComplete) {
                    Text("Jarraitu")
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_media_previous),
                            contentDescription = null
                        )
                    }
                    Text(
                        text = "Papresa Puzzlea",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.size(48.dp))
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Puzzlea", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = if (viewModel.isPuzzleComplete) "Osatuta" else "Osatzen",
                                color = if (viewModel.isPuzzleComplete) Color(0xFF2E7D32) else Color.Gray
                            )
                        }
                        Text(
                            text = "${viewModel.correctCount}/${viewModel.totalPieces}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        itemsIndexed(viewModel.slots) { index, pieceId ->
                            SlotCell(
                                slotIndex = index,
                                pieceId = pieceId,
                                isHighlighted = hoveredSlotIndex == index,
                                hidePiece = draggingFromSlot && draggingPieceId == pieceId,
                                viewModel = viewModel,
                                onSlotPositioned = { slotBounds[index] = it },
                                onDragStart = { pieceId?.let { beginDragFromSlot(it, index) } },
                                onDrag = { updateDrag(it) },
                                onDragEnd = { finishDrag() }
                            )
                        }
                    }
                }

                val loosePieces = viewModel.pieces.filter { it.currentSlot == null && it.isVisible }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (loosePieces.isEmpty()) {
                        Text(
                            text = "",
                            color = Color.Gray
                        )
                    } else {
                        loosePieces.forEach { piece ->
                            LoosePieceChip(
                                piece = piece,
                                isHidden = !draggingFromSlot && draggingPieceId == piece.id,
                                onPositioned = { loosePieceBounds[piece.id] = it },
                                onDragStart = { beginDragFromLoose(piece.id) },
                                onDrag = { updateDrag(it) },
                                onDragEnd = { finishDrag() }
                            )
                        }
                    }
                }
            }
        }

        val draggingPiece = draggingPieceId?.let { id -> viewModel.getPieceById(id) }
        val start = dragStartPosition
        val size = dragPieceSize
        if (draggingPiece != null && start != null && size != null) {
            val currentOffset = start + dragOffset
            val widthDp = with(density) { size.width.toDp() }
            val heightDp = with(density) { size.height.toDp() }
            val pieceShape = PuzzlePieceShape(
                (draggingPiece.correctSlot / PuzzleCols),
                (draggingPiece.correctSlot % PuzzleCols),
                PuzzleRows,
                PuzzleCols
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = with(density) { currentOffset.x.toDp() },
                        y = with(density) { currentOffset.y.toDp() }
                    )
                    .size(widthDp, heightDp)
                    .clip(pieceShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, pieceShape)
                    .background(Color.White, pieceShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = draggingPiece.bitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(pieceShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun SlotCell(
    slotIndex: Int,
    pieceId: Int?,
    isHighlighted: Boolean,
    hidePiece: Boolean,
    viewModel: PuzzleViewModel,
    onSlotPositioned: (Rect) -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    val piece = pieceId?.let { viewModel.getPieceById(it) }
    val isCorrect = pieceId?.let { viewModel.getPieceById(it).correctSlot == slotIndex } == true

    val borderColor = when {
        isHighlighted -> MaterialTheme.colorScheme.primary
        piece != null && isCorrect -> Color(0xFF2E7D32)
        piece != null && !isCorrect -> Color(0xFFC62828)
        else -> Color.Gray.copy(alpha = 0.4f)
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(PuzzlePieceShape(slotIndex / PuzzleCols, slotIndex % PuzzleCols, PuzzleRows, PuzzleCols))
            .border(2.dp, borderColor, PuzzlePieceShape(slotIndex / PuzzleCols, slotIndex % PuzzleCols, PuzzleRows, PuzzleCols))
            .background(Color.White)
            .pointerInput(pieceId) {
                if (pieceId != null) {
                    detectDragGestures(
                        onDragStart = { onDragStart() },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            onDrag(dragAmount)
                        },
                        onDragEnd = { onDragEnd() },
                        onDragCancel = { onDragEnd() }
                    )
                }
            }
            .onGloballyPositioned { coords ->
                onSlotPositioned(coords.boundsInRoot())
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            piece != null && !hidePiece -> {
                Image(
                    bitmap = piece.bitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(PuzzlePieceShape(slotIndex / PuzzleCols, slotIndex % PuzzleCols, PuzzleRows, PuzzleCols)),
                    contentScale = ContentScale.Crop
                )
            }
            else -> {
                Text(
                    text = (slotIndex + 1).toString(),
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LoosePieceChip(
    piece: com.example.errenteriaapp.database.viewModel.PuzzlePiece,
    isHidden: Boolean,
    onPositioned: (Rect) -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(90.dp)
            .clip(PuzzlePieceShape(piece.correctSlot / PuzzleCols, piece.correctSlot % PuzzleCols, PuzzleRows, PuzzleCols))
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = if (isHidden) 0.2f else 1f),
                shape = PuzzlePieceShape(piece.correctSlot / PuzzleCols, piece.correctSlot % PuzzleCols, PuzzleRows, PuzzleCols)
            )
            .alpha(if (isHidden) 0f else 1f)
            .background(Color.White)
            .onGloballyPositioned { coords ->
                onPositioned(coords.boundsInRoot())
            }
            .pointerInput(piece.id) {
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = piece.bitmap,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(PuzzlePieceShape(piece.correctSlot / PuzzleCols, piece.correctSlot % PuzzleCols, PuzzleRows, PuzzleCols)),
            contentScale = ContentScale.Crop
        )
    }
}

class PuzzlePieceShape(
    private val row: Int,
    private val col: Int,
    private val totalRows: Int,
    private val totalCols: Int
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val knob = min(size.width, size.height) / 4f
        val halfWidth = size.width / 2f
        val halfHeight = size.height / 2f

        val topTab = row != 0
        val bottomTab = row != totalRows - 1
        val leftTab = col != 0
        val rightTab = col != totalCols - 1

        path.moveTo(0f, if (leftTab) knob else 0f)

        // Left side
        if (leftTab) {
            path.cubicTo(
                0f,
                knob / 2f,
                -knob,
                halfHeight - knob / 2f,
                -knob,
                halfHeight
            )
            path.cubicTo(
                -knob,
                halfHeight + knob / 2f,
                0f,
                size.height - knob / 2f,
                0f,
                size.height - knob
            )
        }
        path.lineTo(0f, size.height - if (leftTab) knob else 0f)

        // Bottom side
        if (bottomTab) {
            path.lineTo(halfWidth - knob, size.height)
            path.cubicTo(
                halfWidth - knob / 2f,
                size.height,
                halfWidth - knob / 2f,
                size.height + knob,
                halfWidth,
                size.height + knob
            )
            path.cubicTo(
                halfWidth + knob / 2f,
                size.height + knob,
                halfWidth + knob / 2f,
                size.height,
                halfWidth + knob,
                size.height
            )
        }
        path.lineTo(size.width, size.height)

        // Right side
        if (rightTab) {
            path.cubicTo(
                size.width,
                size.height - knob / 2f,
                size.width + knob,
                halfHeight + knob / 2f,
                size.width + knob,
                halfHeight
            )
            path.cubicTo(
                size.width + knob,
                halfHeight - knob / 2f,
                size.width,
                knob / 2f,
                size.width,
                knob
            )
        }
        path.lineTo(size.width, 0f + if (rightTab) knob else 0f)

        // Top side
        if (topTab) {
            path.lineTo(halfWidth + knob, 0f)
            path.cubicTo(
                halfWidth + knob / 2f,
                0f,
                halfWidth + knob / 2f,
                -knob,
                halfWidth,
                -knob
            )
            path.cubicTo(
                halfWidth - knob / 2f,
                -knob,
                halfWidth - knob / 2f,
                0f,
                halfWidth - knob,
                0f
            )
        }
        path.lineTo(0f, 0f)
        path.close()

        return Outline.Generic(path)
    }
}
