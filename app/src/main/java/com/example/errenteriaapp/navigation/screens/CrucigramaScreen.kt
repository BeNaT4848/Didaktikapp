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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import kotlinx.coroutines.launch

// Estado para cada celda del crucigrama
data class CeldaEstado(
    val fila: Int,
    val columna: Int,
    val esNegra: Boolean = false,
    val numeroPista: Int? = null,
    var letraUsuario: Char? = null, // Letra que introduce el usuario
    val letraCorrecta: Char? = null, // Letra correcta (respuesta)
    var esCorrecta: Boolean = false, // Si la letra del usuario es correcta
    var estaEditando: Boolean = false // Si la celda está siendo editada
)

// Definimos las respuestas correctas
class CrucigramaEstado {
    val respuestasHorizontales = mapOf(
        1 to "LEIZEA",    // Horizontal 1
        3 to "ESEA",      // Horizontal 3 (ejemplo)
        5 to "ZUTABEA"    // Horizontal 5
    )

    val respuestasVerticales = mapOf(
        2 to "ARKEOLOGOA",  // Vertical 2
        4 to "RENTERIA"     // Vertical 4
    )
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

        // Mover automáticamente a la siguiente celda (si hay letra)
        if (nuevoCaracter != null) {
            coroutineScope.launch {
                // Buscar siguiente celda editable en la misma dirección
                // Por simplicidad, vamos a la derecha
                val siguienteColumna = columna + 1
                val siguienteCelda = obtenerCelda(fila, siguienteColumna)

                if (siguienteCelda != null && !siguienteCelda.esNegra) {
                    // Aquí normalmente usaríamos un FocusRequester
                    // Pero para mantenerlo simple, solo actualizamos
                }
            }
        }
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
        verificarHorizontal(1, 0, 2, "LEIZEA", celdas)  // Horizontal 1: LEIZEA
        verificarHorizontal(3, 0, 6, "ESEA", celdas)     // Horizontal 3: ESEA
        verificarHorizontal(5, 3, 0, "ZUTABEA", celdas)  // Horizontal 5: ZUTABEA

        // Verificar respuestas verticales
        verificarVertical(2, 2, 1, "RENTERIA", celdas)   // Vertical 2: RENTERIA
        verificarVertical(4, 0, 2, "ARKELOGA", celdas)   // Vertical 4: ARKELOGA (modificado para coincidir)

        verificacionRealizada = true
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
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2196F3),
            modifier = Modifier.padding(bottom = 4.dp, top = 10.dp)
        )

        Text(
            text = "Aizpitarte-Hitz gurutzatuak",
            fontSize = 18.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Tablero del crucigrama interactivo
        TableroCrucigramaInteractivo(
            celdas = celdas,
            onLetraCambiada = { fila, columna, caracter ->
                onLetraCambiada(fila, columna, caracter)
            },
            focusManager = focusManager
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

        // Botón para limpiar/resetear
        Button(
            onClick = {
                // Resetear todas las celdas
                celdas.forEachIndexed { index, celda ->
                    if (!celda.esNegra) {
                        celdas[index] = celda.copy(
                            letraUsuario = null,
                            esCorrecta = false
                        )
                    }
                }
                verificacionRealizada = false
                keyboardController?.hide()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF757575),
                contentColor = Color.White
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "GARBITU",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Indicador de estado
        if (verificacionRealizada) {
            val celdasCorrectas = celdas.count { it.esCorrecta }
            val celdasTotales = celdas.count { !it.esNegra }

            Text(
                text = "Emaitza: $celdasCorrectas/$celdasTotales zuzen",
                fontSize = 14.sp,
                color = if (celdasCorrectas == celdasTotales) Color.Green else Color(0xFFFF9800),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Instrucción
        Text(
            text = "Idatzi hizkiak gelaxketan eta sakatu 'EGIAZTATU' zure erantzunak egiaztatzeko",
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
    onLetraCambiada: (Int, Int, Char?) -> Unit,
    focusManager: androidx.compose.ui.focus.FocusManager
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
                            onLetraCambiada = { caracter ->
                                onLetraCambiada(fila, columna, caracter)
                            },
                            focusManager = focusManager,
                            modifier = Modifier.combinedClickable(
                                onClick = {
                                    // No hacer nada en click simple
                                },
                                onDoubleClick = {
                                    // Limpiar celda al doble click
                                    if (!celda.esNegra && !celda.esCorrecta) {
                                        onLetraCambiada(fila, columna, null)
                                    }
                                }
                            )
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
    onLetraCambiada: (Char?) -> Unit,
    focusManager: androidx.compose.ui.focus.FocusManager,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
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
        celda.esCorrecta -> Color(0xFFC8E6C9) // Verde claro para correcto
        else -> Color.White
    }

    // Determinar color del texto
    val textColor = when {
        celda.esNegra -> Color.White
        celda.esCorrecta -> Color(0xFF2E7D32) // Verde oscuro
        else -> Color.Black
    }

    Box(
        modifier = modifier
            .size(35.dp)
            .border(1.dp, Color.Gray)
            .background(backgroundColor),
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
            // No mostrar nada o podría mostrar algo decorativo
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
                    // Solo permitir una letra
                    if (newValue.text.length <= 1) {
                        textFieldValue = newValue
                        val caracter = newValue.text.uppercase().getOrNull(0)
                        onLetraCambiada(caracter)

                        // Si se ingresó una letra, mover focus
                        if (caracter != null) {
                            focusManager.moveFocus(FocusDirection.Right)
                        }
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
                        focusManager.moveFocus(FocusDirection.Right)
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
                        // Placeholder si está vacío
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

// Función para crear el estado inicial de las celdas
fun crearCeldasEstado(): List<CeldaEstado> {
    val celdas = mutableListOf<CeldaEstado>()

    // Definir las letras correctas según tu crucigrama
    // Horizontal 1: LEIZEA (fila 0, columna 2-7)
    // Horizontal 3: ESEA (fila 0, columna 6-9? - ajustar según grid)
    // Horizontal 5: ZUTABEA (fila 3, columna 0-7)
    // Vertical 2: ARKEOLOGOA? (ajustar según grid)
    // Vertical 4: RENTERIA (fila 2-9, columna 1)

    // Fila 0
    celdas.add(CeldaEstado(0, 0, esNegra = true))
    celdas.add(CeldaEstado(0, 1, esNegra = true))
    celdas.add(CeldaEstado(0, 2, esNegra = false, numeroPista = 1, letraCorrecta = 'L'))
    celdas.add(CeldaEstado(0, 3, esNegra = false,  numeroPista = 2,letraCorrecta = 'E'))
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

    // Fila 5 (corregida - había un error en tu código original)
    celdas.add(CeldaEstado(5, 0, esNegra = true))
    celdas.add(CeldaEstado(5, 1, esNegra = false, letraCorrecta = 'E'))
    celdas.add(CeldaEstado(5, 2, esNegra = true)) // Esta estaba duplicada en tu código
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
            val esCorrecta = celda.letraUsuario?.equals(letra, ignoreCase = true) ?: false
            celdas[index] = celda.copy(esCorrecta = esCorrecta)
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