package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.errenteriaapp.R
import com.example.errenteriaapp.components.GameResultDialogs

// Palabras que pertenecen a cada personaje (con las mayúsculas corregidas)
val xantiWords = listOf("Alkondara Zuria", "Txaleko Gorria", "Txapela", "Gona Urdina")
val maialenWords = listOf("Blusa Zuria", "Kortse Beltza", "Zapia Buruan Lotuta", "Zapia Lepoan Lotuta", "Mantal Beltza")

// SOLO UN Gona Gorria
val extraWords = listOf("Gona Gorria")

@Composable
fun TaulaArrastrarScreen(
    navController: NavController
) {
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    // Todas las palabras disponibles (SOLO UN Gona Gorria)
    val allWords = remember {
        (xantiWords + maialenWords + extraWords).shuffled().toMutableStateList()
    }

    // Asignaciones actuales
    val xantiAssignments = remember {
        mutableStateListOf<String?>().apply { repeat(xantiWords.size) { add(null) } }
    }
    val maialenAssignments = remember {
        mutableStateListOf<String?>().apply { repeat(maialenWords.size) { add(null) } }
    }

    // Estado para el arrastre
    var draggingWord by remember { mutableStateOf<String?>(null) }
    var dragStartBounds by remember { mutableStateOf<Rect?>(null) }
    var dragOffsetPx by remember { mutableStateOf(Offset.Zero) }
    var dragCenterPx by remember { mutableStateOf<Offset?>(null) }
    var isDraggingFromSlot by remember { mutableStateOf(false) }
    var draggingSlotPersonaje by remember { mutableStateOf<String?>(null) }
    var draggingSlotIndex by remember { mutableStateOf<Int?>(null) }

    // Para rastrear la posición inicial del arrastre
    var dragStartPosition by remember { mutableStateOf(Offset.Zero) }

    // Zonas de drop
    val xantiDropZones = remember(xantiWords.size) {
        mutableStateListOf<Rect?>().apply { repeat(xantiWords.size) { add(null) } }
    }
    val maialenDropZones = remember(maialenWords.size) {
        mutableStateListOf<Rect?>().apply { repeat(maialenWords.size) { add(null) } }
    }
    // Determinar si todos los huecos están llenos
    val allSlotsFilled by remember(xantiAssignments, maialenAssignments) {
        derivedStateOf {
            xantiAssignments.all { it != null } && maialenAssignments.all { it != null }
        }
    }

    // Resetear estado del arrastre
    fun resetDragState() {
        draggingWord = null
        draggingSlotPersonaje = null
        draggingSlotIndex = null
        dragStartBounds = null
        dragOffsetPx = Offset.Zero
        dragCenterPx = null
        isDraggingFromSlot = false
        dragStartPosition = Offset.Zero
    }
    fun handleDropToZone(personaje: String, targetIndex: Int) {
        val word = draggingWord ?: return resetDragState()

        when (personaje) {
            "Xanti" -> {
                // Si hay palabra en el slot objetivo, intercambiar
                val existingWord = xantiAssignments[targetIndex]
                xantiAssignments[targetIndex] = word

                // Si veníamos de un slot, limpiar el slot anterior
                if (isDraggingFromSlot) {
                    when (draggingSlotPersonaje) {
                        "Xanti" -> {
                            draggingSlotIndex?.let { index ->
                                xantiAssignments[index] = existingWord
                            }
                        }
                        "Maialen" -> {
                            draggingSlotIndex?.let { index ->
                                maialenAssignments[index] = existingWord
                            }
                        }
                    }
                }
            }
            "Maialen" -> {
                // Si hay palabra en el slot objetivo, intercambiar
                val existingWord = maialenAssignments[targetIndex]
                maialenAssignments[targetIndex] = word

                // Si veníamos de un slot, limpiar el slot anterior
                if (isDraggingFromSlot) {
                    when (draggingSlotPersonaje) {
                        "Xanti" -> {
                            draggingSlotIndex?.let { index ->
                                xantiAssignments[index] = existingWord
                            }
                        }
                        "Maialen" -> {
                            draggingSlotIndex?.let { index ->
                                maialenAssignments[index] = existingWord
                            }
                        }
                    }
                }
            }
        }

        resetDragState()
    }
    // Manejar el drop
    fun handleDrop() {
        val dropPoint = dragCenterPx ?: return resetDragState()

        // Buscar en zonas de Xanti
        val targetXantiIndex = xantiDropZones.indexOfFirst { rect -> rect?.contains(dropPoint) == true }
        if (targetXantiIndex != -1) {
            handleDropToZone("Xanti", targetXantiIndex)
            return
        }

        // Buscar en zonas de Maialen
        val targetMaialenIndex = maialenDropZones.indexOfFirst { rect -> rect?.contains(dropPoint) == true }
        if (targetMaialenIndex != -1) {
            handleDropToZone("Maialen", targetMaialenIndex)
            return
        }

        // Si no cayó en ninguna zona, volver a origen
        resetDragState()
    }



    // Verificar respuestas - SOLO VALIDA CUANDO SE PULSA EL BOTÓN
    fun checkAnswers(): Boolean {
        // Verificar Xanti - NO IMPORTA EL ORDEN
        val xantiAssignedWords = xantiAssignments.filterNotNull().toSet()
        val xantiRequiredWords = xantiWords.toSet()

        // Verificar Maialen - NO IMPORTA EL ORDEN
        val maialenAssignedWords = maialenAssignments.filterNotNull().toSet()
        val maialenRequiredWords = maialenWords.toSet()

        return xantiAssignedWords == xantiRequiredWords &&
                maialenAssignedWords == maialenRequiredWords
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "Hitzak Tauletan Sailkatu",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F),
                modifier = Modifier.padding(bottom = 16.dp,top = 15.dp)
            )

            // Imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray.copy(alpha = 0.2f))
                    .border(2.dp, Color.Gray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                val context = LocalContext.current
                val resourceId = context.resources.getIdentifier(
                    "xanti_eta_maialen_erraldoiak",
                    "drawable",
                    context.packageName
                )

                if (resourceId != 0) {
                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = "Xanti eta Maialen Erraldoiak",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Imagen no encontrada")
                        Text("xanti_eta_maialen_erraldoiak", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Área de personajes CON SCROLL
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Maialen con scroll
                PersonajeAreaScroll(
                    nombre = "MAIALEN",
                    assignments = maialenAssignments,
                    dropZones = maialenDropZones,
                    personaje = "Maialen",
                    onSlotPositioned = { index, rect ->
                        maialenDropZones[index] = rect
                    },
                    onDragStart = { personaje, index, startPosition, bounds ->
                        if (maialenAssignments[index] != null) {
                            isDraggingFromSlot = true
                            draggingSlotPersonaje = personaje
                            draggingSlotIndex = index
                            draggingWord = maialenAssignments[index]
                            dragStartPosition = startPosition
                            dragStartBounds = bounds
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


                Spacer(modifier = Modifier.width(12.dp))
                // Xanti con scroll
                PersonajeAreaScroll(
                    nombre = "XANTI",
                    assignments = xantiAssignments,
                    dropZones = xantiDropZones,
                    personaje = "Xanti",
                    onSlotPositioned = { index, rect ->
                        xantiDropZones[index] = rect
                    },
                    onDragStart = { personaje, index, startPosition, bounds ->
                        if (xantiAssignments[index] != null) {
                            isDraggingFromSlot = true
                            draggingSlotPersonaje = personaje
                            draggingSlotIndex = index
                            draggingWord = xantiAssignments[index]
                            dragStartPosition = startPosition
                            dragStartBounds = bounds
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

            Spacer(modifier = Modifier.height(20.dp))

            // Palabras disponibles - CARD CON SCROLL INTERNO
            Text(
                text = "Hitzak arrastatu:",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
            )

            // Card con scroll horizontal para las palabras
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp)
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxSize(), // solo ocupa el card
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    val availableWords = allWords.filter { word ->
                        !xantiAssignments.contains(word) && !maialenAssignments.contains(word)
                    }

                    items(availableWords) { word ->
                        var bounds by remember { mutableStateOf<Rect?>(null) }

                        Box(
                            modifier = Modifier
                                .size(140.dp, 80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFF3E0))
                                .border(
                                    2.dp,
                                    Color(0xFFFF9800),
                                    RoundedCornerShape(8.dp)
                                )
                                .onGloballyPositioned { coords ->
                                    bounds = coords.boundsInWindow()
                                }
                                .pointerInput(word) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            draggingWord = word
                                            dragStartPosition = offset
                                            dragStartBounds = bounds
                                            dragOffsetPx = Offset.Zero
                                            dragCenterPx = bounds?.center
                                            isDraggingFromSlot = false
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            dragOffsetPx += dragAmount
                                            dragStartBounds?.let { startBounds ->
                                                dragCenterPx = startBounds.topLeft + dragOffsetPx +
                                                        Offset(startBounds.width / 2f, startBounds.height / 2f)
                                            }
                                        },
                                        onDragEnd = { handleDrop() },
                                        onDragCancel = { resetDragState() }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = word,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                maxLines = 2,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón Verificar
            Button(
                onClick = {
                    val isCorrect = checkAnswers()
                    if (isCorrect) {
                        showSuccessDialog = true
                    } else {
                        showErrorDialog = true
                    }
                },
                enabled = allSlotsFilled, // <-- deshabilita si no están llenos
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (allSlotsFilled) Color(0xFF2196F3) else Color(0xFF90CAF9),
                    contentColor = Color.White
                )
            ) {
                Text(text = "EGIAZTATU", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        val density = LocalDensity.current
        if (draggingWord != null && dragStartBounds != null) {
            Box(
                modifier = Modifier
                    .offset(
                        x = with(density) { (dragStartBounds!!.topLeft.x + dragOffsetPx.x).toDp() },
                        y = with(density) { (dragStartBounds!!.topLeft.y + dragOffsetPx.y).toDp() }
                    )
                    .size(140.dp, 80.dp)
                    .shadow(12.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isDraggingFromSlot) {
                            when (draggingSlotPersonaje) {
                                "Xanti" -> Color(0xFFBBDEFB)
                                "Maialen" -> Color(0xFFE1BEE7)
                                else -> Color(0xFFFFF3E0)
                            }
                        } else {
                            Color(0xFFFFF3E0).copy(alpha = 0.95f)
                        }
                    )
                    .border(2.dp, Color(0xFF2196F3), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = draggingWord!!,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }



        // Usar GameResultDialogs en lugar de los diálogos antiguos
        GameResultDialogs(
            showSuccess = showSuccessDialog,
            showWrong = showErrorDialog,
            onDismissSuccess = { showSuccessDialog = false },
            onDismissWrong = { showErrorDialog = false },
            onSuccessButton = {
                showSuccessDialog = false
                navController.navigate("MAPA_SCREEN")
            },
            onWrongButton = {
                showErrorDialog = false
                // Reiniciar
                xantiAssignments.forEachIndexed { index, _ ->
                    xantiAssignments[index] = null
                }
                maialenAssignments.forEachIndexed { index, _ ->
                    maialenAssignments[index] = null
                }
                resetDragState()
            }
        )
    }
}

@Composable
fun PersonajeAreaScroll(
    nombre: String,
    assignments: List<String?>,
    dropZones: MutableList<Rect?>,
    personaje: String,
    onSlotPositioned: (Int, Rect) -> Unit,
    onDragStart: (String, Int, Offset, Rect) -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                when (personaje) {
                    "Xanti" -> Color(0xFFE1F5FE)
                    "Maialen" -> Color(0xFFF3E5F5)
                    else -> Color.LightGray
                }
            )
            .border(2.dp, Color(0xFF757575), RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Nombre del personaje
        Text(
            text = nombre,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Slots para palabras CON SCROLL
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            assignments.forEachIndexed { index, word ->
                WordSlot(
                    slotIndex = index,
                    assignedWord = word,
                    personaje = personaje,
                    onSlotPositioned = { rect ->
                        onSlotPositioned(index, rect)
                    },
                    onDragStart = { startPosition, bounds ->
                        onDragStart(personaje, index, startPosition, bounds)
                    },
                    onDrag = onDrag,
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragCancel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                )
            }
        }
    }
}

@Composable
fun WordSlot(
    modifier: Modifier = Modifier,
    slotIndex: Int,
    assignedWord: String?,
    personaje: String,
    onSlotPositioned: (Rect) -> Unit,
    onDragStart: (Offset, Rect) -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    // Guardar las bounds localmente
    var bounds by remember { mutableStateOf<Rect?>(null) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (assignedWord == null) Color.White.copy(alpha = 0.9f)
                else Color(0xFFFFCC80) // Naranja claro cuando tiene palabra
            )
            .border(
                width = 2.dp,
                color = if (assignedWord == null) Color(0xFFBDBDBD) else Color(0xFFFF9800), // Naranja más intenso
                shape = RoundedCornerShape(10.dp),
            )
            .onGloballyPositioned { coords ->
                bounds = coords.boundsInWindow()
                onSlotPositioned(coords.boundsInWindow())
            }
    ) {
        if (assignedWord != null) {
            Text(
                text = assignedWord,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 2,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .pointerInput(slotIndex) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                bounds?.let {
                                    onDragStart(offset, it)
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                onDrag(dragAmount.x, dragAmount.y)
                            },
                            onDragEnd = { onDragEnd() },
                            onDragCancel = { onDragCancel() }
                        )
                    }
            )
        } else {
            Text(
                text = "Arrastratu hemen",
                fontSize = 13.sp,
                color = Color(0xFF757575),
                fontWeight = FontWeight.Medium
            )
        }
    }

}