package com.example.errenteriaapp.navigation.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.errenteriaapp.database.viewModel.PuzzleViewModel
import kotlinx.coroutines.delay

@Composable
fun PuzzleScreen(
    onBack: () -> Unit, onPuzzleComplete: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: PuzzleViewModel = viewModel()

    LaunchedEffect(Unit) {
        if (viewModel.pieces.isEmpty()) {
            viewModel.initializePuzzle(context)
        }
    }

    var selectedPieceId by remember { mutableStateOf<Int?>(null) }
    var selectedSlotIndex by remember { mutableStateOf<Int?>(null) }
    var showCompleteScreen by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.isPuzzleComplete) {
        if (viewModel.isPuzzleComplete) {
            delay(400)
            showCompleteScreen = true
        }
    }

    fun clearSelection() {
        selectedPieceId = null
        selectedSlotIndex = null
    }

    fun handlePieceSelection(pieceId: Int, slotIndex: Int?) {
        if (selectedPieceId == null) {
            selectedPieceId = pieceId
            selectedSlotIndex = slotIndex
            return
        }

        val targetSlot = slotIndex ?: return
        val currentSelection = selectedPieceId ?: return

        val placed = viewModel.placePieceInSlot(currentSelection, targetSlot)
        if (placed) {
            if (selectedSlotIndex == null) {
                viewModel.onPiecePlaced(context)
            }
            clearSelection()
        }
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
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        MaterialTheme.colorScheme.surfaceVariant.copy(
                            alpha = 0.2f
                        )
                    )
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
                                isSelected = selectedSlotIndex == index,
                                viewModel = viewModel,
                                onSelect = {
                                    pieceId?.let { handlePieceSelection(it, index) }
                                },
                                onDropTarget = {
                                    selectedPieceId?.let { handlePieceSelection(it, index) }
                                })
                        }
                    }
                }

                val loosePieces = viewModel.pieces.filter { it.currentSlot == null && it.isVisible }
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    loosePieces.forEach { piece ->
                        val isSelected = selectedPieceId == piece.id && selectedSlotIndex == null
                        LoosePieceChip(
                            piece = piece, isSelected = isSelected, onClick = {
                                if (isSelected) {
                                    clearSelection()
                                } else {
                                    selectedPieceId = piece.id
                                    selectedSlotIndex = null
                                }
                            })
                    }
                }
            }
        }
    }
}

@Composable
private fun SlotCell(
    slotIndex: Int,
    pieceId: Int?,
    isSelected: Boolean,
    viewModel: PuzzleViewModel,
    onSelect: () -> Unit,
    onDropTarget: () -> Unit
) {
    val piece = pieceId?.let { viewModel.getPieceById(it) }
    val isCorrect = pieceId?.let { viewModel.getPieceById(it).correctSlot == slotIndex } == true

    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        piece != null && isCorrect -> Color(0xFF2E7D32)
        piece != null && !isCorrect -> Color(0xFFC62828)
        else -> Color.Gray.copy(alpha = 0.4f)
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .border(2.dp, borderColor, RoundedCornerShape(10.dp))
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else -> Color.White
                }
            )
            .clickable {
                if (piece != null) {
                    onSelect()
                } else {
                    onDropTarget()
                }
            }, contentAlignment = Alignment.Center
    ) {
        if (piece != null) {
            Image(
                bitmap = piece.bitmap,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

        } else {
            Text(
                text = (slotIndex + 1).toString(), color = Color.Gray, fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LoosePieceChip(
    piece: com.example.errenteriaapp.database.viewModel.PuzzlePiece,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(90.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 3.dp else 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                shape = RoundedCornerShape(12.dp)
            )
            .background(Color.White)
            .clickable { onClick() }, contentAlignment = Alignment.Center
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