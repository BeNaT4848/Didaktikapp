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
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.errenteriaapp.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.errenteriaapp.database.viewModel.PuzzleViewModel
import kotlinx.coroutines.delay

private const val DragSlowFactor = 1f // Arrastearen abiadura faktorea

/**
 * Puzzle jokoaren pantaila nagusia.
 * Puzzle piezak arrastatu eta jartzeko jokoa.
 *
 * @param onBack Atzera botoiaren ekintza
 * @param userName Erabiltzaile izena (hautazkoa)
 * @param onPuzzleComplete Puzzlea osatutakoan exekutatuko den ekintza
 *
 * @see PuzzleViewModel
 * @see SlotCell
 * @see LoosePieceChip
 */
@Composable
fun PuzzleScreen(
    onBack: () -> Unit,
    userName: String?,
    onPuzzleComplete: () -> Unit
) {
    // Testuingurua eta ViewModel-a lortu
    val context = LocalContext.current
    val viewModel: PuzzleViewModel = viewModel()
    val density = LocalDensity.current

    // Puzzlea hasieratu beharrezkoa bada
    LaunchedEffect(Unit) {
        if (viewModel.pieces.isEmpty()) {
            viewModel.initializePuzzle(context)
        }
    }

    // Kokapen mugak gordetzeko mapa
    val slotBounds = remember { mutableStateMapOf<Int, Rect>() }
    // Libre dauden piezen mugak gordetzeko mapa
    val loosePieceBounds = remember { mutableStateMapOf<Int, Rect>() }

    // Arraste-egoera aldagaiak
    var draggingPieceId by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var dragStartPosition by remember { mutableStateOf<Offset?>(null) }
    var dragPieceSize by remember { mutableStateOf<Size?>(null) }
    var dragCenter by remember { mutableStateOf<Offset?>(null) }
    var draggingFromSlot by remember { mutableStateOf(false) }
    var originSlotIndex by remember { mutableStateOf<Int?>(null) }

    var showCompleteScreen by remember { mutableStateOf(false) }

    // Puzzlea osatu denean pantaila aldatu
    LaunchedEffect(viewModel.isPuzzleComplete) {
        if (viewModel.isPuzzleComplete) {
            delay(400)
            showCompleteScreen = true
        }
    }

    /**
     * Arraste-egoera berrezarri.
     */
    fun resetDragState() {
        draggingPieceId = null
        dragOffset = Offset.Zero
        dragStartPosition = null
        dragPieceSize = null
        dragCenter = null
        draggingFromSlot = false
        originSlotIndex = null
    }

    /**
     * Arrastatzea hasi kokapen batetik.
     *
     * @param pieceId Pieza IDa
     * @param slotIndex Kokapen indizea
     */
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

    /**
     * Arrastatzea hasi pieza libre batetik.
     *
     * @param pieceId Pieza IDa
     */
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

    /**
     * Arrastea eguneratu.
     *
     * @param dragAmount Arraste kopurua
     */
    fun updateDrag(dragAmount: Offset) {
        if (draggingPieceId == null) return
        dragOffset = dragOffset + (dragAmount * DragSlowFactor)
        val start = dragStartPosition
        val size = dragPieceSize
        if (start != null && size != null) {
            dragCenter = start + dragOffset + Offset(size.width / 2f, size.height / 2f)
        }
    }

    /**
     * Arrastea amaitu.
     */
    fun finishDrag() {
        val pieceId = draggingPieceId ?: return resetDragState()
        val center = dragCenter
        if (center != null) {
            // Zein kokapenetara askatu den begiratu
            val targetSlot = slotBounds.entries.firstOrNull { it.value.contains(center) }?.key
            if (targetSlot != null) {
                if (draggingFromSlot) {
                    // Kokapen batetik bestera arrastatu
                    originSlotIndex?.let { source ->
                        viewModel.movePieceBetweenSlots(source, targetSlot)
                    }
                } else {
                    // Pieza libre bat kokapenera arrastatu
                    val placed = viewModel.placePieceInSlot(pieceId, targetSlot)
                    if (placed) {
                        viewModel.onPiecePlaced(context)
                    }
                }
            }
        }
        resetDragState()
    }

    // Kargatzen ari bada, kargaketa pantaila erakutsi
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
                Text(stringResource(R.string.papresa_puzzle_loading))
            }
        }
        return
    }

    // Gainean dagoen kokapena detektatu
    val hoveredSlotIndex = slotBounds.entries.firstOrNull { entry ->
        val center = dragCenter
        center != null && entry.value.contains(center)
    }?.key

    // Pantaila nagusia
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Puzzlea osatutako pantaila
        if (showCompleteScreen) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.papresa_puzzle_complete),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Image(
                    painter = painterResource(id = viewModel.fullPuzzleImageRes),
                    contentDescription = stringResource(R.string.papresa_puzzle_complete_cd),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .aspectRatio(1.2f)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onPuzzleComplete) {
                    Text(stringResource(R.string.papresa_puzzle_continue), color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        } else {
            // Joko pantaila nagusia
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Goiko txartela (egoera eta puntuazioa)
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
                            Text(
                                text = stringResource(R.string.papresa_puzzle_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (viewModel.isPuzzleComplete) {
                                    stringResource(R.string.papresa_puzzle_status_done)
                                } else {
                                    stringResource(R.string.papresa_puzzle_status_doing)
                                },
                                color = if (viewModel.isPuzzleComplete) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${viewModel.correctCount}/${viewModel.totalPieces}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Puzzle kokapenak (sarea)
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

                // Libre dauden piezen eremua
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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

        // Arrastean dagoen pieza erakutsi
        val draggingPiece = draggingPieceId?.let { id -> viewModel.getPieceById(id) }
        val start = dragStartPosition
        val size = dragPieceSize
        if (draggingPiece != null && start != null && size != null) {
            val currentOffset = start + dragOffset
            val widthDp = with(density) { size.width.toDp() }
            val heightDp = with(density) { size.height.toDp() }

            Box(
                modifier = Modifier
                    .offset(
                        x = with(density) { currentOffset.x.toDp() },
                        y = with(density) { currentOffset.y.toDp() }
                    )
                    .size(widthDp, heightDp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = draggingPiece.bitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

/**
 * Puzzle kokapen baten gelaxka.
 *
 * @param slotIndex Kokapen indizea
 * @param pieceId Pieza IDa (hautazkoa)
 * @param isHighlighted Gainean dagoen ala ez
 * @param hidePiece Pieza ezkutatu behar den (arrastean dagoenean)
 * @param viewModel Puzzle ViewModel
 * @param onSlotPositioned Kokapena posizionatutakoan
 * @param onDragStart Arrastatzea hasitakoan
 * @param onDrag Arrastean dagoenean
 * @param onDragEnd Arrastea amaitutakoan
 */
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

    // Ertzeko kolorea egoeraren arabera
    val borderColor = when {
        isHighlighted -> MaterialTheme.colorScheme.primary
        piece != null && isCorrect -> MaterialTheme.colorScheme.tertiary
        piece != null && !isCorrect -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f) // Karratu formakoa
            .clip(RoundedCornerShape(10.dp))
            .border(2.dp, borderColor, RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
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
                // Pieza erakutsi
                Image(
                    bitmap = piece.bitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            else -> {
                // Kokapen zenbakia erakutsi (pieza ezean)
                Text(
                    text = (slotIndex + 1).toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Libre dagoen puzzle pieza baten txipa.
 *
 * @param piece Puzzle pieza
 * @param isHidden Ezkutatuta dagoen ala ez (arrastean dagoenean)
 * @param onPositioned Posizionatutakoan
 * @param onDragStart Arrastatzea hasitakoan
 * @param onDrag Arrastean dagoenean
 * @param onDragEnd Arrastea amaitutakoan
 */
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
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = if (isHidden) 0.2f else 1f),
                shape = RoundedCornerShape(12.dp)
            )
            .alpha(if (isHidden) 0f else 1f) // Arrastean dagoenean ezkutatu
            .background(MaterialTheme.colorScheme.surface)
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
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )
    }
}