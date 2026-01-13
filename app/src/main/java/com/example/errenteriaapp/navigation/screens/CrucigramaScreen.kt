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
    val mapaCeldas = mutableMapOf<Pair<Int, Int>, PalabraInfo>()

    init {
        // Construir mapa de celdas
        palabras.forEach { palabra ->
            for (i in 0 until palabra.longitud) {
                val fila = if (palabra.direccion == "HORIZONTAL") palabra.filaInicio else palabra.filaInicio + i
                val columna = if (palabra.direccion == "HORIZONTAL") palabra.columnaInicio + i else palabra.columnaInicio
                mapaCeldas[Pair(fila, columna)] = palabra
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

    fun encontrarCeldaAnterior(
        fila: Int,
        columna: Int,
        palabra: PalabraInfo
    ): Pair<Int, Int>? {
        val posActual =
            if (palabra.direccion == "HORIZONTAL")
                columna - palabra.columnaInicio
            else
                fila - palabra.filaInicio

        if (posActual <= 0) return null

        val nuevaPos = posActual - 1

        val newFila =
            if (palabra.direccion == "HORIZONTAL")
                palabra.filaInicio
            else
                palabra.filaInicio + nuevaPos

        val newCol =
            if (palabra.direccion == "HORIZONTAL")
                palabra.columnaInicio + nuevaPos
            else
                palabra.columnaInicio

        return Pair(newFila, newCol)
    }

    // Función para encontrar la palabra en una celda
    fun encontrarPalabraEnCelda(fila: Int, columna: Int): PalabraInfo? {
        return crucigramaEstado.mapaCeldas[Pair(fila, columna)]
    }

    // Función para encontrar la siguiente celda en la misma palabra
    fun encontrarSiguienteCeldaEnPalabraActual(fila: Int, columna: Int): Pair<Int, Int>? {
        val palabra = palabraActiva ?: return null

        val posicionActual = if (palabra.direccion == "HORIZONTAL") {
            columna - palabra.columnaInicio
        } else {
            fila - palabra.filaInicio
        }

        // Buscar siguiente celda vacía en la palabra
        for (i in posicionActual + 1 until palabra.longitud) {
            val nextFila = if (palabra.direccion == "HORIZONTAL") palabra.filaInicio else palabra.filaInicio + i
            val nextColumna = if (palabra.direccion == "HORIZONTAL") palabra.columnaInicio + i else palabra.columnaInicio
            val celda = obtenerCelda(nextFila, nextColumna)
            if (celda != null && !celda.esNegra && celda.letraUsuario == null) {
                return Pair(nextFila, nextColumna)
            }
        }

        // Si no hay después de la posición actual, buscar desde el inicio
        for (i in 0 until posicionActual) {
            val nextFila = if (palabra.direccion == "HORIZONTAL") palabra.filaInicio else palabra.filaInicio + i
            val nextColumna = if (palabra.direccion == "HORIZONTAL") palabra.columnaInicio + i else palabra.columnaInicio
            val celda = obtenerCelda(nextFila, nextColumna)
            if (celda != null && !celda.esNegra && celda.letraUsuario == null) {
                return Pair(nextFila, nextColumna)
            }
        }

        return null
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

    // Función para buscar cualquier celda vacía
    fun buscarYCualquierCeldaVacia() {
        coroutineScope.launch {
            delay(100)
            val celdaVacia = celdas.firstOrNull { !it.esNegra && it.letraUsuario == null && !it.esCorrecta }
            celdaVacia?.let {
                focusRequesters[Pair(it.fila, it.columna)]?.requestFocus()
            } ?: run {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        }
    }

    fun encontrarPalabraPorDireccion(
        fila: Int,
        columna: Int,
        direccion: String
    ): PalabraInfo? {
        return crucigramaEstado.palabras.firstOrNull { palabra ->
            palabra.direccion == direccion &&
                    when (direccion) {
                        "HORIZONTAL" ->
                            fila == palabra.filaInicio &&
                                    columna in palabra.columnaInicio until (palabra.columnaInicio + palabra.longitud)

                        "VERTICAL" ->
                            columna == palabra.columnaInicio &&
                                    fila in palabra.filaInicio until (palabra.filaInicio + palabra.longitud)

                        else -> false
                    }
        }
    }

    fun irASiguientePalabra(fila: Int, columna: Int) {
        val palabraActual = encontrarPalabraEnCelda(fila, columna) ?: return
        val indiceActual = crucigramaEstado.palabras.indexOf(palabraActual)
        val siguientePalabra = crucigramaEstado.palabras.getOrNull(indiceActual + 1)
        siguientePalabra?.let { palabra ->
            palabraActiva = palabra
            direccionActual = palabra.direccion
            // Mover foco a la primera celda de la siguiente palabra
            val primerFila = palabra.filaInicio
            val primerCol = palabra.columnaInicio
            focusRequesters[Pair(primerFila, primerCol)]?.requestFocus()
        }
    }

    fun moverFocoASiguienteCelda(filaActual: Int, columnaActual: Int) {
        coroutineScope.launch {
            delay(50)

            // PRIMERO: Intentar mover dentro de la palabra activa actual
            if (palabraActiva != null) {
                val siguiente = encontrarSiguienteCeldaEnPalabraActual(filaActual, columnaActual)
                if (siguiente != null) {
                    focusRequesters[siguiente]?.requestFocus()
                    return@launch
                } else {
                    // Si no hay más celdas vacías en la palabra activa
                    // Verificar si la palabra está completa
                    if (palabraActiva?.let { palabraEstaCompleta(it) } == true) {
                        // Palabra completada, buscar nueva palabra
                        palabraActiva = null
                    } else {
                        // Palabra no completa pero sin celdas vacías, mantener foco
                        focusRequesters[Pair(filaActual, columnaActual)]?.requestFocus()
                        return@launch
                    }
                }
            }

            // SEGUNDO: Si no hay palabra activa, buscar en dirección actual
            val palabraEnDireccion = encontrarPalabraPorDireccion(filaActual, columnaActual, direccionActual)
            if (palabraEnDireccion != null) {
                // Buscar siguiente celda en esta palabra
                val posicionActual = if (palabraEnDireccion.direccion == "HORIZONTAL") {
                    columnaActual - palabraEnDireccion.columnaInicio
                } else {
                    filaActual - palabraEnDireccion.filaInicio
                }

                for (i in posicionActual + 1 until palabraEnDireccion.longitud) {
                    val nextFila = if (palabraEnDireccion.direccion == "HORIZONTAL")
                        palabraEnDireccion.filaInicio
                    else
                        palabraEnDireccion.filaInicio + i

                    val nextColumna = if (palabraEnDireccion.direccion == "HORIZONTAL")
                        palabraEnDireccion.columnaInicio + i
                    else
                        palabraEnDireccion.columnaInicio

                    val celda = obtenerCelda(nextFila, nextColumna)
                    if (celda != null && !celda.esNegra && celda.letraUsuario == null) {
                        palabraActiva = palabraEnDireccion
                        focusRequesters[Pair(nextFila, nextColumna)]?.requestFocus()
                        return@launch
                    }
                }
            }

            // TERCERO: Si no se encontró nada, buscar cualquier celda vacía
            buscarYCualquierCeldaVacia()
        }
    }

    // Función para obtener dirección preferida al hacer clic
    fun obtenerDireccionPreferidaAlHacerClic(fila: Int, columna: Int): String {
        val tieneVertical = crucigramaEstado.palabras.any {
            it.direccion == "VERTICAL" &&
                    it.columnaInicio == columna &&
                    fila in it.filaInicio until (it.filaInicio + it.longitud)
        }

        val tieneHorizontal = crucigramaEstado.palabras.any {
            it.direccion == "HORIZONTAL" &&
                    it.filaInicio == fila &&
                    columna in it.columnaInicio until (it.columnaInicio + it.longitud)
        }

        // Si hay palabra activa, mantener su dirección
        if (palabraActiva != null) {
            // Verificar si la celda pertenece a la palabra activa
            val perteneceAPalabraActiva = when (palabraActiva!!.direccion) {
                "HORIZONTAL" ->
                    fila == palabraActiva!!.filaInicio &&
                            columna in palabraActiva!!.columnaInicio until (palabraActiva!!.columnaInicio + palabraActiva!!.longitud)

                "VERTICAL" ->
                    columna == palabraActiva!!.columnaInicio &&
                            fila in palabraActiva!!.filaInicio until (palabraActiva!!.filaInicio + palabraActiva!!.longitud)

                else -> false
            }

            if (perteneceAPalabraActiva) {
                return palabraActiva!!.direccion
            }
        }

        // Si no hay palabra activa o la celda no pertenece a ella
        // Priorizar la dirección actual si la celda tiene una palabra en esa dirección
        val tieneEnDireccionActual = when (direccionActual) {
            "HORIZONTAL" -> tieneHorizontal
            "VERTICAL" -> tieneVertical
            else -> false
        }

        if (tieneEnDireccionActual) {
            return direccionActual
        }

        // Si no, elegir la dirección disponible
        return when {
            tieneVertical -> "VERTICAL"
            tieneHorizontal -> "HORIZONTAL"
            else -> direccionActual
        }
    }

    fun borrarYRetroceder(fila: Int, columna: Int) {
        val palabra = encontrarPalabraEnCelda(fila, columna) ?: return

        // Posición relativa en la palabra
        val posActual = if (palabra.direccion == "HORIZONTAL") {
            columna - palabra.columnaInicio
        } else {
            fila - palabra.filaInicio
        }

        // Si la celda actual tiene letra, borrarla
        val celdaActual = obtenerCelda(fila, columna)
        if (celdaActual?.letraUsuario != null) {
            actualizarCelda(fila, columna) { it.copy(letraUsuario = null, esCorrecta = false) }
            focusRequesters[Pair(fila, columna)]?.requestFocus()
            return
        }

        // Si la celda actual está vacía, moverse hacia atrás hasta la primera letra de la palabra
        if (posActual > 0) {
            val nuevaPos = posActual - 1
            val newFila = if (palabra.direccion == "HORIZONTAL") palabra.filaInicio else palabra.filaInicio + nuevaPos
            val newCol = if (palabra.direccion == "HORIZONTAL") palabra.columnaInicio + nuevaPos else palabra.columnaInicio

            actualizarCelda(newFila, newCol) { it.copy(letraUsuario = null, esCorrecta = false) }
            focusRequesters[Pair(newFila, newCol)]?.requestFocus()
        }
    }

    // Función para manejar entrada de texto
    fun onLetraCambiada(fila: Int, columna: Int, nuevoCaracter: Char?) {
        actualizarCelda(fila, columna) { celda ->
            if (!celda.esNegra && !celda.esCorrecta) {
                celda.copy(
                    letraUsuario = nuevoCaracter?.uppercaseChar(),
                    esCorrecta = false
                )
            } else {
                celda
            }
        }

        // Si se ingresó una letra
        if (nuevoCaracter != null) {
            // Si no hay palabra activa, establecerla
            if (palabraActiva == null) {
                // Buscar una palabra en la dirección actual que contenga esta celda
                palabraActiva = encontrarPalabraPorDireccion(fila, columna, direccionActual)
                    ?: encontrarPalabraEnCelda(fila, columna)
            }

            // Mover a siguiente celda
            moverFocoASiguienteCelda(fila, columna)
        }
    }

    // Función para manejar Enter
    fun onEnterPressed(fila: Int, columna: Int) {
        irASiguientePalabra(fila, columna)
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
            direccionActual = direccionActual,
            onDireccionCambio = { nuevaDireccion ->
                // Al cambiar dirección manualmente, limpiar palabra activa
                direccionActual = nuevaDireccion
                palabraActiva = null
            },
            obtenerDireccionPreferidaAlHacerClic = { fila, columna ->
                obtenerDireccionPreferidaAlHacerClic(fila, columna)
            },
            onClickCelda = { fila, columna ->
                // Al hacer clic en una celda:
                // 1. Obtener dirección preferida para esta celda
                val nuevaDireccion = obtenerDireccionPreferidaAlHacerClic(fila, columna)
                if (nuevaDireccion != direccionActual) {
                    direccionActual = nuevaDireccion
                }

                // 2. Establecer palabra activa basada en la nueva dirección
                palabraActiva = encontrarPalabraPorDireccion(fila, columna, direccionActual)
                    ?: encontrarPalabraEnCelda(fila, columna)

                // 3. Enfocar la celda
                focusRequesters[Pair(fila, columna)]?.requestFocus()
            }
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Sección de pistas HORIZONTALES
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
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Pista 1
                PistaVisualItem(
                    numero = 1,
                    texto = "Duela milaka urte kobazuloetan bizi ziren gizakiak."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Pista 3
                PistaVisualItem(
                    numero = 3,
                    texto = "Kobazuloetako hormetan margotzen zituzten animaliak eta sinboloak."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Pista 5
                PistaVisualItem(
                    numero = 5,
                    texto = "Haria lantzeko erabilizen zuten tresna."
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sección de pistas VERTICALES
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
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Pista 2
                PistaVisualItem(
                    numero = 2,
                    texto = "Historiaurrea ikertzen duen zientzialaria."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Pista 4
                PistaVisualItem(
                    numero = 4,
                    texto = "Aizpitarteko kobazuloak dauden herria."
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

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

        Spacer(modifier = Modifier.height(8.dp))

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

        // Instrucción
        Text(
            text = "Haz clic en una palabra para activarla. La dirección se mantendrá hasta completarla.",
            fontSize = 12.sp,
            color = Color.Gray,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
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
    direccionActual: String,
    onDireccionCambio: (String) -> Unit,
    obtenerDireccionPreferidaAlHacerClic: (Int, Int) -> String,
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
                            direccionActual = direccionActual,
                            onDireccionCambio = onDireccionCambio,
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
    direccionActual: String,
    onDireccionCambio: (String) -> Unit,
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
        else -> Color.White
    }

    // Determinar color del texto
    val textColor = when {
        celda.esNegra -> Color.White
        celda.esCorrecta -> Color(0xFF2E7D32)
        else -> Color.Black
    }

    Box(
        modifier = Modifier
            .size(35.dp)
            .border(1.dp, Color.Gray)
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
                color = Color.Blue,
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
                    // BACKSPACE
                    if (newValue.text.isEmpty() && textFieldValue.text.isNotEmpty()) {
                        textFieldValue = TextFieldValue("", TextRange(0))
                        onBorrar()
                        return@BasicTextField
                    }

                    // UNA sola letra
                    if (newValue.text.length == 1) {
                        textFieldValue = newValue
                        onLetraCambiada(newValue.text[0])
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
                        // Al presionar Enter/Next, ir a siguiente palabra
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
fun PistaVisualItem(numero: Int, texto: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(
                    color = if (numero % 2 == 1) Color(0xFFC8E6C9) else Color(0xFFBBDEFB),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
                .border(
                    1.dp,
                    if (numero % 2 == 1) Color(0xFF4CAF50) else Color(0xFF2196F3),
                    androidx.compose.foundation.shape.CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$numero",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (numero % 2 == 1) Color(0xFF2E7D32) else Color(0xFF1565C0)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = texto,
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
    }
}

// Función para crear el estado inicial de las celdas (igual que antes)
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

// Funciones de verificación (igual que antes)
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