package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.errenteriaapp.components.ActiveWordIndicator
import com.example.errenteriaapp.components.CluesSection
import com.example.errenteriaapp.components.CrucigramaHeader
import com.example.errenteriaapp.components.GameResultDialogs
import com.example.errenteriaapp.components.InstructionsDialog
import com.example.errenteriaapp.components.PistaInfo
import com.example.errenteriaapp.components.TableroCrucigramaInteractivo
import com.example.errenteriaapp.components.VerificationIndicator
import com.example.errenteriaapp.components.VerifyButton
import com.example.errenteriaapp.components.generateFocusRequesters
import com.example.errenteriaapp.components.onBorrarYRetroceder
import com.example.errenteriaapp.components.onClickCelda
import com.example.errenteriaapp.components.onLetraCambiada
import com.example.errenteriaapp.database.viewModel.CrucigramaViewModel
import com.example.errenteriaapp.navigation.Routes
import com.example.errenteriaapp.progress.KokapenaProgressRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CrucigramaScreen(
    navController: NavController,
    userName: String?,
    viewModel: CrucigramaViewModel
) {
    val context = LocalContext.current
    val progressRepo = remember { KokapenaProgressRepository(context) }

    LaunchedEffect(userName) {
        userName?.let { viewModel.setUsuario(it) }
    }
    val celdas by viewModel.celdas
    val crucigramaEstado by viewModel.crucigramaEstado
    val verificacionRealizada by viewModel.verificacionRealizada
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val mostrarDialogoExito by viewModel.mostrarDialogoExito
    val palabraActiva by viewModel.palabraActiva
    val mostrarInstruccionesIniciales by viewModel.mostrarInstruccionesIniciales

    val focusRequesters = remember { generateFocusRequesters() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CrucigramaHeader()

        if (palabraActiva != null) {
            ActiveWordIndicator(
                palabraActiva = palabraActiva!!,
                onDeactivate = {
                    viewModel.desactivarPalabra()
                    focusManager.clearFocus()
                }
            )
        }

        TableroCrucigramaInteractivo(
            celdas = celdas,
            focusRequesters = focusRequesters,
            onLetraCambiada = { fila, columna, caracter ->
                onLetraCambiada(fila, columna, caracter, viewModel, coroutineScope, focusRequesters)
            },
            onBorrar = { fila, columna ->
                onBorrarYRetroceder(fila, columna, viewModel, coroutineScope, focusRequesters)
            },
            onEnterPressed = { fila, columna ->
                viewModel.moverFocoSiguienteDesdeEnter(
                    fila,
                    columna,
                    coroutineScope,
                    focusRequesters
                )
            },
            focusManager = focusManager,
            palabraActiva = palabraActiva,
            crucigramaEstado = crucigramaEstado,
            onClickCelda = { fila, columna ->
                onClickCelda(fila, columna, viewModel)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        CluesSection(
            pistasHorizontales = listOf(
                PistaInfo(1, "Kobazulo barruan agertzen diren gela desberdinak."),
                PistaInfo(5, "Egitura harritsu bertikal eta luzanga, sabaitik haitzuloko lurreraino hedatzen dena, edo bertan bermatzen dena.")
            ),
            pistasVerticales = listOf(
                PistaInfo(2, "Kono irregularraren formako kareharri formazio, haitzulo baten lurretik gorantz hazten dena."),
                PistaInfo(3, "Kobazulo baten sabaitik zintzirik dagoen egitura harritsua."),
                PistaInfo(4, "Nola deitzen dira kobazuloetan aurkitzen diren irudiak? lehen gizakiek egindakoak.")

            ),
            palabraActiva = palabraActiva,
            onActivateWord = { numero -> viewModel.activarPalabraPorNumero(numero) }
        )

        LaunchedEffect(palabraActiva) {
            palabraActiva?.let { palabra ->
                val posicion = Pair(palabra.filaInicio, palabra.columnaInicio)

                focusRequesters[posicion]?.requestFocus()
                keyboardController?.show()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        VerifyButton(
            onClick = {
                viewModel.verificarRespuestas()
                keyboardController?.hide()
            }
        )

        if (mostrarDialogoExito) {
            GameResultDialogs(
                showSuccess = true,
                showWrong = false,
                onDismissSuccess = {
                    viewModel.cerrarDialogoExito()
                },
                onDismissWrong = {},
                onSuccessButton = {
                    viewModel.cerrarDialogoExito()
                    progressRepo.markCompleted(Routes.CRUCIGRAMA_SCREEN)
                    navController.navigate(Routes.GPS_SCREEN)
                },
                onWrongButton = {}
            )
        }

        if (verificacionRealizada && !mostrarDialogoExito) {
            VerificationIndicator(celdas = celdas)
        }

        Spacer(modifier = Modifier.height(30.dp))

        if (mostrarInstruccionesIniciales) {
            InstructionsDialog(
                onDismiss = { viewModel.cerrarInstrucciones() }
            )
        }
    }
}