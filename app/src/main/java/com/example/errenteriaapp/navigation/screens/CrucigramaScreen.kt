package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.errenteriaapp.components.GameResultDialogs
import com.example.errenteriaapp.navigation.Routes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Estado para cada celda del crucigrama
data class CeldaEstado(
    val fila: Int,
    val columna: Int,
    val esNegra: Boolean = false,
    val numeroPista: Int? = null,
    var letraUsuario: Char? = null,
    val letraCorrecta: Char? = null,
    var esCorrecta: Boolean = false,
    var estaEditando: Boolean = false
)

// Definición de palabras del crucigrama
data class PalabraInfo(
    val numero: Int,
    val texto: String,
    val direccion: String, // "HORIZONTAL" o "VERTICAL"
    val filaInicio: Int,
    val columnaInicio: Int,
    val longitud: Int
)

class CrucigramaEstado {
    val palabras = listOf(
        PalabraInfo(1, "LEIZEA", "HORIZONTAL", 0, 2, 6),
        PalabraInfo(3, "ESTALAKTITA", "VERTICAL", 0, 6, 11),
        PalabraInfo(5, "ZUTABEA", "HORIZONTAL", 3, 0, 7),
        PalabraInfo(2, "ESTALAGMITA", "VERTICAL", 0, 3, 11),
        PalabraInfo(4, "RUPESTRE", "VERTICAL", 2, 1, 8)
    )

    // Mapa para acceso rápido a celdas por coordenadas
    val mapaCeldas = mutableMapOf<Pair<Int, Int>, MutableList<PalabraInfo>>()

    // Mapa para acceso rápido a palabras por número
    val mapaPalabrasPorNumero = mutableMapOf<Int, PalabraInfo>()

    init {
        // Construir mapa de celdas (una celda puede tener múltiples palabras)
        palabras.forEach { palabra ->
            mapaPalabrasPorNumero[palabra.numero] = palabra
            for (i in 0 until palabra.longitud) {
                val fila = if (palabra.direccion == "HORIZONTAL") palabra.filaInicio else palabra.filaInicio + i
                val columna = if (palabra.direccion == "HORIZONTAL") palabra.columnaInicio + i else palabra.columnaInicio
                val key = Pair(fila, columna)
                if (!mapaCeldas.containsKey(key)) {
                    mapaCeldas[key] = mutableListOf()
                }
                mapaCeldas[key]?.add(palabra)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun CrucigramaScreen(navController: NavController) {
    // Estado del crucigrama
    val celdas = remember { crearCeldasEstado().toMutableStateList() }
    val crucigramaEstado = remember { CrucigramaEstado() }
    var verificacionRealizada by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    var mostrarDialogoExito by remember { mutableStateOf(false) }
    var direccionActual by remember { mutableStateOf("HORIZONTAL") }
    var palabraActiva by remember { mutableStateOf<PalabraInfo?>(null) }
    val context = LocalContext.current
    var mostrarInstruccionesIniciales by remember { mutableStateOf(true) }

    // Mapa de FocusRequesters para cada celda
    val focusRequesters = remember {
        mutableMapOf<Pair<Int, Int>, FocusRequester>().apply {
            for (fila in 0 until 11) {
                for (columna in 0 until 8) {
                    this[Pair(fila, columna)] = FocusRequester()
                }
            }
        }
    }

    // Función para obtener una celda específica
    fun obtenerCelda(fila: Int, columna: Int): CeldaEstado? {
        return celdas.find { it.fila == fila && it.columna == columna }
    }

    // Función para actualizar una celda
    fun actualizarCelda(fila: Int, columna: Int, actualizacion: (CeldaEstado) -> CeldaEstado) {
        val index = celdas.indexOfFirst { it.fila == fila && it.columna == columna }
        if (index != -1) {
            celdas[index] = actualizacion(celdas[index])
        }
    }

    // Función para encontrar la palabra en una celda
    fun encontrarPalabraEnCelda(fila: Int, columna: Int): List<PalabraInfo> {
        return crucigramaEstado.mapaCeldas[Pair(fila, columna)] ?: emptyList()
    }

    fun palabraEstaCompleta(palabra: PalabraInfo): Boolean {
        for (i in 0 until palabra.longitud) {
            val fila = if (palabra.direccion == "HORIZONTAL")
                palabra.filaInicio
            else
                palabra.filaInicio + i

            val columna = if (palabra.direccion == "HORIZONTAL")
                palabra.columnaInicio + i
            else
                palabra.columnaInicio

            val celda = obtenerCelda(fila, columna)
            if (celda?.letraUsuario == null) {
                return false
            }
        }
        return true
    }

    fun juegoCompletado(): Boolean {
        return celdas
            .filter { !it.esNegra }
            .all { it.letraUsuario != null && it.esCorrecta }
    }

    // Función para activar una palabra por su número
    fun activarPalabraPorNumero(numero: Int) {
        coroutineScope.launch {
            delay(100)

            // Buscar la palabra por número
            val palabra = crucigramaEstado.mapaPalabrasPorNumero[numero]
            palabra?.let {
                // Si la misma palabra ya está activa, desactivarla
                if (palabraActiva?.numero == numero) {
                    palabraActiva = null
                } else {
                    // Activar la nueva palabra
                    palabraActiva = it
                    direccionActual = it.direccion

                    // Buscar la primera celda vacía de esta palabra y enfocarla
                    for (i in 0 until it.longitud) {
                        val fila = if (it.direccion == "HORIZONTAL") it.filaInicio else it.filaInicio + i
                        val columna = if (it.direccion == "HORIZONTAL") it.columnaInicio + i else it.columnaInicio

                        val celda = obtenerCelda(fila, columna)
                        if (celda != null && !celda.esNegra && celda.letraUsuario == null) {
                            focusRequesters[Pair(fila, columna)]?.requestFocus()
                            return@let
                        }
                    }

                    // Si todas las celdas están llenas, enfocar la primera
                    val primeraFila = it.filaInicio
                    val primeraColumna = it.columnaInicio
                    focusRequesters[Pair(primeraFila, primeraColumna)]?.requestFocus()
                }
            }
        }
    }

    fun moverFocoASiguienteCelda(filaActual: Int, columnaActual: Int) {
        coroutineScope.launch {
            delay(30)

            val palabra = palabraActiva ?: return@launch

            val posActual = if (palabra.direccion == "HORIZONTAL") {
                columnaActual - palabra.columnaInicio
            } else {
                filaActual - palabra.filaInicio
            }

            // Buscar la siguiente casilla VACÍA dentro de la palabra
            for (i in posActual + 1 until palabra.longitud) {
                val nextFila = if (palabra.direccion == "HORIZONTAL") {
                    palabra.filaInicio
                } else {
                    palabra.filaInicio + i
                }

                val nextColumna = if (palabra.direccion == "HORIZONTAL") {
                    palabra.columnaInicio + i
                } else {
                    palabra.columnaInicio
                }

                val celda = obtenerCelda(nextFila, nextColumna)

                // Saltar celdas con letra
                if (celda != null && !celda.esNegra && celda.letraUsuario == null) {
                    focusRequesters[Pair(nextFila, nextColumna)]?.requestFocus()
                    return@launch
                }
            }
        }
    }

    fun onLetraCambiada(fila: Int, columna: Int, nuevoCaracter: Char?) {
        if (nuevoCaracter == null) return

        // 1. Actualizar letra
        actualizarCelda(fila, columna) { celda ->
            if (!celda.esNegra && !celda.esCorrecta) {
                celda.copy(
                    letraUsuario = nuevoCaracter.uppercaseChar(),
                    esCorrecta = false
                )
            } else celda
        }

        // 2. Avanzar foco SOLO si hay palabra activa
        palabraActiva?.let { palabra ->
            moverFocoASiguienteCelda(fila, columna)
        }
    }

    fun onEnterPressed(fila: Int, columna: Int) {
        // No hacer nada especial con Enter
    }

    fun moverFocoACeldaAnterior(filaActual: Int, columnaActual: Int) {
        coroutineScope.launch {
            delay(30)

            val palabra = palabraActiva ?: return@launch

            val posActual = if (palabra.direccion == "HORIZONTAL") {
                columnaActual - palabra.columnaInicio
            } else {
                filaActual - palabra.filaInicio
            }

            // Buscar la casilla anterior (aunque esté vacía)
            if (posActual > 0) {
                val prevFila = if (palabra.direccion == "HORIZONTAL") {
                    palabra.filaInicio
                } else {
                    palabra.filaInicio + (posActual - 1)
                }

                val prevColumna = if (palabra.direccion == "HORIZONTAL") {
                    palabra.columnaInicio + (posActual - 1)
                } else {
                    palabra.columnaInicio
                }

                val celda = obtenerCelda(prevFila, prevColumna)

                // Ir a la celda anterior si no es negra ni correcta
                if (celda != null && !celda.esNegra && !celda.esCorrecta) {
                    focusRequesters[Pair(prevFila, prevColumna)]?.requestFocus()
                    return@launch
                }
            }

            // Si no se puede ir a la anterior (o es la primera), enfocar la primera celda
            val primeraFila = palabra.filaInicio
            val primeraColumna = palabra.columnaInicio
            focusRequesters[Pair(primeraFila, primeraColumna)]?.requestFocus()
        }
    }

    fun borrarYRetroceder(fila: Int, columna: Int) {
        val palabra = palabraActiva ?: return

        // Posición relativa en la palabra
        val posActual = if (palabra.direccion == "HORIZONTAL") {
            columna - palabra.columnaInicio
        } else {
            fila - palabra.filaInicio
        }

        coroutineScope.launch {
            delay(30)

            // CASO 1: Borrar la celda actual si tiene contenido
            val celdaActual = obtenerCelda(fila, columna)
            if (celdaActual?.letraUsuario != null && !celdaActual.esCorrecta) {
                actualizarCelda(fila, columna) { it.copy(letraUsuario = null, esCorrecta = false) }
            }

            // CASO 2: Mover foco a la celda anterior (aunque esté vacía)
            if (posActual > 0) {
                moverFocoACeldaAnterior(fila, columna)
            } else {
                // Si es la primera celda, mantener foco aquí
                focusRequesters[Pair(fila, columna)]?.requestFocus()
            }
        }
    }

    // Función para verificar si una celda pertenece a la palabra activa
    fun esCeldaDePalabraActiva(fila: Int, columna: Int): Boolean {
        val palabrasEnCelda = crucigramaEstado.mapaCeldas[Pair(fila, columna)] ?: return false
        return palabrasEnCelda.any { it.numero == palabraActiva?.numero }
    }

    // Función para verificar respuestas
    fun verificarRespuestas() {
        // Resetear verificaciones previas
        celdas.forEachIndexed { index, celda ->
            if (!celda.esNegra) {
                celdas[index] = celda.copy(
                    esCorrecta = false
                )
            }
        }

        // Verificar respuestas horizontales
        verificarHorizontal(1, 0, 2, "LEIZEA", celdas)
        verificarHorizontal(5, 3, 0, "ZUTABEA", celdas)
        verificarVertical(2, 0, 3, "ESTALAGMITA", celdas)
        verificarVertical(4, 2, 1, "RUPESTRE", celdas)
        verificarVertical(3, 0, 6, "ESTALAKTITA", celdas)

        verificacionRealizada = true

        if (juegoCompletado()) {
            mostrarDialogoExito = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título principal
        Text(
            text = "GURUTZEGRAMA",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2196F3),
            modifier = Modifier.padding(bottom = 4.dp, top = 10.dp)
        )

        Text(
            text = "Aizpitarte-Hitz gurutzatuak",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Indicador de palabra activa
        if (palabraActiva != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                ),
                border = BorderStroke(1.dp, Color(0xFF2196F3))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Palabra activa: ${palabraActiva!!.numero} ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )

                    Button(
                        onClick = {
                            palabraActiva = null
                            focusManager.clearFocus()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF44336),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Desactivar", fontSize = 12.sp)
                    }
                }
            }
        }

        // Tablero del crucigrama interactivo
        TableroCrucigramaInteractivo(
            celdas = celdas,
            focusRequesters = focusRequesters,
            onLetraCambiada = { fila, columna, caracter ->
                onLetraCambiada(fila, columna, caracter)
            },
            onBorrar = { fila, columna ->
                borrarYRetroceder(fila, columna)
            },
            onEnterPressed = { fila, columna ->
                onEnterPressed(fila, columna)
            },
            focusManager = focusManager,
            palabraActiva = palabraActiva,
            crucigramaEstado = crucigramaEstado,
            onClickCelda = { fila, columna ->
                // Al hacer clic en una celda, activar la primera palabra que contiene
                val palabrasEnCelda = encontrarPalabraEnCelda(fila, columna)
                if (palabrasEnCelda.isNotEmpty()) {
                    activarPalabraPorNumero(palabrasEnCelda.first().numero)
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Sección de pistas HORIZONTALES - AHORA SON CLICABLES
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE8F5E9)
            ),
            border = BorderStroke(1.dp, Color(0xFF4CAF50))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "HORIZONTALAK",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Pista 1 - CLICABLE
                PistaClicableItem(
                    numero = 1,
                    texto = "Duela milaka urte kobazuloetan bizi ziren gizakiak.",
                    esActiva = palabraActiva?.numero == 1,
                    esHorizontal = true,
                    onClick = { activarPalabraPorNumero(1) }
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Pista 5 - CLICABLE
                PistaClicableItem(
                    numero = 5,
                    texto = "Haria lantzeko erabilizen zuten tresna.",
                    esActiva = palabraActiva?.numero == 5,
                    esHorizontal = true,
                    onClick = { activarPalabraPorNumero(5) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sección de pistas VERTICALES - AHORA SON CLICABLES
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            ),
            border = BorderStroke(1.dp, Color(0xFF2196F3))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "BERTIKALAK",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Pista 2 - CLICABLE
                PistaClicableItem(
                    numero = 2,
                    texto = "Historiaurrea ikertzen duen zientzialaria.",
                    esActiva = palabraActiva?.numero == 2,
                    esHorizontal = false,
                    onClick = { activarPalabraPorNumero(2) }
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Pista 4 - CLICABLE
                PistaClicableItem(
                    numero = 4,
                    texto = "Aizpitarteko kobazuloak dauden herria.",
                    esActiva = palabraActiva?.numero == 4,
                    esHorizontal = false,
                    onClick = { activarPalabraPorNumero(4) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Pista 3 - CLICABLE (se movió aquí porque es vertical)
                PistaClicableItem(
                    numero = 3,
                    texto = "Kobazuloetako hormetan margotzen zituzten animaliak eta sinboloak.",
                    esActiva = palabraActiva?.numero == 3,
                    esHorizontal = false,
                    onClick = { activarPalabraPorNumero(3) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Egiaztatu (verificar)
        Button(
            onClick = {
                verificarRespuestas()
                keyboardController?.hide()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800),
                contentColor = Color.White
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "EGIAZTATU",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))


        if (mostrarDialogoExito) {
            GameResultDialogs(
                showSuccess = true,
                showWrong = false,
                onDismissSuccess = {
                    mostrarDialogoExito = false
                },
                onDismissWrong = {},
                onSuccessButton = {
                    mostrarDialogoExito = false
                    navController.navigate(Routes.MAPA_SCREEN)
                },
                onWrongButton = {}
            )
        }

        // Indicador de estado
        if (verificacionRealizada && !mostrarDialogoExito) {
            val correctas = celdas.count { !it.esNegra && it.esCorrecta }
            val totales = celdas.count { !it.esNegra }

            Text(
                text = "Letras correctas: $correctas / $totales",
                color = Color(0xFFFF9800),
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        if (mostrarInstruccionesIniciales) {
            AlertDialog(
                onDismissRequest = { mostrarInstruccionesIniciales = false },
                title = {
                    Text(
                        text = "¡Bienvenido al Crucigrama!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Instrucciones:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.padding(bottom = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("• ", color = Color(0xFFFF9800))
                            Text(
                                text = "Haz clic en el NÚMERO de una pista para activar esa palabra",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.padding(bottom = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("• ", color = Color(0xFFFF9800))
                            Text(
                                text = "Solo puedes escribir en la palabra activa (se marca en naranja)",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.padding(bottom = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("• ", color = Color(0xFFFF9800))
                            Text(
                                text = "Presiona EGIAZTATU para verificar tus respuestas",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { mostrarInstruccionesIniciales = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        )
                    ) {
                        Text("¡Entendido!")
                    }
                },
                containerColor = Color.White,
                shape = MaterialTheme.shapes.large
            )
        }

    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun TableroCrucigramaInteractivo(
    celdas: List<CeldaEstado>,
    focusRequesters: Map<Pair<Int, Int>, FocusRequester>,
    onLetraCambiada: (Int, Int, Char?) -> Unit,
    onBorrar: (Int, Int) -> Unit,
    onEnterPressed: (Int, Int) -> Unit,
    focusManager: FocusManager,
    palabraActiva: PalabraInfo?,
    crucigramaEstado: CrucigramaEstado,
    onClickCelda: (Int, Int) -> Unit
) {
    // Organizar celdas en grid
    val grid = Array(11) { Array<CeldaEstado?>(8) { null } }

    celdas.forEach { celda ->
        if (celda.fila < 11 && celda.columna < 8) {
            grid[celda.fila][celda.columna] = celda
        }
    }

    Column(
        modifier = Modifier
            .border(3.dp, Color.Black)
            .background(Color.White)
            .padding(1.dp)
    ) {
        for (fila in 0 until 11) {
            Row {
                for (columna in 0 until 8) {
                    val celda = grid[fila][columna]
                    celda?.let {
                        // Verificar si la celda pertenece a la palabra activa
                        val estaActiva = palabraActiva?.let { palabra ->
                            val palabrasEnCelda = crucigramaEstado.mapaCeldas[Pair(fila, columna)]
                            palabrasEnCelda?.any { it.numero == palabra.numero } ?: false
                        } ?: false

                        CeldaInteractivaUI(
                            celda = it,
                            focusRequester = focusRequesters[Pair(fila, columna)] ?: FocusRequester(),
                            onLetraCambiada = { caracter ->
                                onLetraCambiada(fila, columna, caracter)
                            },
                            onBorrar = {
                                onBorrar(fila, columna)
                            },
                            onEnterPressed = {
                                onEnterPressed(fila, columna)
                            },
                            focusManager = focusManager,
                            estaActiva = estaActiva,
                            onClickCelda = {
                                onClickCelda(fila, columna)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CeldaInteractivaUI(
    celda: CeldaEstado,
    focusRequester: FocusRequester,
    onLetraCambiada: (Char?) -> Unit,
    onBorrar: () -> Unit,
    onEnterPressed: () -> Unit,
    focusManager: FocusManager,
    estaActiva: Boolean,
    onClickCelda: () -> Unit
) {
    var textFieldValue by remember(celda.letraUsuario) {
        mutableStateOf(
            TextFieldValue(
                text = celda.letraUsuario?.toString() ?: "",
                selection = TextRange(celda.letraUsuario?.toString()?.length ?: 0)
            )
        )
    }

    // Determinar color de fondo
    val backgroundColor = when {
        celda.esNegra -> Color.Black
        celda.esCorrecta -> Color(0xFFC8E6C9)
        estaActiva -> Color(0xFFFFF3E0) // Naranja claro para palabra activa
        else -> Color.White
    }

    // Determinar color del texto
    val textColor = when {
        celda.esNegra -> Color.White
        celda.esCorrecta -> Color(0xFF2E7D32)
        estaActiva -> Color(0xFFE65100) // Naranja oscuro para palabra activa
        else -> Color.Black
    }

    Box(
        modifier = Modifier
            .size(35.dp)
            .border(
                width = if (estaActiva) 2.dp else 1.dp,
                color = if (estaActiva) Color(0xFFFF9800) else Color.Gray
            )
            .background(backgroundColor)
            .clickable(
                enabled = !celda.esNegra && !celda.esCorrecta,
                onClick = onClickCelda
            ),
        contentAlignment = Alignment.Center
    ) {
        // Número de pista
        if (celda.numeroPista != null) {
            Text(
                text = "${celda.numeroPista}",
                fontSize = 10.sp,
                color = if (estaActiva) Color.Red else Color.Blue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(2.dp)
            )
        }

        if (celda.esNegra) {
            // Celda negra - no editable
        } else if (celda.esCorrecta) {
            // Celda correcta - mostrar letra pero no editable
            Text(
                text = celda.letraUsuario?.toString() ?: "",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
        } else {
            // Celda editable
            BasicTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    // BACKSPACE - cuando el texto se hace vacío
                    if (newValue.text.isEmpty()) {
                        textFieldValue = TextFieldValue("", TextRange(0))
                        onBorrar()  // Llama a borrarYRetroceder
                        return@BasicTextField
                    }

                    // Escribir una letra nueva (solo una letra permitida)
                    if (newValue.text.length == 1) {
                        textFieldValue = newValue.copy(selection = TextRange(newValue.text.length))
                        onLetraCambiada(newValue.text[0])
                    }

                    // Si es más de una letra (pegar texto), tomar solo la primera
                    else if (newValue.text.length > 1) {
                        val primeraLetra = newValue.text.first().toString()
                        textFieldValue = TextFieldValue(primeraLetra, TextRange(primeraLetra.length))
                        onLetraCambiada(primeraLetra[0])
                    }
                },
                modifier = Modifier
                    .size(30.dp)
                    .focusRequester(focusRequester),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        onEnterPressed()
                    },
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                singleLine = true,
                maxLines = 1,
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        innerTextField()
                        if (textFieldValue.text.isEmpty()) {
                            Text(
                                text = "",
                                fontSize = 20.sp,
                                color = Color.LightGray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            )
        }
    }
}
@Composable
fun PistaClicableItem(numero: Int, texto: String, esActiva: Boolean, esHorizontal: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (esActiva) Color(0xFFFFF3E0) else Color.Transparent
        ),
        border = if (esActiva) BorderStroke(2.dp, Color(0xFFFF9800)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(
                        color = if (esActiva) Color(0xFFFF9800) else
                            if (esHorizontal) Color(0xFFC8E6C9) else Color(0xFFBBDEFB),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                    .border(
                        1.dp,
                        if (esActiva) Color.Red else
                            if (esHorizontal) Color(0xFF4CAF50) else Color(0xFF2196F3),
                        androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$numero",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (esActiva) Color.White else
                        if (esHorizontal) Color(0xFF2E7D32) else Color(0xFF1565C0)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = texto,
                fontSize = 15.sp,
                color = if (esActiva) Color(0xFFE65100) else Color.Black,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// Función para crear el estado inicial de las celdas
fun crearCeldasEstado(): List<CeldaEstado> {
    val celdas = mutableListOf<CeldaEstado>()

    // Fila 0
    celdas.add(CeldaEstado(0, 0, esNegra = true))
    celdas.add(CeldaEstado(0, 1, esNegra = true))
    celdas.add(CeldaEstado(0, 2, esNegra = false, numeroPista = 1, letraCorrecta = 'L'))
    celdas.add(CeldaEstado(0, 3, esNegra = false, numeroPista = 2, letraCorrecta = 'E'))
    celdas.add(CeldaEstado(0, 4, esNegra = false, letraCorrecta = 'I'))
    celdas.add(CeldaEstado(0, 5, esNegra = false, letraCorrecta = 'Z'))
    celdas.add(CeldaEstado(0, 6, esNegra = false, numeroPista = 3, letraCorrecta = 'E'))
    celdas.add(CeldaEstado(0, 7, esNegra = false, letraCorrecta = 'A'))

    // Fila 1
    celdas.add(CeldaEstado(1, 0, esNegra = true))
    celdas.add(CeldaEstado(1, 1, esNegra = true))
    celdas.add(CeldaEstado(1, 2, esNegra = true))
    celdas.add(CeldaEstado(1, 3, esNegra = false, letraCorrecta = 'S'))
    celdas.add(CeldaEstado(1, 4, esNegra = true))
    celdas.add(CeldaEstado(1, 5, esNegra = true))
    celdas.add(CeldaEstado(1, 6, esNegra = false, letraCorrecta = 'S'))
    celdas.add(CeldaEstado(1, 7, esNegra = true))

    // Fila 2
    celdas.add(CeldaEstado(2, 0, esNegra = true))
    celdas.add(CeldaEstado(2, 1, esNegra = false, numeroPista = 4, letraCorrecta = 'R'))
    celdas.add(CeldaEstado(2, 2, esNegra = true))
    celdas.add(CeldaEstado(2, 3, esNegra = false, letraCorrecta = 'T'))
    celdas.add(CeldaEstado(2, 4, esNegra = true))
    celdas.add(CeldaEstado(2, 5, esNegra = true))
    celdas.add(CeldaEstado(2, 6, esNegra = false, letraCorrecta = 'T'))
    celdas.add(CeldaEstado(2, 7, esNegra = true))

    // Fila 3
    celdas.add(CeldaEstado(3, 0, esNegra = false, numeroPista = 5, letraCorrecta = 'Z'))
    celdas.add(CeldaEstado(3, 1, esNegra = false, letraCorrecta = 'U'))
    celdas.add(CeldaEstado(3, 2, esNegra = false, letraCorrecta = 'T'))
    celdas.add(CeldaEstado(3, 3, esNegra = false, letraCorrecta = 'A'))
    celdas.add(CeldaEstado(3, 4, esNegra = false, letraCorrecta = 'B'))
    celdas.add(CeldaEstado(3, 5, esNegra = false, letraCorrecta = 'E'))
    celdas.add(CeldaEstado(3, 6, esNegra = false, letraCorrecta = 'A'))
    celdas.add(CeldaEstado(3, 7, esNegra = true))

    // Fila 4
    celdas.add(CeldaEstado(4, 0, esNegra = true))
    celdas.add(CeldaEstado(4, 1, esNegra = false, letraCorrecta = 'P'))
    celdas.add(CeldaEstado(4, 2, esNegra = true))
    celdas.add(CeldaEstado(4, 3, esNegra = false, letraCorrecta = 'L'))
    celdas.add(CeldaEstado(4, 4, esNegra = true))
    celdas.add(CeldaEstado(4, 5, esNegra = true))
    celdas.add(CeldaEstado(4, 6, esNegra = false, letraCorrecta = 'L'))
    celdas.add(CeldaEstado(4, 7, esNegra = true))

    // Fila 5
    celdas.add(CeldaEstado(5, 0, esNegra = true))
    celdas.add(CeldaEstado(5, 1, esNegra = false, letraCorrecta = 'E'))
    celdas.add(CeldaEstado(5, 2, esNegra = true))
    celdas.add(CeldaEstado(5, 3, esNegra = false, letraCorrecta = 'A'))
    celdas.add(CeldaEstado(5, 4, esNegra = true))
    celdas.add(CeldaEstado(5, 5, esNegra = true))
    celdas.add(CeldaEstado(5, 6, esNegra = false, letraCorrecta = 'A'))
    celdas.add(CeldaEstado(5, 7, esNegra = true))

    // Fila 6
    celdas.add(CeldaEstado(6, 0, esNegra = true))
    celdas.add(CeldaEstado(6, 1, esNegra = false, letraCorrecta = 'S'))
    celdas.add(CeldaEstado(6, 2, esNegra = true))
    celdas.add(CeldaEstado(6, 3, esNegra = false, letraCorrecta = 'G'))
    celdas.add(CeldaEstado(6, 4, esNegra = true))
    celdas.add(CeldaEstado(6, 5, esNegra = true))
    celdas.add(CeldaEstado(6, 6, esNegra = false, letraCorrecta = 'K'))
    celdas.add(CeldaEstado(6, 7, esNegra = true))

    // Fila 7
    celdas.add(CeldaEstado(7, 0, esNegra = true))
    celdas.add(CeldaEstado(7, 1, esNegra = false, letraCorrecta = 'T'))
    celdas.add(CeldaEstado(7, 2, esNegra = true))
    celdas.add(CeldaEstado(7, 3, esNegra = false, letraCorrecta = 'M'))
    celdas.add(CeldaEstado(7, 4, esNegra = true))
    celdas.add(CeldaEstado(7, 5, esNegra = true))
    celdas.add(CeldaEstado(7, 6, esNegra = false, letraCorrecta = 'T'))
    celdas.add(CeldaEstado(7, 7, esNegra = true))

    // Fila 8
    celdas.add(CeldaEstado(8, 0, esNegra = true))
    celdas.add(CeldaEstado(8, 1, esNegra = false, letraCorrecta = 'R'))
    celdas.add(CeldaEstado(8, 2, esNegra = true))
    celdas.add(CeldaEstado(8, 3, esNegra = false, letraCorrecta = 'I'))
    celdas.add(CeldaEstado(8, 4, esNegra = true))
    celdas.add(CeldaEstado(8, 5, esNegra = true))
    celdas.add(CeldaEstado(8, 6, esNegra = false, letraCorrecta = 'I'))
    celdas.add(CeldaEstado(8, 7, esNegra = true))

    // Fila 9
    celdas.add(CeldaEstado(9, 0, esNegra = true))
    celdas.add(CeldaEstado(9, 1, esNegra = false, letraCorrecta = 'E'))
    celdas.add(CeldaEstado(9, 2, esNegra = true))
    celdas.add(CeldaEstado(9, 3, esNegra = false, letraCorrecta = 'T'))
    celdas.add(CeldaEstado(9, 4, esNegra = true))
    celdas.add(CeldaEstado(9, 5, esNegra = true))
    celdas.add(CeldaEstado(9, 6, esNegra = false, letraCorrecta = 'T'))
    celdas.add(CeldaEstado(9, 7, esNegra = true))

    // Fila 10
    celdas.add(CeldaEstado(10, 0, esNegra = true))
    celdas.add(CeldaEstado(10, 1, esNegra = true))
    celdas.add(CeldaEstado(10, 2, esNegra = true))
    celdas.add(CeldaEstado(10, 3, esNegra = false, letraCorrecta = 'A'))
    celdas.add(CeldaEstado(10, 4, esNegra = true))
    celdas.add(CeldaEstado(10, 5, esNegra = true))
    celdas.add(CeldaEstado(10, 6, esNegra = false, letraCorrecta = 'A'))
    celdas.add(CeldaEstado(10, 7, esNegra = true))

    return celdas
}

// Funciones de verificación
fun verificarHorizontal(
    numeroPista: Int,
    filaInicio: Int,
    columnaInicio: Int,
    respuesta: String,
    celdas: MutableList<CeldaEstado>
) {
    var columna = columnaInicio
    for (letra in respuesta) {
        val index = celdas.indexOfFirst {
            it.fila == filaInicio && it.columna == columna
        }
        if (index != -1) {
            val celda = celdas[index]
            val coincide = celda.letraUsuario?.equals(letra, ignoreCase = true) ?: false
            celdas[index] = celda.copy(
                esCorrecta = celda.esCorrecta || coincide
            )
        }
        columna++
    }
}

fun verificarVertical(
    numeroPista: Int,
    filaInicio: Int,
    columnaInicio: Int,
    respuesta: String,
    celdas: MutableList<CeldaEstado>
) {
    var fila = filaInicio
    for (letra in respuesta) {
        val index = celdas.indexOfFirst {
            it.fila == fila && it.columna == columnaInicio
        }
        if (index != -1) {
            val celda = celdas[index]
            val esCorrecta = celda.letraUsuario?.equals(letra, ignoreCase = true) ?: false
            celdas[index] = celda.copy(esCorrecta = esCorrecta)
        }
        fila++
    }
}