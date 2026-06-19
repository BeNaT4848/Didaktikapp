package com.example.errenteriaapp.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clip
import com.example.errenteriaapp.classes.*
/**
 * Gurutze-hitzaren taula interaktiboa erakusten du.
 * Zelda interaktiboak erakusten ditu testu-eremuekin.
 *
 * @param celdas Zelda guztien zerrenda
 * @param focusRequesters Zelda bakoitzaren fokus-eskakizunen mapa
 * @param onLetraCambiada Letra aldatzean deitzen den funtzioa
 * @param onBorrar Letra ezabatzeko deitzen den funtzioa
 * @param onEnterPressed Enter sakatzeko deitzen den funtzioa
 * @param focusManager Fokus-kudeatzailea
 * @param palabraActiva Hitz aktiboa (null bada, ez dago aktiborik)
 * @param crucigramaEstado Gurutze-hitzaren egoera orokorra
 * @param onClickCelda Zelda batean klik egitean deitzen den funtzioa
 */
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
    val colorScheme = MaterialTheme.colorScheme

    // Organizar celdas en grid
    val grid = Array(11) { Array<CeldaEstado?>(8) { null } }

    celdas.forEach { celda ->
        if (celda.fila < 11 && celda.columna < 8) {
            grid[celda.fila][celda.columna] = celda
        }
    }

    Column(
        modifier = Modifier
            .border(3.dp, colorScheme.outline)
            .background(colorScheme.surface)
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
/**
 * Zelda interaktibo bat erakusten du.
 * Testu-eremua erakusten du letrak sartzeko.
 *
 * @param celda Zelda-egoera
 * @param focusRequester Zelda honen fokus-eskakizuna
 * @param onLetraCambiada Letra aldatzean deitzen den funtzioa
 * @param onBorrar Letra ezabatzeko deitzen den funtzioa
 * @param onEnterPressed Enter sakatzeko deitzen den funtzioa
 * @param focusManager Fokus-kudeatzailea
 * @param estaActiva Zelda hitz aktiboari dagokion
 * @param onClickCelda Zelda batean klik egitean deitzen den funtzioa
 */
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
    val colorScheme = MaterialTheme.colorScheme

    var textFieldValue by remember(celda.letraUsuario) {
        mutableStateOf(
            TextFieldValue(
                text = celda.letraUsuario?.toString() ?: "",
                selection = TextRange(celda.letraUsuario?.toString()?.length ?: 0)
            )
        )
    }

    // Zelda editagarria den zehaztu
    val esEditable = !celda.esNegra && !celda.esCorrecta && estaActiva

    // Atzeko kolorea gaiaren arabera
    val backgroundColor = when {
        celda.esNegra -> Color.Black  // Beltza mantentzen dugu zelda beltzetarako
        celda.esCorrecta -> Color(0xFFC8E6C9) // tertiaryContainer erabili zuzenetarako
        estaActiva -> colorScheme.primaryContainer  // primaryContainer aktiboetarako
        else -> colorScheme.surfaceContainer  // surfaceContainer normaleetarako
    }

    // Testu-kolorea gaiaren arabera
    val textColor = when {
        celda.esNegra -> colorScheme.onSurface  // Testu argia beltzean
        celda.esCorrecta -> colorScheme.onTertiaryContainer  // Testua tertiaryContainer gainean
        estaActiva -> colorScheme.onPrimaryContainer  // Testua primaryContainer gainean
        else -> colorScheme.onSurfaceVariant  // Testu sotila normaleetan
    }

    // Ertz-kolorea gaiaren arabera
    val borderColor = when {
        celda.esNegra -> colorScheme.outlineVariant  // Ertz sotila beltzetarako
        estaActiva -> colorScheme.primary  // Ertz nagusia aktiboetarako
        celda.esCorrecta -> colorScheme.tertiary  // Ertz tertiary zuzenetarako
        else -> colorScheme.outlineVariant  // Ertz sotila normaleetarako
    }

    // Pistaren zenbakiaren kolorea gaiaren arabera
    val numeroPistaColor = when {
        estaActiva -> colorScheme.error  // Errorea aktiboetan nabarmentzeko
        !esEditable && !celda.esNegra -> colorScheme.outline  // Outline editagarriak ez direnetarako
        celda.esCorrecta -> colorScheme.onTertiaryContainer  // Testu-kolorea zuzenetan
        else -> colorScheme.primary  // Nagusia normaleetan
    }

    Box(
        modifier = Modifier
            .size(35.dp)
            .border(
                width = if (estaActiva) 2.dp else 1.dp,
                color = borderColor
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
                color = numeroPistaColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(2.dp)
            )
        }


        if (celda.esNegra) {
            // Zelda beltza - atzeko planoa bakarrik
        } else if (celda.esCorrecta) {
            // Zelda zuzena - letra erakutsi baina ez editatu
            Text(
                text = celda.letraUsuario?.toString() ?: "",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
        } else {
            DisposableEffect(focusRequester) {
                onDispose { }
            }

            BasicTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    // Aldaketak bakarrik editagarria bada
                    if (!esEditable) return@BasicTextField

                    // Letra bera bada, alde batera utzi
                    if (newValue.text == textFieldValue.text) return@BasicTextField

                    // BACKSPACE - testua hutsik denean
                    if (newValue.text.isEmpty()) {
                        onBorrar()
                        textFieldValue = TextFieldValue("", TextRange(0))
                        return@BasicTextField
                    }

                    // Letra berria idatzi
                    if (newValue.text.length == 1) {
                        val nuevaLetra = newValue.text.first().uppercaseChar()
                        if (celda.letraUsuario != nuevaLetra) {
                            textFieldValue = TextFieldValue(nuevaLetra.toString(), TextRange(1))
                            onLetraCambiada(nuevaLetra)
                        }
                    }
                    // Letra bat baino gehiago bada (testua itsatsi), lehenengoa hartu
                    else if (newValue.text.length > 1) {
                        val primeraLetra = newValue.text.first().uppercaseChar()
                        if (celda.letraUsuario != primeraLetra) {
                            textFieldValue = TextFieldValue(primeraLetra.toString(), TextRange(1))
                            onLetraCambiada(primeraLetra)
                        }
                    }
                },
                modifier = Modifier
                    .size(30.dp)
                    .focusRequester(focusRequester)
                    .clickable(
                        enabled = !celda.esNegra && !celda.esCorrecta,
                        onClick = onClickCelda
                    ),
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
                enabled = esEditable,
                readOnly = !esEditable,
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        innerTextField()
                        // Placeholder sutil cuando está vacío
                        if (textFieldValue.text.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                    .background(colorScheme.outlineVariant)
                            )
                        }
                    }
                }
            )
        }
    }
}