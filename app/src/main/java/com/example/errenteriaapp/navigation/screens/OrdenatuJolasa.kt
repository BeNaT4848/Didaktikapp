package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.errenteriaapp.R
import com.example.errenteriaapp.navigation.Routes
import kotlin.math.roundToInt
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

    val photos = remember {
        photoNumberMap.keys.toList().shuffled() // Mezclar las fotos
    }

    GameScreen(
        navController = navController,
        photos = photos,
        photoNumberMap = photoNumberMap,
        modifier = modifier
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameScreen(
    navController: NavController,
    photos: List<Int>,
    photoNumberMap: Map<Int, Int>,
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

    // Contar cuántas fotos están en la posición correcta
    val correctCount by remember(slotAssignments) {
        derivedStateOf {
            slotAssignments.withIndex().count { (slotIndex, photoRes) ->
                if (photoRes == null) false
                else {
                    val expectedNumber = slotIndex + 1
                    val photoNumber = photoNumberMap[photoRes]
                    photoNumber == expectedNumber
                }
            }
        }
    }

    // Función para verificar si una foto está en el slot correcto
    fun isPhotoInCorrectSlot(photoRes: Int, slotIndex: Int): Boolean {
        val expectedNumber = slotIndex + 1
        val photoNumber = photoNumberMap[photoRes]
        return photoNumber == expectedNumber
    }

    LaunchedEffect(isComplete) {
        if (isComplete) {
            if (correctCount >= 3) {
                // 3 o más fotos correctas: mostrar éxito
                showSuccessDialog = true
            } else {
                // Menos de 3 fotos correctas: mostrar "lo has hecho mal"
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
        val dropPoint = dragCenterPx
        if (dropPoint == null) {
            resetDragState()
            return
        }

        val targetIndex = dropZones.indexOfFirst { rect -> rect?.contains(dropPoint) == true }

        if (targetIndex != -1) {
            if (isDraggingFromSlot) {
                // Estamos moviendo una foto desde un slot
                val sourceSlot = draggingSlotIndex
                if (sourceSlot != null && sourceSlot != targetIndex) {
                    // Mover la foto de un slot a otro
                    val photoRes = slotAssignments[sourceSlot]
                    if (photoRes != null) {
                        val currentInTarget = slotAssignments[targetIndex]
                        slotAssignments[sourceSlot] = currentInTarget
                        slotAssignments[targetIndex] = photoRes
                    }
                }
            } else {
                // Estamos arrastrando desde la lista de fotos
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
                        contentDescription = "Foto ${photoNumberMap[photoRes] ?: (index + 1)}",
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
                                detectDragGestures(
                                    onDragStart = {
                                        val bounds = photoBounds.getOrNull(index)
                                        if (bounds != null && !slotAssignments.contains(photoRes)) {
                                            draggingPhotoIndex = index
                                            dragStartBounds = bounds
                                            dragOffsetPx = Offset.Zero
                                            dragCenterPx = bounds.center
                                            isDraggingFromSlot = false
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
                    val isCorrectPosition = assignedPhoto?.let {
                        isPhotoInCorrectSlot(it, index)
                    } ?: true

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                when {
                                    isHighlighted -> Color(0xFFFFF176)
                                    assignedPhoto != null && !isCorrectPosition -> Color(0xFFEF9A9A)
                                    assignedPhoto != null -> Color(0xFF81C784)
                                    else -> Color(0xFFBBDEFB)
                                }
                            )
                            .border(
                                width = if (assignedPhoto != null && !isCorrectPosition) 3.dp
                                else if (isHighlighted) 4.dp else 2.dp,
                                color = when {
                                    assignedPhoto != null && !isCorrectPosition -> Color.Red
                                    isHighlighted -> Color(0xFFF57C00)
                                    else -> Color(0xFF1976D2)
                                },
                                shape = RoundedCornerShape(16.dp)
                            )
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
                                    .clip(RoundedCornerShape(14.dp))
                                    .pointerInput(index) {
                                        detectDragGestures(
                                            onDragStart = {
                                                val bounds = placedPhotoBounds.getOrNull(index)
                                                if (bounds != null) {
                                                    draggingSlotIndex = index
                                                    dragStartBounds = bounds
                                                    dragOffsetPx = Offset.Zero
                                                    dragCenterPx = bounds.center
                                                    isDraggingFromSlot = true
                                                }
                                            },
                                            onDrag = { change, dragAmount ->
                                                if (draggingSlotIndex == index && dragStartBounds != null) {
                                                    change.consume()
                                                    dragOffsetPx += dragAmount
                                                    val bounds = dragStartBounds!!
                                                    dragCenterPx = bounds.topLeft + dragOffsetPx +
                                                            Offset(bounds.width / 2f, bounds.height / 2f)
                                                }
                                            },
                                            onDragEnd = {
                                                if (draggingSlotIndex == index) handleDrop()
                                            },
                                            onDragCancel = {
                                                if (draggingSlotIndex == index) resetDragState()
                                            }
                                        )
                                    }
                                    .onGloballyPositioned { coords ->
                                        placedPhotoBounds[index] = coords.boundsInRoot()
                                    }
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = (index + 1).toString(),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0D47A1)
                                )
                            }
                        }
                    }
                }
            }

            // Se eliminaron los mensajes de texto de feedback
        }

        val density = LocalDensity.current

        if ((draggingPhotoIndex != null || draggingSlotIndex != null) && dragStartBounds != null) {
            val photoRes = when {
                isDraggingFromSlot && draggingSlotIndex != null -> slotAssignments[draggingSlotIndex!!]
                !isDraggingFromSlot && draggingPhotoIndex != null -> photos[draggingPhotoIndex!!]
                else -> null
            }

            if (photoRes != null) {
                val bounds = dragStartBounds!!
                val widthDp = with(density) { bounds.width.toDp() } * 0.7f
                val heightDp = with(density) { bounds.height.toDp() } * 0.7f

                Image(
                    painter = painterResource(id = photoRes),
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

        // Diálogo para éxito (3 o más fotos correctas)
        if (showSuccessDialog) {
            Dialog(
                onDismissRequest = { showSuccessDialog = false }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .aspectRatio(0.8f),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.8f)
                                .padding(horizontal = 16.dp, vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ondo_egina),
                                contentDescription = "Ondo eginda",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Button(
                            onClick = {
                                navController.navigate(Routes.MAPA_SCREEN)                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(bottom = 24.dp)
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Text("Jolasekin jarraitu!", fontSize = 18.sp)
                        }
                    }
                }
            }
        }

        // Diálogo para error (menos de 3 fotos correctas)
        if (showWrongDialog) {
            Dialog(
                onDismissRequest = { showWrongDialog = false }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .aspectRatio(0.8f),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.6f) // Un poco menos para dejar espacio al texto
                                .padding(horizontal = 16.dp, vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.saiatu_berriro), // Cambia esto por tu imagen de error
                                contentDescription = "Inténtalo de nuevo",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Button(
                            onClick = {
                                navController.navigate(Routes.ORDENATUJOLASA_SCREEN)
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(bottom = 24.dp)
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFC62828)
                            )
                        ) {
                            Text("Saiatu berriro!", fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GameScreenPreview() {
    val navController = rememberNavController()
    OrdenatuJolasaScreen(navController = navController)
}