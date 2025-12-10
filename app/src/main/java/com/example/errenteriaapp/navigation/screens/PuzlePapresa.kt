package com.example.errenteriaapp.navigation.screens

import androidx.compose.ui.graphics.Brush
import kotlinx.coroutines.delay



import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.errenteriaapp.R
import kotlinx.coroutines.launch
import kotlin.math.sqrt

data class PieceState(
    val id: Int,
    val imageRes: Int,
    var position: Int? = null, // null = en el banco
    var isCorrect: Boolean = false,
    var isLocked: Boolean = false,
    val correctPosition: Int = id
)
@Composable
fun PuzleScreen(navController: NavController) {

    var pieces by remember {
        mutableStateOf(
            listOf(
                PieceState(0, R.drawable.parte1),
                PieceState(1, R.drawable.parte2),
                PieceState(2, R.drawable.parte3),
                PieceState(3, R.drawable.parte4)
            )
        )
    }

    var isComplete by remember { mutableStateOf(false) }
    var showZoom by remember { mutableStateOf(false) }
    var movesCount by remember { mutableStateOf(0) }

    // Cuando sueltas una pieza sobre una casilla concreta (0..3)
    fun handleDropOnSlot(pieceId: Int, slotIndex: Int) {
        val piece = pieces.find { it.id == pieceId } ?: return
        if (piece.isLocked) return

        movesCount++

        pieces = pieces.map { p ->
            when {
                p.id == pieceId -> {
                    val isCorrect = p.correctPosition == slotIndex
                    p.copy(
                        position = slotIndex,
                        isCorrect = isCorrect,
                        isLocked = isCorrect
                    )
                }
                // Si otra pieza estaba en esa casilla y no está bloqueada, vuelve al banco
                p.position == slotIndex && !p.isLocked -> {
                    p.copy(position = null, isCorrect = false, isLocked = false)
                }
                else -> p
            }
        }

        val allCorrect = pieces.all { it.isCorrect && it.isLocked }
        if (allCorrect) {
            isComplete = true
        }
    }

    fun returnToBank(pieceId: Int) {
        pieces = pieces.map { p ->
            if (p.id == pieceId && !p.isLocked) {
                p.copy(position = null, isCorrect = false, isLocked = false)
            } else p
        }
        movesCount++
    }

    fun resetGame() {
        pieces = listOf(
            PieceState(0, R.drawable.parte1),
            PieceState(1, R.drawable.parte2),
            PieceState(2, R.drawable.parte3),
            PieceState(3, R.drawable.parte4)
        )
        isComplete = false
        showZoom = false
        movesCount = 0
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Puzzlea bete",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Text(
                text = "Mugimenduak: $movesCount",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!isComplete) {
                        // BANCO ARRIBA
                        Text(
                            text = "Arrastatu piezak",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            pieces.forEach { piece ->
                                if (piece.position == null) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                    ) {
                                        DraggablePieceOverSlots(
                                            piece = piece,
                                            onDropOnSlot = { slotIndex ->
                                                handleDropOnSlot(piece.id, slotIndex)
                                            }
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // GRID ABAJO
                        Text(
                            text = "Jarri hemen",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        PuzzleSlots(
                            pieces = pieces,
                            onReturn = { returnToBank(it) }
                        )

                        Button(
                            onClick = { resetGame() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B7280)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Berrabiarazi", fontSize = 16.sp)
                        }
                    } else {
                        // IMAGEN FINAL (papresa_azalpena / puzzle completo)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clickable { showZoom = true }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.papresa_azalpena),
                                contentDescription = "Puzzle osatua",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(4.dp, Color(0xFF22C55E), RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // BOTÓN HASI JOLAZAGAZ
                        Button(
                            onClick = { navController.navigate("home") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Hasi jolazagaz", fontSize = 18.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { resetGame() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Berriro jokatu", fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        // ZOOM DE LA IMAGEN FINAL
        if (showZoom) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable { showZoom = false },
                contentAlignment = Alignment.Center
            ) {
                val scale by animateFloatAsState(
                    targetValue = if (showZoom) 1.2f else 1f,
                    animationSpec = tween(durationMillis = 250),
                    label = ""
                )
                Image(
                    painter = painterResource(id = R.drawable.papresa_azalpena),
                    contentDescription = "Puzzle handitua",
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .aspectRatio(1f)
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .clickable { showZoom = false },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
@Composable
fun DraggablePieceOverSlots(
    piece: PieceState,
    onDropOnSlot: (Int) -> Unit
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    // El área donde imaginariamente está el grid 2x2 (debajo).
    // Para simplificar, consideramos un grid virtual de 2x2 de tamaño 2x2 en unidades relativas.
    val rows = 2
    val cols = 2

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                if (!piece.isLocked) {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            offset += dragAmount
                        },
                        onDragEnd = {
                            isDragging = false

                            // Mapear offset a índice de casilla: arriba-izq, arriba-der, abajo-izq, abajo-der
                            // Aquí puedes afinar según tu diseño real:
                            val slotIndex = when {
                                offset.y < -150f && offset.x < 0f -> 0 // arriba izq
                                offset.y < -150f && offset.x >= 0f -> 1 // arriba der
                                offset.y >= -150f && offset.x < 0f -> 2 // abajo izq
                                offset.y >= -150f && offset.x >= 0f -> 3 // abajo der
                                else -> null
                            }

                            slotIndex?.let { onDropOnSlot(it) }
                            offset = Offset.Zero
                        },
                        onDragCancel = {
                            isDragging = false
                            offset = Offset.Zero
                        }
                    )
                }
            }
            .offset { IntOffset(offset.x.toInt(), offset.y.toInt()) }
            .scale(if (isDragging) 0.95f else 1f)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 2.dp,
                color = if (isDragging) Color(0xFF3B82F6) else Color(0xFFD1D5DB),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Image(
            painter = painterResource(id = piece.imageRes),
            contentDescription = "Puzzle pieza ${piece.id + 1}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
@Composable
fun PuzzleSlots(
    pieces: List<PieceState>,
    onReturn: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color(0xFFFEF3C7), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DropZone(position = 0, pieces = pieces, onReturn = onReturn)
            DropZone(position = 1, pieces = pieces, onReturn = onReturn)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DropZone(position = 2, pieces = pieces, onReturn = onReturn)
            DropZone(position = 3, pieces = pieces, onReturn = onReturn)
        }
    }
}
@Composable
fun RowScope.DropZone(
    position: Int,
    pieces: List<PieceState>,
    onReturn: (Int) -> Unit
) {
    // ¿Hay pieza en esta casilla?
    val piece = pieces.find { it.position == position }
    val isCorrect = piece?.isCorrect == true
    val isLocked = piece?.isLocked == true

    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 3.dp,
                color = when {
                    isCorrect && isLocked -> Color(0xFF22C55E)   // verde correcto
                    piece != null && !isCorrect -> Color(0xFFEF4444) // rojo incorrecto
                    else -> Color(0xFFFBBF24)                    // amarillo vacío
                },
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                when {
                    isCorrect && isLocked ->
                        Color(0xFF22C55E).copy(alpha = 0.1f)
                    piece != null && !isCorrect ->
                        Color(0xFFEF4444).copy(alpha = 0.05f)
                    else ->
                        Color.White.copy(alpha = 0.6f)
                }
            )
    ) {
        // Número de casilla (1–4)
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(4.dp, 4.dp)
                .size(20.dp)
                .background(Color(0xFF374151).copy(alpha = 0.7f), CircleShape)
        ) {
            Text(
                text = "${position + 1}",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        piece?.let { p ->
            // Imagen de la pieza puesta en la casilla
            Image(
                painter = painterResource(id = p.imageRes),
                contentDescription = "Pieza ${p.id + 1} kokaturik",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Overlay de OK cuando está bien y bloqueada
            if (isCorrect && isLocked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF22C55E).copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center)
                            .background(Color(0xFF22C55E), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Zuzena",
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(20.dp)
                        )
                    }
                }
            }

            // Botón para devolver al banco si está mal (rojo) y no está bloqueada
            if (!isLocked) {
                IconButton(
                    onClick = { onReturn(p.id) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = (-4).dp)
                        .size(28.dp)
                        .background(Color(0xFFEF4444), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Kendu",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        } ?: run {
            // Texto cuando está vacío
            Text(
                text = "Hutsik",
                fontSize = 12.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

